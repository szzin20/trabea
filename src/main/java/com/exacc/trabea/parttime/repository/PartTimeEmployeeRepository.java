package com.exacc.trabea.parttime.repository;


import com.exacc.trabea.parttime.entity.PartTimeEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartTimeEmployeeRepository
        extends JpaRepository<PartTimeEmployee, Integer> {

    Optional<PartTimeEmployee> findByUser_WorkEmail(String workEmail);

    boolean existsByPersonalEmail(String personalEmail);

    boolean existsByPersonalPhoneNumber(String personalPhoneNumber);

    boolean existsByUser_WorkEmail(String workEmail);

    boolean existsByPersonalEmailAndIdNot(String personalEmail, Integer id);

    boolean existsByPersonalPhoneNumberAndIdNot(String personalPhoneNumber, Integer id);

    @Query("select p from PartTimeEmployee p where " +
            "(:name is null or lower(concat(p.firstName, ' ', p.lastName)) like lower(concat('%', :name, '%'))) and " +
            "(:active is null or (:active = true and p.resignDate is null) or (:active = false and p.resignDate is not null))")
    Page<PartTimeEmployee> search(@Param("name") String name, @Param("active") Boolean active, Pageable pageable);
}

