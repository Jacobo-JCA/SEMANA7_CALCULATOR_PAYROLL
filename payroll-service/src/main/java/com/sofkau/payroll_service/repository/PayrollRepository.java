package com.sofkau.payroll_service.repository;

import com.sofkau.payroll_service.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    Optional<Payroll> findFirstByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
}
