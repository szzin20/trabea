package com.exacc.trabea.workschedule.repository;

import com.exacc.trabea.workschedule.entity.WorkSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer> {
    boolean existsByPartTimeEmployee_IdAndWorkDateAndWorkShift_Id(Integer employeeId, LocalDate workDate, Integer shiftId);

    long countByPartTimeEmployee_IdAndWorkDate(Integer employeeId, LocalDate workDate);

    @Query("select count(w) from WorkSchedule w where w.partTimeEmployee.id = :employeeId " +
            "and w.workDate >= :start and w.workDate < :end")
    long countByEmployeeAndDateRange(@Param("employeeId") Integer employeeId,
                                     @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query(value = "select w from WorkSchedule w join fetch w.partTimeEmployee join fetch w.workShift " +
            "where w.partTimeEmployee.id = :employeeId",
            countQuery = "select count(w) from WorkSchedule w where w.partTimeEmployee.id = :employeeId")
    Page<WorkSchedule> findMine(@Param("employeeId") Integer employeeId, Pageable pageable);

    @Query("select w from WorkSchedule w join fetch w.partTimeEmployee join fetch w.workShift " +
            "where w.isApproved = true and w.workDate between :start and :end " +
            "order by w.workDate asc, w.partTimeEmployee.id asc")
    List<WorkSchedule> findApprovedForPeriod(@Param("start") LocalDate start,
                                             @Param("end") LocalDate end);

    @Query("select w from WorkSchedule w join fetch w.partTimeEmployee join fetch w.workShift " +
            "where w.isApproved is null order by w.workDate asc, w.partTimeEmployee.id asc")
    List<WorkSchedule> findAllPending();
}
