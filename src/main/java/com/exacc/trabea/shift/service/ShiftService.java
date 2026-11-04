package com.exacc.trabea.shift.service;

import com.exacc.trabea.common.ResourceNotFoundException;
import com.exacc.trabea.shift.dto.ShiftResponse;
import com.exacc.trabea.shift.mapper.ShiftMapper;
import com.exacc.trabea.shift.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftService {
    private final ShiftRepository shifts;
    private final ShiftMapper mapper;

    @Transactional(readOnly = true)
    public List<ShiftResponse> getAllShifts() {
        return shifts.findAllByOrderByIdAsc().stream().map(mapper::toResponse).toList();
    }

//    @Transactional(readOnly = true)
//    public ShiftResponse getShiftById(Integer id) {
//        return shifts.findById(id)
//                .map(mapper::toResponse)
//                .orElseThrow(() -> new ResourceNotFoundException("Shift dengan ID " + id + " tidak ditemukan", HttpStatus.NOT_FOUND));
//    }
}
