package com.exacc.trabea.shift.controller;

import com.exacc.trabea.common.ApiResponse;
import com.exacc.trabea.shift.dto.ShiftResponse;
import com.exacc.trabea.shift.service.ShiftService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
@SecurityRequirement(name = "bearerAuth")
public class ShiftController {
    private final ShiftService service;

    public ShiftController(ShiftService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getAllShifts() {
        return ResponseEntity.ok(ApiResponse.success("Shifts berhasil ditemukan", service.getAllShifts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftResponse>> getShiftById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Shift berhasil ditemukan", service.getShiftById(id)));
    }
}
