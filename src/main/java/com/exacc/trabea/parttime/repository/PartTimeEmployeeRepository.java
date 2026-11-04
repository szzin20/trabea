package com.exacc.trabea.parttime.repository;


import com.exacc.trabea.parttime.entity.PartTimeEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface PartTimeEmployeeRepository
        extends JpaRepository<PartTimeEmployee, Integer> {

    Optional<PartTimeEmployee> findByUser_WorkEmail(String workEmail);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PartTimeEmployee p where p.user.workEmail = :workEmail")
    Optional<PartTimeEmployee> findByUser_WorkEmailForUpdate(@Param("workEmail") String workEmail);

    boolean existsByPersonalEmail(String personalEmail);

    boolean existsByPersonalPhoneNumber(String personalPhoneNumber);

    boolean existsByUser_WorkEmail(String workEmail);

    boolean existsByPersonalEmailAndIdNot(String personalEmail, Integer id);

    boolean existsByPersonalPhoneNumberAndIdNot(String personalPhoneNumber, Integer id);

    @Query("select p from PartTimeEmployee p " +
            "where lower(concat(p.firstName, ' ', p.lastName)) like lower(concat('%', :name, '%'))")
    Page<PartTimeEmployee> search(@Param("name") String name, Pageable pageable);}

