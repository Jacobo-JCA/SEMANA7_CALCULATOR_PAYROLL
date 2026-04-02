package com.sofkau.repository;

import com.sofkau.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.sofkau.entity.Contract;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByName(String name);
    @Query("SELECT c FROM Contract c WHERE c.employee.id = :employeeId AND c.endDate IS NULL")
    Optional<Contract> findActiveContractByEmployeeId(@Param("employeeId") Long employeeId);
}
