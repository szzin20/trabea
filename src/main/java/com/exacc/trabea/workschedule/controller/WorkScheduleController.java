package com.exacc.trabea.workschedule.controller;

import com.exacc.trabea.common.ApiResponse;
import com.exacc.trabea.workschedule.dto.CreateWorkScheduleRequest;
import com.exacc.trabea.workschedule.dto.WorkScheduleResponse;
import com.exacc.trabea.workschedule.dto.SchedulePageItem;
import com.exacc.trabea.workschedule.dto.WorkScheduleApprovalResponse;
import com.exacc.trabea.workschedule.service.WorkScheduleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/work-schedules")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class WorkScheduleController {
    private final WorkScheduleService service;

    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<SchedulePageItem>>> getSchedulePage(
            @RequestParam(defaultValue = "current") String week) {
        return ResponseEntity.ok(ApiResponse.success("Jadwal berhasil ditemukan", service.getSchedulePage(week)));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<java.util.List<WorkScheduleApprovalResponse>>> getPendingSchedules() {
        return ResponseEntity.ok(ApiResponse.success("Pengajuan pending berhasil ditemukan", service.getPendingSchedules()));
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
