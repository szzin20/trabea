package com.exacc.trabea.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserRoleId implements Serializable {

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "role_id")
    private Integer roleId;
}

