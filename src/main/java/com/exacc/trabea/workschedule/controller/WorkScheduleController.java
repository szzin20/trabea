package com.exacc.trabea.workschedule.controller;

import com.exacc.trabea.common.ApiResponse;
import com.exacc.trabea.workschedule.dto.CreateWorkScheduleRequest;
import com.exacc.trabea.workschedule.dto.WorkScheduleResponse;
import com.exacc.trabea.workschedule.dto.SchedulePageItem;
import com.exacc.trabea.workschedule.dto.WorkScheduleApprovalResponse;
import com.exacc.trabea.workschedule.dto.ReviewItemResponse;
import com.exacc.trabea.workschedule.dto.ScheduleEmployeeDetailResponse;
import com.exacc.trabea.workschedule.service.WorkScheduleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/work-schedules")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Work Schedule")
public class WorkScheduleController {
    private final WorkScheduleService service;

    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<SchedulePageItem>>> getSchedulePage(
            @RequestParam(defaultValue = "current") String week) {
        return ResponseEntity.ok(ApiResponse.success("Jadwal berhasil ditemukan", service.getSchedulePage(week)));
    }

    @GetMapping("/part-time-employees/{partTimerId}")
    public ResponseEntity<ApiResponse<ScheduleEmployeeDetailResponse>> getScheduleEmployeeDetail(
            @PathVariable Integer partTimerId) {
        return ResponseEntity.ok(ApiResponse.success("Detail part-time employee berhasil ditemukan",
                service.getScheduleEmployeeDetail(partTimerId)));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<ReviewItemResponse>>> getPendingSchedules(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer shiftId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Pengajuan berhasil ditemukan",
                service.searchPendingForReview(name, shiftId, startDate, endDate, page, size)));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<WorkScheduleApprovalResponse>> approve(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Pengajuan berhasil disetujui", service.approveSchedule(id)));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<WorkScheduleApprovalResponse>> reject(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Pengajuan berhasil ditolak", service.rejectSchedule(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WorkScheduleResponse> create(@Valid @RequestBody CreateWorkScheduleRequest request,
                                                   Authentication authentication) {
        return ApiResponse.success("Pengajuan shift berhasil dibuat",
                service.createWorkSchedule(authentication.getName(), request));
    }

    @GetMapping("/my")
    public ApiResponse<Page<WorkScheduleResponse>> getMine(Authentication authentication,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success("Daftar pengajuan shift", service.getMyWorkSchedules(authentication.getName(), page, size));
    }
}
