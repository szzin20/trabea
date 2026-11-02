package com.exacc.trabea.user.repository;

import com.exacc.trabea.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByWorkEmail(String workEmail);

    boolean existsByWorkEmail(String workEmail);
}
