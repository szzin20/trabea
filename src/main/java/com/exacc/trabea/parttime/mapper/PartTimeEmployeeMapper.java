package com.exacc.trabea.parttime.mapper;

import com.exacc.trabea.parttime.dto.PartTimeEmployeeResponse;
import com.exacc.trabea.parttime.entity.PartTimeEmployee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartTimeEmployeeMapper {
    @Mapping(target = "workEmail", source = "user.workEmail")
    @Mapping(target = "fullName", expression = "java(employee.getFirstName() + \" \" + employee.getLastName())")
    PartTimeEmployeeResponse toResponse(PartTimeEmployee employee);
}
