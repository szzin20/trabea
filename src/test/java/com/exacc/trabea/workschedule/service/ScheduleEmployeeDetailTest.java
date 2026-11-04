package com.exacc.trabea.workschedule.service;

import com.exacc.trabea.common.ResourceNotFoundException;
import com.exacc.trabea.parttime.entity.PartTimeEmployee;
import com.exacc.trabea.parttime.repository.PartTimeEmployeeRepository;
import com.exacc.trabea.shift.repository.ShiftRepository;
import com.exacc.trabea.workschedule.mapper.WorkScheduleMapper;
import com.exacc.trabea.workschedule.repository.WorkScheduleRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ScheduleEmployeeDetailTest {
    private final PartTimeEmployeeRepository employees = mock(PartTimeEmployeeRepository.class);
    private final WorkScheduleRepository schedules = mock(WorkScheduleRepository.class);
    private final WorkScheduleService service = new WorkScheduleService(schedules, employees,
            mock(ShiftRepository.class), mock(WorkScheduleMapper.class), Clock.system(ZoneId.of("Asia/Jakarta")));

    @Test void employeeMustExistAndHaveApprovedSchedule() {
        when(employees.findById(5)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getScheduleEmployeeDetail(5));

        when(employees.findById(6)).thenReturn(Optional.of(mock(PartTimeEmployee.class)));
        when(schedules.existsByPartTimeEmployee_IdAndIsApprovedTrue(6)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.getScheduleEmployeeDetail(6));
    }
}
