package com.recruitment.platform.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ── Exchange ───────────────────────────────────────────────────────────
    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    // ── cv.uploaded ────────────────────────────────────────────────────────
    @Value("${app.rabbitmq.queues.cv-uploaded.name}")
    private String cvUploadedQueue;

    @Value("${app.rabbitmq.queues.cv-uploaded.dlq}")
    private String cvUploadedDlq;

    @Value("${app.rabbitmq.queues.cv-uploaded.routing-key}")
    private String cvUploadedRoutingKey;

    // ── cv.extracted ───────────────────────────────────────────────────────
    @Value("${app.rabbitmq.queues.cv-extracted.name}")
    private String cvExtractedQueue;

    @Value("${app.rabbitmq.queues.cv-extracted.dlq}")
    private String cvExtractedDlq;

    @Value("${app.rabbitmq.queues.cv-extracted.routing-key}")
    private String cvExtractedRoutingKey;

    // ── Exchange ───────────────────────────────────────────────────────────

    @Bean
    public TopicExchange cvExchange() {
        return ExchangeBuilder
                .topicExchange(exchange)
                .durable(true)
                .build();
    }

    // ── DLQ exchange (single dead-letter exchange for all DLQs) ───────────

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange(exchange + ".dlx")
                .durable(true)
                .build();
    }

    // ── cv.uploaded queue + DLQ ────────────────────────────────────────────

    @Bean
    public Queue cvUploadedQueue() {
        return QueueBuilder
                .durable(cvUploadedQueue)
                .withArgument("x-dead-letter-exchange", exchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", cvUploadedDlq)
                .build();
    }

    @Bean
    public Queue cvUploadedDlq() {
        return QueueBuilder
                .durable(cvUploadedDlq)
                .build();
    }

    @Bean
    public Binding cvUploadedBinding() {
        return BindingBuilder
                .bind(cvUploadedQueue())
                .to(cvExchange())
                .with(cvUploadedRoutingKey);
    }

    @Bean
    public Binding cvUploadedDlqBinding() {
        return BindingBuilder
                .bind(cvUploadedDlq())
                .to(deadLetterExchange())
                .with(cvUploadedDlq);
    }

    // ── cv.extracted queue + DLQ ───────────────────────────────────────────

    @Bean
    public Queue cvExtractedQueue() {
        return QueueBuilder
                .durable(cvExtractedQueue)
                .withArgument("x-dead-letter-exchange", exchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", cvExtractedDlq)
                .build();
    }

    @Bean
    public Queue cvExtractedDlq() {
        return QueueBuilder
                .durable(cvExtractedDlq)
                .build();
    }

    @Bean
    public Binding cvExtractedBinding() {
        return BindingBuilder
                .bind(cvExtractedQueue())
                .to(cvExchange())
                .with(cvExtractedRoutingKey);
    }

    @Bean
    public Binding cvExtractedDlqBinding() {
        return BindingBuilder
                .bind(cvExtractedDlq())
                .to(deadLetterExchange())
                .with(cvExtractedDlq);
    }

    // ── Jackson message converter ──────────────────────────────────────────
    // Serialises/deserialises messages as JSON instead of Java serialisation.
    // Both producer and consumer must use the same converter.

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}