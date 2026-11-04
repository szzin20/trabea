package com.exacc.trabea.workschedule.service;

import com.exacc.trabea.common.BusinessRuleException;
import com.exacc.trabea.common.ResourceNotFoundException;
import com.exacc.trabea.parttime.repository.PartTimeEmployeeRepository;
import com.exacc.trabea.shift.repository.ShiftRepository;
import com.exacc.trabea.workschedule.dto.CreateWorkScheduleRequest;
import com.exacc.trabea.workschedule.dto.WorkScheduleResponse;
import com.exacc.trabea.workschedule.dto.SchedulePageItem;
import com.exacc.trabea.workschedule.dto.WorkScheduleApprovalResponse;
import com.exacc.trabea.workschedule.entity.WorkSchedule;
import com.exacc.trabea.workschedule.mapper.WorkScheduleMapper;
import com.exacc.trabea.workschedule.repository.WorkScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {
    private static final ZoneId PROJECT_ZONE = ZoneId.of("Asia/Jakarta");
    private final WorkScheduleRepository schedules;
    private final PartTimeEmployeeRepository employees;
    private final ShiftRepository shifts;
    private final WorkScheduleMapper mapper;

    @Transactional
    public WorkScheduleResponse createWorkSchedule(String workEmail, CreateWorkScheduleRequest request) {
        var employee = employees.findByUser_WorkEmail(workEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Part-Time Employee tidak ditemukan", HttpStatus.NOT_FOUND));
        if (employee.getResignDate() != null)
            throw new BusinessRuleException("Pekerja yang sudah resign tidak dapat mengajukan shift", HttpStatus.FORBIDDEN);

        LocalDate date = request.workDate();
        validatePeriod(date);
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
            throw new BusinessRuleException("Pengajuan hanya diperbolehkan pada hari Senin sampai Jumat", HttpStatus.BAD_REQUEST);

        var shift = shifts.findById(request.workShiftId()).orElseThrow(() ->
                new ResourceNotFoundException("Shift dengan ID " + request.workShiftId() + " tidak ditemukan", HttpStatus.NOT_FOUND));
        if (schedules.existsByPartTimeEmployee_IdAndWorkDateAndWorkShift_Id(employee.getId(), date, shift.getId()))
            throw new BusinessRuleException("Shift tersebut sudah diajukan pada tanggal yang sama", HttpStatus.CONFLICT);
        if (schedules.countByPartTimeEmployee_IdAndWorkDate(employee.getId(), date) >= 2)
            throw new BusinessRuleException("Maksimal 2 shift dalam satu hari", HttpStatus.CONFLICT);

        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        if (schedules.countByEmployeeAndDateRange(employee.getId(), weekStart, weekStart.plusWeeks(1)) >= 5)
            throw new BusinessRuleException("Maksimal 5 shift atau 20 jam dalam satu minggu", HttpStatus.CONFLICT);

        WorkSchedule schedule = new WorkSchedule();
        schedule.setPartTimeEmployee(employee);
        schedule.setWorkShift(shift);
        schedule.setWorkDate(date);
        schedule.setIsApproved(null);
        schedule.setManager(null);
        return mapper.toResponse(schedules.save(schedule));
    }

    private void validatePeriod(LocalDate date) {
        LocalDate thisMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = thisMonday.plusWeeks(2);
        if (date.isBefore(thisMonday) || !date.isBefore(end))
            throw new BusinessRuleException("Tanggal pengajuan harus pada minggu berjalan atau minggu berikutnya", HttpStatus.BAD_REQUEST);
    }

    @Transactional(readOnly = true)
    public Page<WorkScheduleResponse> getMyWorkSchedules(String workEmail, int page, int size) {
        if (page < 0 || size < 1 || size > 100)
            throw new BusinessRuleException("Page harus minimal 0 dan size antara 1 sampai 100", HttpStatus.BAD_REQUEST);
        var employee = employees.findByUser_WorkEmail(workEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Part-Time Employee tidak ditemukan", HttpStatus.NOT_FOUND));
        return schedules.findMine(employee.getId(), PageRequest.of(page, size,
                        Sort.by(Sort.Order.asc("workDate"), Sort.Order.asc("workShift.id"))))
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<SchedulePageItem> getSchedulePage(String week) {
        String selectedWeek = week == null ? "current" : week.trim().toLowerCase(java.util.Locale.ROOT);
        if (!selectedWeek.equals("current") && !selectedWeek.equals("next")) {
            throw new BusinessRuleException("Parameter week harus bernilai current atau next", HttpStatus.BAD_REQUEST);
        }
        LocalDate today = LocalDate.now(PROJECT_ZONE);
        LocalDate start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        if (selectedWeek.equals("next")) start = start.plusWeeks(1);
        LocalDate end = start.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        return schedules.findApprovedForPeriod(start, end).stream()
                .map(mapper::toSchedulePageItem).toList();
    }

    @Transactional(readOnly = true)
    public List<WorkScheduleApprovalResponse> getPendingSchedules() {
        return schedules.findAllPending().stream().map(mapper::toApprovalResponse).toList();
    }

    @Transactional
    public WorkScheduleApprovalResponse approveSchedule(Integer id) {
        return processApproval(id, true);
    }

    @Transactional
    public WorkScheduleApprovalResponse rejectSchedule(Integer id) {
        return processApproval(id, false);
    }

    private WorkScheduleApprovalResponse processApproval(Integer id, boolean approved) {
        WorkSchedule schedule = schedules.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Pengajuan jadwal dengan ID " + id + " tidak ditemukan", HttpStatus.NOT_FOUND));
        if (schedule.getIsApproved() != null) {
            throw new BusinessRuleException("Pengajuan jadwal sudah diproses", HttpStatus.CONFLICT);
        }
        schedule.setIsApproved(approved);
        return mapper.toApprovalResponse(schedules.save(schedule));
    }
}
