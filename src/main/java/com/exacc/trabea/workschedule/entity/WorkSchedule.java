package com.exacc.trabea.workschedule.entity;

import com.exacc.trabea.employee.entity.Employee;
import com.exacc.trabea.parttime.entity.PartTimeEmployee;
import com.exacc.trabea.shift.entity.Shift;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "work_schedules", uniqueConstraints = @UniqueConstraint(
        name = "uk_work_schedule_employee_date_shift",
        columnNames = {"part_time_employee_id", "work_date", "work_shift_id"}))
@Getter @Setter @NoArgsConstructor
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "part_time_employee_id", nullable = false)
    private PartTimeEmployee partTimeEmployee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_shift_id", nullable = false)
    private Shift workShift;
}
