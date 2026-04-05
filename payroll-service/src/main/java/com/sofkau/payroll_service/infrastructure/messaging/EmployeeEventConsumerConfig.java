package com.sofkau.payroll_service.infrastructure.messaging;

import com.sofkau.payroll_service.dto.EmployeeEventDto;
import com.sofkau.payroll_service.entity.LocalEmployee;
import com.sofkau.payroll_service.repository.LocalEmployeeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class EmployeeEventConsumerConfig {
    private static final Logger log = LoggerFactory.getLogger(EmployeeEventConsumerConfig.class);
    private final LocalEmployeeRepository repository;

    public EmployeeEventConsumerConfig(LocalEmployeeRepository repository) {
        this.repository = repository;
    }

    @Bean
    public Consumer<EmployeeEventDto> employeeEvents() {
        return event -> {
            log.info("Recibido evento de empleado: {}", event);
            LocalEmployee employee = repository.findById(event.employeeId())
                    .orElse(new LocalEmployee());

            employee.setId(event.employeeId());
            employee.setName(event.name());
            employee.setContractType(event.contractType());
            employee.setGrossSalary(event.grossSalary());
            repository.save(employee);
            log.info("Empleado {} sincronizado localmente con éxito.", event.employeeId());
        };
    }
}
