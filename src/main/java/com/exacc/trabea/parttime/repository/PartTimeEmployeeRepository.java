package com.exacc.trabea.parttime.repository;


import com.exacc.trabea.parttime.entity.PartTimeEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartTimeEmployeeRepository
        extends JpaRepository<PartTimeEmployee, Integer> {

    Optional<PartTimeEmployee> findByUser_WorkEmail(String workEmail);

    boolean existsByPersonalEmail(String personalEmail);

    boolean existsByPersonalPhoneNumber(String personalPhoneNumber);

    boolean existsByUser_WorkEmail(String workEmail);
}

