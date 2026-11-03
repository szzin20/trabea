package com.exacc.trabea.employee.repository;

import com.exacc.trabea.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByUser_WorkEmail(String workEmail);

    boolean existsByUser_WorkEmail(String workEmail);

}
