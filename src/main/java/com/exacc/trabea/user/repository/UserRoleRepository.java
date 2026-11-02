package com.exacc.trabea.user.repository;


import com.exacc.trabea.user.entity.UserRole;
import com.exacc.trabea.user.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository
        extends JpaRepository<UserRole, UserRoleId> {

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

