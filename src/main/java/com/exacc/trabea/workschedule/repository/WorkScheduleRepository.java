package com.exacc.trabea.workschedule.repository;

import com.exacc.trabea.workschedule.entity.WorkSchedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import com.exacc.trabea.shift.entity.Shift;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer> {
    boolean existsByPartTimeEmployee_IdAndIsApprovedTrue(Integer partTimerId);

    boolean existsByPartTimeEmployee_IdAndWorkDateAndWorkShift_Id(Integer employeeId, LocalDate workDate, Integer shiftId);

    long countByPartTimeEmployee_IdAndWorkDate(Integer employeeId, LocalDate workDate);

    @Query("select w.workShift from WorkSchedule w where w.partTimeEmployee.id = :employeeId and w.workDate = :workDate")
    List<Shift> findShiftsByEmployeeAndDate(@Param("employeeId") Integer employeeId,
                                            @Param("workDate") LocalDate workDate);

    @Query("select count(w) from WorkSchedule w where w.partTimeEmployee.id = :employeeId " +
            "and w.workDate >= :start and w.workDate < :end " +
            "and (w.isApproved is null or w.isApproved = true)")
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

    @Query(value = "select w from WorkSchedule w join fetch w.partTimeEmployee p join fetch w.workShift s " +
            "where w.workDate between :start and :end " +
            "and (:shiftId is null or s.id = :shiftId) " +
            "and (:name = '' or lower(concat(concat(p.firstName, ' '), p.lastName)) like lower(concat('%', :name, '%'))) " +
            "and w.isApproved is null " +
            "order by w.workDate asc, p.id asc, s.id asc",
            countQuery = "select count(w) from WorkSchedule w join w.partTimeEmployee p join w.workShift s " +
                    "where w.workDate between :start and :end " +
                    "and (:shiftId is null or s.id = :shiftId) " +
                    "and (:name = '' or lower(concat(concat(p.firstName, ' '), p.lastName)) like lower(concat('%', :name, '%'))) " +
                    "and w.isApproved is null")
    Page<WorkSchedule> searchPendingForReview(@Param("name") String name, @Param("shiftId") Integer shiftId,
                                              @Param("start") LocalDate start, @Param("end") LocalDate end,
                                              Pageable pageable);
}
