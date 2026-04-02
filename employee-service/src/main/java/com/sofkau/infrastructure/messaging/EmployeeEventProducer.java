package com.sofkau.infrastructure.messaging;

import com.sofkau.dto.EmployeeCreatedEvent;
import com.sofkau.dto.EmployeeUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEventProducer {
    private static final Logger log = LoggerFactory.getLogger(EmployeeEventProducer.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.employee:employee.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key.employee.created}")
    private String routingKeyCreated;

    @Value("${rabbitmq.routing.key.employee.updated}")
    private String routingKeyUpdated;

    public EmployeeEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("Publicando evento EmployeeCreatedEvent: {}", event);
        rabbitTemplate.convertAndSend(exchangeName, routingKeyCreated, event);
    }

    public void publishEmployeeUpdated(EmployeeUpdatedEvent event) {
        log.info("Publicando evento EmployeeUpdatedEvent: {}", event);
        rabbitTemplate.convertAndSend(exchangeName, routingKeyUpdated, event);
    }
}
