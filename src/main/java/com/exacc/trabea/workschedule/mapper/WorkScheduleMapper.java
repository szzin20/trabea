package com.exacc.trabea.workschedule.mapper;

import com.exacc.trabea.workschedule.dto.WorkScheduleResponse;
import com.exacc.trabea.workschedule.dto.SchedulePageItem;
import com.exacc.trabea.workschedule.dto.WorkScheduleApprovalResponse;
import com.exacc.trabea.workschedule.dto.ReviewItemResponse;
import com.exacc.trabea.workschedule.dto.ScheduleEmployeeDetailResponse;
import com.exacc.trabea.parttime.entity.PartTimeEmployee;
import com.exacc.trabea.workschedule.entity.WorkSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {
    @Mapping(target = "fullName", expression = "java(employee.getFirstName() + \" \" + employee.getLastName())")
    @Mapping(target = "workEmail", source = "user.workEmail")
    ScheduleEmployeeDetailResponse toScheduleEmployeeDetailResponse(PartTimeEmployee employee);

    @Mapping(target = "partTimeEmployeeId", source = "partTimeEmployee.id")
    @Mapping(target = "firstName", source = "partTimeEmployee.firstName")
    @Mapping(target = "lastName", source = "partTimeEmployee.lastName")
    @Mapping(target = "workShiftId", source = "workShift.id")
    @Mapping(target = "startTime", source = "workShift.startTime")
    @Mapping(target = "endTime", source = "workShift.endTime")
    WorkScheduleResponse toResponse(WorkSchedule schedule);

    @Mapping(target = "partTimerId", source = "partTimeEmployee.id")
    @Mapping(target = "fullName", expression = "java(schedule.getPartTimeEmployee().getFirstName() + \" \" + schedule.getPartTimeEmployee().getLastName())")
    @Mapping(target = "date", source = "workDate")
    @Mapping(target = "shiftId", source = "workShift.id")
    SchedulePageItem toSchedulePageItem(WorkSchedule schedule);

    @Mapping(target = "workScheduleId", source = "id")
    @Mapping(target = "partTimerId", source = "partTimeEmployee.id")
    @Mapping(target = "fullName", expression = "java(schedule.getPartTimeEmployee().getFirstName() + \" \" + schedule.getPartTimeEmployee().getLastName())")
    @Mapping(target = "date", source = "workDate")
    @Mapping(target = "shiftId", source = "workShift.id")
    WorkScheduleApprovalResponse toApprovalResponse(WorkSchedule schedule);

    @Mapping(target = "workScheduleId", source = "id")
    @Mapping(target = "partTimerId", source = "partTimeEmployee.id")
    @Mapping(target = "fullName", expression = "java(schedule.getPartTimeEmployee().getFirstName() + \" \" + schedule.getPartTimeEmployee().getLastName())")
    @Mapping(target = "date", source = "workDate")
    @Mapping(target = "shiftId", source = "workShift.id")
    @Mapping(target = "status", expression = "java(schedule.getIsApproved() == null ? \"PENDING\" : schedule.getIsApproved() ? \"APPROVED\" : \"REJECTED\")")
    ReviewItemResponse toReviewItemResponse(WorkSchedule schedule);
}
