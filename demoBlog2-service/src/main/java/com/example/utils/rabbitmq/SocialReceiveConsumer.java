package com.example.utils.rabbitmq;


import com.example.entity.SocialTaxMsg;
import com.example.service.ISocialTaxMsgService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class SocialReceiveConsumer {

    @Autowired
    private ISocialTaxMsgService socialTaxMsgService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "social.receive.queue")
    public void handleTaxReply(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = message.getMessageProperties().getMessageId();

        try {
            String body = new String(message.getBody(), "UTF-8");
            log.info("社保局收到税务回执，messageId: {}, content: {}", messageId, body);

            JsonNode jsonNode = objectMapper.readTree(body);
            String dataNo = jsonNode.get("dataNo").asText();

            SocialTaxMsg socialMsg = socialTaxMsgService.lambdaQuery()
                    .eq(SocialTaxMsg::getDataNo, dataNo)
                    .one();

            if (socialMsg != null) {
                Integer processResult = jsonNode.get("processResult").asInt();
                String processMsg = jsonNode.get("processMsg").asText();

                socialMsg.setReceiveStatus(processResult == 0 ? (byte) 1 : (byte) 2);
                socialMsg.setReceiveMsg(processMsg);
                socialMsg.setReceiveTime(LocalDateTime.now());
                socialMsg.setTaxSerialNo(jsonNode.has("taxSerialNo") ? jsonNode.get("taxSerialNo").asText() : null);
                socialMsg.setUpdateTime(LocalDateTime.now());

                socialTaxMsgService.updateById(socialMsg);

                log.info("社保消息状态已更新，dataNo: {}, 结果: {}", dataNo, processResult);
            } else {
                log.warn("未找到对应的社保消息，dataNo: {}", dataNo);
            }

            channel.basicAck(deliveryTag, false);
            log.info("税务回执处理成功，messageId: {}", messageId);

        } catch (Exception e) {
            log.error("处理税务回执失败，messageId: {}", messageId, e);

            try {
                channel.basicNack(deliveryTag, false, false);
                log.warn("回执消息被拒绝，将进入死信队列，messageId: {}", messageId);
            } catch (IOException ioException) {
                log.error("拒绝消息失败", ioException);
            }
        }
    }

    @RabbitListener(queues = "social.receive.dlq")
    public void handleDeadLetter(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = message.getMessageProperties().getMessageId();

        try {
            String body = new String(message.getBody(), "UTF-8");
            log.error("收到死信回执消息，需要人工干预，messageId: {}, content: {}", messageId, body);

            channel.basicAck(deliveryTag, false);
            log.info("死信回执消息已确认，messageId: {}", messageId);

        } catch (Exception e) {
            log.error("处理死信回执消息失败", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
