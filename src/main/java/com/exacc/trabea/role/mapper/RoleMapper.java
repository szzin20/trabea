package com.exacc.trabea.role.mapper;

import com.exacc.trabea.role.dto.RoleResponse;
import com.exacc.trabea.role.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toResponse(Role role);
}
