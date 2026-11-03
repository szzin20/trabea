package com.exacc.trabea.role.service;

import com.exacc.trabea.role.dto.RoleResponse;
import com.exacc.trabea.role.entity.Role;
import com.exacc.trabea.role.mapper.RoleMapper;
import com.exacc.trabea.role.repository.RoleRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RoleServiceTest {
    private final RoleRepository repository = mock(RoleRepository.class);
    private final RoleMapper mapper = mock(RoleMapper.class);
    private final RoleService service = new RoleService(repository, mapper);

    @Test
    void mapsAllRolesToIdAndNameResponses() {
        var administrator = Role.builder().id(1).name("Administrator").build();
        var manager = Role.builder().id(2).name("Manager").build();
        when(repository.findAll()).thenReturn(List.of(administrator, manager));
        when(mapper.toResponse(administrator)).thenReturn(new RoleResponse(1, "Administrator"));
        when(mapper.toResponse(manager)).thenReturn(new RoleResponse(2, "Manager"));

        assertEquals(List.of(new RoleResponse(1, "Administrator"), new RoleResponse(2, "Manager")), service.findAll());
        verify(repository).findAll();
    }
}
