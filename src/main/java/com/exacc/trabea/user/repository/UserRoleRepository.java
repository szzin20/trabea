package com.exacc.trabea.user.repository;


import com.exacc.trabea.user.entity.UserRole;
import com.exacc.trabea.user.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface UserRoleRepository
        extends JpaRepository<UserRole, UserRoleId> {

    @EntityGraph(attributePaths = {"role"})
    List<UserRole> findAllByUser_WorkEmail(String workEmail);

    boolean existsByUser_WorkEmailAndRole_Name(
            String workEmail,
            String roleName
    );

    void deleteByUser_WorkEmailAndRole_Name(
            String workEmail,
            String roleName
    );
}

