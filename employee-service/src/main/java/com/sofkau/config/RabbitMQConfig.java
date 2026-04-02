package com.sofkau.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.exchange.employee:employee.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue.employee.created}")
    private String createdQueueName;

    @Value("${rabbitmq.queue.employee.updated}")
    private String updatedQueueName;

    @Value("${rabbitmq.routing.key.employee.created}")
    private String createdRoutingKey;

    @Value("${rabbitmq.routing.key.employee.updated}")
    private String updatedRoutingKey;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue createdQueue() {
        return new Queue(createdQueueName, true);
    }

    @Bean
    public Queue updatedQueue() {
        return new Queue(updatedQueueName, true);
    }

    @Bean
    public Binding createdBinding(Queue createdQueue, TopicExchange exchange) {
        return BindingBuilder.bind(createdQueue).to(exchange).with(createdRoutingKey);
    }

    @Bean
    public Binding updatedBinding(Queue updatedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(updatedQueue).to(exchange).with(updatedRoutingKey);
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
