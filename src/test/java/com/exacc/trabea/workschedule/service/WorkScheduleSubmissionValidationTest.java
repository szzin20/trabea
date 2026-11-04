package com.exacc.trabea.workschedule.service;

import com.exacc.trabea.common.BusinessRuleException;
import com.exacc.trabea.parttime.entity.PartTimeEmployee;
import com.exacc.trabea.parttime.repository.PartTimeEmployeeRepository;
import com.exacc.trabea.shift.entity.Shift;
import com.exacc.trabea.shift.repository.ShiftRepository;
import com.exacc.trabea.workschedule.dto.CreateWorkScheduleRequest;
import com.exacc.trabea.workschedule.mapper.WorkScheduleMapper;
import com.exacc.trabea.workschedule.repository.WorkScheduleRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkScheduleSubmissionValidationTest {
    private static final LocalDate DATE = LocalDate.of(2026, 11, 4);

    @Test void oneShiftAndConsecutivePairsAreAllowed() {
        assertDoesNotThrow(() -> submit(List.of(), 1, 0, 0));
        assertDoesNotThrow(() -> submit(List.of(1), 2, 1, 0));
        assertDoesNotThrow(() -> submit(List.of(2), 3, 1, 0));
        assertDoesNotThrow(() -> submit(List.of(3), 4, 1, 0));
    }

    @Test void nonConsecutivePairsAreRejected() {
        for (var pair : List.of(List.of(1, 3), List.of(1, 4), List.of(2, 4))) {
            var exception = assertThrows(BusinessRuleException.class,
                    () -> submit(List.of(pair.get(0)), pair.get(1), 1, 0));
            assertEquals(400, exception.getStatus().value());
        }
    }

    @Test void sameShiftAndThirdShiftAreRejected() {
        assertThrows(BusinessRuleException.class, () -> submit(List.of(2), 2, 1, 0, true));
        assertThrows(BusinessRuleException.class, () -> submit(List.of(1, 2), 3, 2, 0));
    }

    @Test void weeklyMaximumRemainsEnforced() {
        assertDoesNotThrow(() -> submit(List.of(), 1, 0, 4));
        assertThrows(BusinessRuleException.class, () -> submit(List.of(), 1, 0, 5));
    }

    private void submit(List<Integer> existingShiftIds, int requestedShiftId, long dailyCount, long weeklyCount) {
        submit(existingShiftIds, requestedShiftId, dailyCount, weeklyCount, false);
    }

    private void submit(List<Integer> existingShiftIds, int requestedShiftId, long dailyCount,
                        long weeklyCount, boolean duplicate) {
        var schedules = mock(WorkScheduleRepository.class);
        var employees = mock(PartTimeEmployeeRepository.class);
        var shifts = mock(ShiftRepository.class);
        var mapper = mock(WorkScheduleMapper.class);
        var employee = mock(PartTimeEmployee.class);
        var shift = new Shift();
        shift.setId(requestedShiftId);
        shift.setStartTime(java.time.LocalTime.of(8 + (requestedShiftId - 1) * 4, 0));
        shift.setEndTime(shift.getStartTime().plusHours(4));
        when(employee.getId()).thenReturn(9);
        when(employees.findByUser_WorkEmailForUpdate("parttimer@example.com")).thenReturn(Optional.of(employee));
        when(shifts.findById(requestedShiftId)).thenReturn(Optional.of(shift));
        when(schedules.existsByPartTimeEmployee_IdAndWorkDateAndWorkShift_Id(9, DATE, requestedShiftId))
                .thenReturn(duplicate);
        when(schedules.countByPartTimeEmployee_IdAndWorkDate(9, DATE)).thenReturn(dailyCount);
        when(schedules.findShiftsByEmployeeAndDate(9, DATE)).thenReturn(existingShiftIds.stream()
                .map(this::shiftForId).toList());
        when(schedules.countByEmployeeAndDateRange(eq(9), any(), any())).thenReturn(weeklyCount);
        var service = new WorkScheduleService(schedules, employees, shifts, mapper,
                Clock.fixed(Instant.parse("2026-11-04T00:00:00Z"), ZoneId.of("Asia/Jakarta")));

        service.createWorkSchedule("parttimer@example.com", new CreateWorkScheduleRequest(DATE, requestedShiftId));
    }

    private Shift shiftForId(Integer id) {
        var shift = new Shift();
        shift.setId(id);
        shift.setStartTime(java.time.LocalTime.of(8 + (id - 1) * 4, 0));
        shift.setEndTime(shift.getStartTime().plusHours(4));
        return shift;
    }
}
