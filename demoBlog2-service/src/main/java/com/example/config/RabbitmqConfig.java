package com.example.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitmqConfig {

    public static final String SOCIAL_TO_TAX_EXCHANGE = "social.to.tax.exchange";
    public static final String TAX_TO_SOCIAL_EXCHANGE = "tax.to.social.exchange";
    
    public static final String SOCIAL_SEND_QUEUE = "social.send.queue";
    public static final String TAX_RECEIVE_QUEUE = "tax.receive.queue";
    public static final String TAX_REPLY_QUEUE = "tax.reply.queue";
    public static final String SOCIAL_RECEIVE_QUEUE = "social.receive.queue";
    
    public static final String SOCIAL_SEND_DLQ = "social.send.dlq";
    public static final String TAX_RECEIVE_DLQ = "tax.receive.dlq";
    public static final String TAX_REPLY_DLQ = "tax.reply.dlq";
    public static final String SOCIAL_RECEIVE_DLQ = "social.receive.dlq";
    
    public static final String SOCIAL_SEND_DLX_EXCHANGE = "social.send.dlx.exchange";
    public static final String TAX_RECEIVE_DLX_EXCHANGE = "tax.receive.dlx.exchange";
    public static final String TAX_REPLY_DLX_EXCHANGE = "tax.reply.dlx.exchange";
    public static final String SOCIAL_RECEIVE_DLX_EXCHANGE = "social.receive.dlx.exchange";

    @Bean
    public DirectExchange socialToTaxExchange() {
        return new DirectExchange(SOCIAL_TO_TAX_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange taxToSocialExchange() {
        return new DirectExchange(TAX_TO_SOCIAL_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange socialSendDlxExchange() {
        return new DirectExchange(SOCIAL_SEND_DLX_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange taxReceiveDlxExchange() {
        return new DirectExchange(TAX_RECEIVE_DLX_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange taxReplyDlxExchange() {
        return new DirectExchange(TAX_REPLY_DLX_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange socialReceiveDlxExchange() {
        return new DirectExchange(SOCIAL_RECEIVE_DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue socialSendQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", SOCIAL_SEND_DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", "social.send.dlx");
        args.put("x-message-ttl", 600000);
        return new Queue(SOCIAL_SEND_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue taxReceiveQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", TAX_RECEIVE_DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", "tax.receive.dlx");
        args.put("x-message-ttl", 600000);
        return new Queue(TAX_RECEIVE_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue taxReplyQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", TAX_REPLY_DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", "tax.reply.dlx");
        args.put("x-message-ttl", 600000);
        return new Queue(TAX_REPLY_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue socialReceiveQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", SOCIAL_RECEIVE_DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", "social.receive.dlx");
        args.put("x-message-ttl", 600000);
        return new Queue(SOCIAL_RECEIVE_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue socialSendDlq() {
        return new Queue(SOCIAL_SEND_DLQ, true);
    }

    @Bean
    public Queue taxReceiveDlq() {
        return new Queue(TAX_RECEIVE_DLQ, true);
    }

    @Bean
    public Queue taxReplyDlq() {
        return new Queue(TAX_REPLY_DLQ, true);
    }

    @Bean
    public Queue socialReceiveDlq() {
        return new Queue(SOCIAL_RECEIVE_DLQ, true);
    }

    @Bean
    public Binding socialSendBinding() {
        return BindingBuilder.bind(socialSendQueue())
                .to(socialToTaxExchange())
                .with("social.send");
    }

    @Bean
    public Binding taxReceiveBinding() {
        return BindingBuilder.bind(taxReceiveQueue())
                .to(socialToTaxExchange())
                .with("social.send");
    }

    @Bean
    public Binding taxReplyBinding() {
        return BindingBuilder.bind(taxReplyQueue())
                .to(taxToSocialExchange())
                .with("tax.reply");
    }

    @Bean
    public Binding socialReceiveBinding() {
        return BindingBuilder.bind(socialReceiveQueue())
                .to(taxToSocialExchange())
                .with("tax.reply");
    }

    @Bean
    public Binding socialSendDlqBinding() {
        return BindingBuilder.bind(socialSendDlq())
                .to(socialSendDlxExchange())
                .with("social.send.dlx");
    }

    @Bean
    public Binding taxReceiveDlqBinding() {
        return BindingBuilder.bind(taxReceiveDlq())
                .to(taxReceiveDlxExchange())
                .with("tax.receive.dlx");
    }

    @Bean
    public Binding taxReplyDlqBinding() {
        return BindingBuilder.bind(taxReplyDlq())
                .to(taxReplyDlxExchange())
                .with("tax.reply.dlx");
    }

    @Bean
    public Binding socialReceiveDlqBinding() {
        return BindingBuilder.bind(socialReceiveDlq())
                .to(socialReceiveDlxExchange())
                .with("social.receive.dlx");
    }
}
