package com.exacc.trabea.parttime.controller;

import com.exacc.trabea.common.ApiResponse;
import com.exacc.trabea.parttime.dto.PartTimeEmployeeRequest;
import com.exacc.trabea.parttime.dto.PartTimeEmployeeResponse;
import com.exacc.trabea.parttime.service.PartTimeEmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/part-time-employees")
public class PartTimeEmployeeController {
    private final PartTimeEmployeeService service;
    public PartTimeEmployeeController(PartTimeEmployeeService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PartTimeEmployeeResponse> create(@Valid @RequestBody PartTimeEmployeeRequest request) {
        return ApiResponse.success("PartTimer berhasil dibuat", service.create(request));
    }
    @GetMapping
    public ApiResponse<Page<PartTimeEmployeeResponse>> findAll(
            @PageableDefault(size = 10, sort = "firstName", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String name, @RequestParam(required = false) Boolean active) {
        return ApiResponse.success("Daftar PartTimer", service.findAll(pageable, name, active));
    }
    @GetMapping("/{id}")
    public ApiResponse<PartTimeEmployeeResponse> findById(@PathVariable Integer id) {
        return ApiResponse.success("Detail PartTimer", service.findById(id));
    }
    @PutMapping("/{id}")
    public ApiResponse<PartTimeEmployeeResponse> update(@PathVariable Integer id,
            @Valid @RequestBody PartTimeEmployeeRequest request) {
        return ApiResponse.success("PartTimer berhasil diperbarui", service.update(id, request));
    }
    @PatchMapping("/{id}/resign")
    public ApiResponse<PartTimeEmployeeResponse> resign(@PathVariable Integer id) {
        return ApiResponse.success("PartTimer berhasil diproses resign", service.resign(id));
    }
}
