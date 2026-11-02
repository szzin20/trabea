package com.exacc.trabea.parttime.entity;

import com.exacc.trabea.parttime.EducationLevel;
import com.exacc.trabea.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "part_time_employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartTimeEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 25)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "personal_email", nullable = false, unique = true, length = 100)
    private String personalEmail;

    @Column(name = "personal_phone_number", nullable = false, unique = true, length = 20)
    private String personalPhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_education", nullable = false, length = 50)
    private EducationLevel lastEducation;

    @Enumerated(EnumType.STRING)
    @Column(name = "on_going_education", length = 50)
    private EducationLevel onGoingEducation;

    @Column(name = "join_date", nullable = false, updatable = false)
    private LocalDateTime joinDate;

    @Column(name = "resign_date")
    private LocalDateTime resignDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "work_email",
            nullable = false,
            unique = true
    )
    private User user;

    @PrePersist
    protected void onCreate() {
        if (joinDate == null) {
            joinDate = LocalDateTime.now();
        }
    }
}

