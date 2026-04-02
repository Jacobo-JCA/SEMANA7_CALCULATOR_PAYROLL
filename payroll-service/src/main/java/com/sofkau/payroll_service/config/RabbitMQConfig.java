package com.sofkau.payroll_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.exchange.employee:employee.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue.employee.events:employee.events.queue}")
    private String queueName;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue eventsQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding eventsBinding(Queue eventsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(eventsQueue).to(exchange).with("employee.#");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
