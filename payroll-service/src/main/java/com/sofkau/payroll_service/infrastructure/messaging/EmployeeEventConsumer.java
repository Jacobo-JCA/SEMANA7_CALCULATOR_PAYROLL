package com.sofkau.payroll_service.infrastructure.messaging;

import com.sofkau.payroll_service.dto.EmployeeEventDto;
import com.sofkau.payroll_service.entity.LocalEmployee;
import com.sofkau.payroll_service.repository.LocalEmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(EmployeeEventConsumer.class);
    private final LocalEmployeeRepository repository;

    public EmployeeEventConsumer(LocalEmployeeRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "${rabbitmq.queue.employee.events:employee.events.queue}")
    public void receiveEmployeeEvent(EmployeeEventDto event) {
        log.info("Recibido evento de empleado: {}", event);
        LocalEmployee employee = repository.findById(event.employeeId())
                .orElse(new LocalEmployee());

        employee.setId(event.employeeId());
        employee.setName(event.name());
        employee.setContractType(event.contractType());
        employee.setGrossSalary(event.grossSalary());

        repository.save(employee);
        log.info("Empleado {} sincronizado localmente con éxito.", event.employeeId());
    }
}