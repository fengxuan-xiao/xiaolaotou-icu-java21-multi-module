package com.example.utils.rabbitmq;


import com.example.entity.TaxReceiveMsg;
import com.example.service.ITaxReceiveMsgService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
public class TaxReceiveConsumer {

    @Autowired
    private ITaxReceiveMsgService taxReceiveMsgService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "tax.receive.queue")
    public void handleSocialMessage(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = message.getMessageProperties().getMessageId();

        try {
            String body = new String(message.getBody(), "UTF-8");
            log.info("税务局收到社保消息，messageId: {}, content: {}", messageId, body);

            JsonNode jsonNode = objectMapper.readTree(body);

            TaxReceiveMsg taxMsg = new TaxReceiveMsg();
            taxMsg.setDataNo(jsonNode.get("dataNo").asText());
            taxMsg.setMsgId(messageId);
            taxMsg.setUnitName(jsonNode.get("unitName").asText());
            taxMsg.setUnitCode(jsonNode.get("unitCode").asText());
            taxMsg.setFeePeriod(jsonNode.get("feePeriod").asText());
            taxMsg.setFeePeriodRef(jsonNode.has("feePeriodRef") ? jsonNode.get("feePeriodRef").asText() : null);
            taxMsg.setAreaCode(jsonNode.get("areaCode").asText());
            taxMsg.setAmount(new BigDecimal(jsonNode.get("amount").asText()));
            taxMsg.setTaxSerialNo(jsonNode.has("taxSerialNo") ? jsonNode.get("taxSerialNo").asText() : null);
            taxMsg.setReceiveStatus((byte) 0);
            taxMsg.setIsRepeat((byte) 0);
            taxMsg.setStatus((byte) 1);
            taxMsg.setCreateTime(LocalDateTime.now());
            taxMsg.setUpdateTime(LocalDateTime.now());
            taxMsg.setIsDeleted((byte) 0);

            taxReceiveMsgService.save(taxMsg);

            channel.basicAck(deliveryTag, false);
            log.info("税务消息处理成功并入库，messageId: {}, dataNo: {}", messageId, taxMsg.getDataNo());

        } catch (Exception e) {
            log.error("处理税务消息失败，messageId: {}", messageId, e);

            try {
                channel.basicNack(deliveryTag, false, false);
                log.warn("消息被拒绝，将进入死信队列，messageId: {}", messageId);
            } catch (IOException ioException) {
                log.error("拒绝消息失败", ioException);
            }
        }
    }

    @RabbitListener(queues = "tax.receive.dlq")
    public void handleDeadLetter(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = message.getMessageProperties().getMessageId();

        try {
            String body = new String(message.getBody(), "UTF-8");
            log.error("收到死信消息，需要人工干预，messageId: {}, content: {}", messageId, body);

            channel.basicAck(deliveryTag, false);
            log.info("死信消息已确认，messageId: {}", messageId);

        } catch (Exception e) {
            log.error("处理死信消息失败", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
