package com.exacc.trabea.workschedule.mapper;

import com.exacc.trabea.workschedule.dto.WorkScheduleResponse;
import com.exacc.trabea.workschedule.entity.WorkSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {
    @Mapping(target = "partTimeEmployeeId", source = "partTimeEmployee.id")
    @Mapping(target = "firstName", source = "partTimeEmployee.firstName")
    @Mapping(target = "lastName", source = "partTimeEmployee.lastName")
    @Mapping(target = "workShiftId", source = "workShift.id")
    @Mapping(target = "startTime", source = "workShift.startTime")
    @Mapping(target = "endTime", source = "workShift.endTime")
    WorkScheduleResponse toResponse(WorkSchedule schedule);
}
