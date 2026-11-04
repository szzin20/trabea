package com.exacc.trabea.workschedule.service;

import com.exacc.trabea.parttime.repository.PartTimeEmployeeRepository;
import com.exacc.trabea.shift.repository.ShiftRepository;
import com.exacc.trabea.workschedule.mapper.WorkScheduleMapper;
import com.exacc.trabea.workschedule.repository.WorkScheduleRepository;
import com.exacc.trabea.workschedule.entity.WorkSchedule;
import com.exacc.trabea.common.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WorkScheduleServiceTest {
    private final WorkScheduleRepository schedules = mock(WorkScheduleRepository.class);
    private final WorkScheduleService service = new WorkScheduleService(
            schedules, mock(PartTimeEmployeeRepository.class), mock(ShiftRepository.class), mock(WorkScheduleMapper.class));

    @Test void defaultsToCurrentMondayThroughFriday() {
        var monday = LocalDate.now(java.time.ZoneId.of("Asia/Jakarta"))
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        when(schedules.findApprovedForPeriod(monday, monday.plusDays(4))).thenReturn(List.of());

        service.getSchedulePage(null);

        verify(schedules).findApprovedForPeriod(monday, monday.plusDays(4));
    }

    @Test void nextSelectsFollowingMondayThroughFriday() {
        var monday = LocalDate.now(java.time.ZoneId.of("Asia/Jakarta"))
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(1);
        when(schedules.findApprovedForPeriod(monday, monday.plusDays(4))).thenReturn(List.of());

        service.getSchedulePage("next");

        verify(schedules).findApprovedForPeriod(monday, monday.plusDays(4));
    }

    @Test void rejectsUnsupportedWeek() {
        assertThrows(RuntimeException.class, () -> service.getSchedulePage("previous"));
        verifyNoInteractions(schedules);
    }

    @Test void pendingListComesFromPendingRepositoryQuery() {
        when(schedules.findAllPending()).thenReturn(List.of());
        service.getPendingSchedules();
        verify(schedules).findAllPending();
    }

    @Test void approvesOnlyPendingSchedule() {
        var schedule = new WorkSchedule();
        when(schedules.findById(3)).thenReturn(Optional.of(schedule));
        when(schedules.save(schedule)).thenReturn(schedule);

        service.approveSchedule(3);

        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, schedule.getIsApproved());
        verify(schedules).save(schedule);
    }

    @Test void rejectsOnlyPendingSchedule() {
        var schedule = new WorkSchedule();
        when(schedules.findById(4)).thenReturn(Optional.of(schedule));
        when(schedules.save(schedule)).thenReturn(schedule);

        service.rejectSchedule(4);

        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, schedule.getIsApproved());
        verify(schedules).save(schedule);
    }

    @Test void preventsReprocessingAndReturnsNotFoundForMissingSchedule() {
        var approved = new WorkSchedule();
        approved.setIsApproved(true);
        when(schedules.findById(5)).thenReturn(Optional.of(approved));
        when(schedules.findById(6)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.approveSchedule(5));
        assertThrows(ResourceNotFoundException.class, () -> service.rejectSchedule(6));
        verify(schedules, never()).save(approved);
    }
}
