package com.exacc.trabea.employee.entity;

import com.exacc.trabea.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 25)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "work_email",
            nullable = false,
            unique = true
    )
    private User user;
}
