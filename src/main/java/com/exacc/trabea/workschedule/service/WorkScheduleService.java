package com.exacc.trabea.workschedule.service;

import com.exacc.trabea.common.BusinessRuleException;
import com.exacc.trabea.common.ResourceNotFoundException;
import com.exacc.trabea.parttime.repository.PartTimeEmployeeRepository;
import com.exacc.trabea.shift.repository.ShiftRepository;
import com.exacc.trabea.workschedule.dto.CreateWorkScheduleRequest;
import com.exacc.trabea.workschedule.dto.WorkScheduleResponse;
import com.exacc.trabea.workschedule.dto.SchedulePageItem;
import com.exacc.trabea.workschedule.dto.WorkScheduleApprovalResponse;
import com.exacc.trabea.workschedule.dto.ReviewItemResponse;
import com.exacc.trabea.workschedule.dto.ScheduleEmployeeDetailResponse;
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
import java.util.List;
import java.time.Clock;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {
    private static final LocalDate EARLIEST_SCHEDULE_DATE = LocalDate.of(1, 1, 1);
    private static final LocalDate LATEST_SCHEDULE_DATE = LocalDate.of(9999, 12, 31);
    private final WorkScheduleRepository schedules;
    private final PartTimeEmployeeRepository employees;
    private final ShiftRepository shifts;
    private final WorkScheduleMapper mapper;
    private final Clock workScheduleClock;

    @Transactional
    public WorkScheduleResponse createWorkSchedule(String workEmail, CreateWorkScheduleRequest request) {
        var employee = employees.findByUser_WorkEmailForUpdate(workEmail)
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
        long shiftsOnDate = schedules.countByPartTimeEmployee_IdAndWorkDate(employee.getId(), date);
        if (shiftsOnDate >= 2)
            throw new BusinessRuleException("Maksimal 2 shift dalam satu hari", HttpStatus.CONFLICT);
        if (shiftsOnDate == 1) {
            List<com.exacc.trabea.shift.entity.Shift> existingShifts =
                    schedules.findShiftsByEmployeeAndDate(employee.getId(), date);
            boolean adjacent = existingShifts.size() == 1 && (
                    existingShifts.getFirst().getEndTime().equals(shift.getStartTime()) ||
                            shift.getEndTime().equals(existingShifts.getFirst().getStartTime()));
            if (!adjacent) {
                throw new BusinessRuleException(
                        "Jika mengambil 2 shift dalam satu hari, shift harus berurutan.",
                        HttpStatus.BAD_REQUEST);
            }
        }

        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        if (schedules.countByEmployeeAndDateRange(employee.getId(), weekStart, weekStart.plusDays(5)) >= 5)
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
        LocalDate thisMonday = LocalDate.now(workScheduleClock)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
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
        LocalDate today = LocalDate.now(workScheduleClock);
        LocalDate start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        if (selectedWeek.equals("next")) start = start.plusWeeks(1);
        LocalDate end = start.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        return schedules.findApprovedForPeriod(start, end).stream()
                .map(mapper::toSchedulePageItem).toList();
    }

    @Transactional(readOnly = true)
    public ScheduleEmployeeDetailResponse getScheduleEmployeeDetail(Integer partTimerId) {
        var employee = employees.findById(partTimerId).orElseThrow(() ->
                new ResourceNotFoundException("Part-Time Employee dengan ID " + partTimerId + " tidak ditemukan", HttpStatus.NOT_FOUND));
        if (!schedules.existsByPartTimeEmployee_IdAndIsApprovedTrue(partTimerId)) {
            throw new ResourceNotFoundException("Part-Time Employee tidak memiliki jadwal approved", HttpStatus.NOT_FOUND);
        }
        return mapper.toScheduleEmployeeDetailResponse(employee);
    }

    @Transactional(readOnly = true)
    public List<WorkScheduleApprovalResponse> getPendingSchedules() {
        return schedules.findAllPending().stream().map(mapper::toApprovalResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<ReviewItemResponse> searchPendingForReview(String name, Integer shiftId, LocalDate startDate,
                                                            LocalDate endDate, int page, int size) {
        if (page < 0) throw new BusinessRuleException("page harus mulai dari 0", HttpStatus.BAD_REQUEST);
        if (size < 1 || size > 100) throw new BusinessRuleException("size harus antara 1 dan 100", HttpStatus.BAD_REQUEST);
        LocalDate start = startDate == null ? EARLIEST_SCHEDULE_DATE : startDate;
        LocalDate end = endDate == null ? LATEST_SCHEDULE_DATE : endDate;
        if (start.isAfter(end)) {
            throw new BusinessRuleException("startDate tidak boleh setelah endDate", HttpStatus.BAD_REQUEST);
        }
        String normalizedName = name == null || name.isBlank() ? "" : name.trim();
        return schedules.searchPendingForReview(normalizedName, shiftId, start, end,
                        PageRequest.of(page, size))
                .map(mapper::toReviewItemResponse);
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
