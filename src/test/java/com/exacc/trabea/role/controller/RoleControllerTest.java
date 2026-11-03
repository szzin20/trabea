package com.exacc.trabea.role.controller;

import com.exacc.trabea.role.dto.RoleResponse;
import com.exacc.trabea.role.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleControllerTest {
    private final RoleService service = mock(RoleService.class);
    private final RoleController controller = new RoleController(service);

    @Test
    void returnsWrappedRoleIdsAndNames() {
        when(service.findAll()).thenReturn(List.of(new RoleResponse(1, "Administrator")));

        var response = controller.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().success());
        assertEquals("Roles berhasil ditemukan", response.getBody().message());
        assertEquals(List.of(new RoleResponse(1, "Administrator")), response.getBody().data());
        verify(service).findAll();
    }
}
