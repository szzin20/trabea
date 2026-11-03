package com.exacc.trabea.role.service;

import com.exacc.trabea.role.dto.RoleResponse;
import com.exacc.trabea.role.mapper.RoleMapper;
import com.exacc.trabea.role.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roles;
    private final RoleMapper mapper;

    public RoleService(RoleRepository roles, RoleMapper mapper) {
        this.roles = roles;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roles.findAll().stream().map(mapper::toResponse).toList();
    }
}
