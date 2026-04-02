package com.sofkau.payroll_service.repository;

import com.sofkau.payroll_service.entity.LocalEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalEmployeeRepository extends JpaRepository<LocalEmployee, Long> {
}
