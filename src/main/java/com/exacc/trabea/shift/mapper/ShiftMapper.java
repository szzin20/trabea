package com.exacc.trabea.shift.mapper;

import com.exacc.trabea.shift.dto.ShiftResponse;
import com.exacc.trabea.shift.entity.Shift;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShiftMapper {
    ShiftResponse toResponse(Shift shift);
}
