package com.example.utils.rabbitmq;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class RabbitmqMessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendSocialToTax(String routingKey, Object message) {
        try {
            String messageId = UUID.randomUUID().toString();
            String jsonMessage = objectMapper.writeValueAsString(message);

            CorrelationData correlationData = new CorrelationData(messageId);

            rabbitTemplate.setConfirmCallback((correlation, ack, cause) -> {
                if (ack) {
                    log.info("消息确认到达交换机，messageId: {}", correlation.getId());
                } else {
                    log.error("消息未到达交换机，messageId: {}, cause: {}", correlation.getId(), cause);
                }
            });

            rabbitTemplate.setReturnsCallback(returned -> {
                log.error("消息未路由到队列，messageId: {}, exchange: {}, routingKey: {}, message: {}",
                        returned.getMessage().getMessageProperties().getMessageId(),
                        returned.getExchange(),
                        returned.getRoutingKey(),
                        new String(returned.getMessage().getBody()));
            });

            rabbitTemplate.convertAndSend(
                    "social.to.tax.exchange",
                    routingKey,
                    jsonMessage,
                    messagePostProcessor -> {
                        messagePostProcessor.getMessageProperties().setMessageId(messageId);
                        messagePostProcessor.getMessageProperties().setContentType("application/json");
                        return messagePostProcessor;
                    },
                    correlationData
            );

            log.info("社保消息已发送到交换机，routingKey: {}, messageId: {}", routingKey, messageId);

        } catch (Exception e) {
            log.error("发送社保消息失败", e);
            throw new RuntimeException("发送消息失败", e);
        }
    }

    public void sendTaxReplyToSocial(String routingKey, Object message) {
        try {
            String messageId = UUID.randomUUID().toString();
            String jsonMessage = objectMapper.writeValueAsString(message);

            CorrelationData correlationData = new CorrelationData(messageId);

            rabbitTemplate.convertAndSend(
                    "tax.to.social.exchange",
                    routingKey,
                    jsonMessage,
                    messagePostProcessor -> {
                        messagePostProcessor.getMessageProperties().setMessageId(messageId);
                        messagePostProcessor.getMessageProperties().setContentType("application/json");
                        return messagePostProcessor;
                    },
                    correlationData
            );

            log.info("税务回执已发送到交换机，routingKey: {}, messageId: {}", routingKey, messageId);

        } catch (Exception e) {
            log.error("发送税务回执失败", e);
            throw new RuntimeException("发送回执失败", e);
        }
    }
}