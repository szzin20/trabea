package com.exacc.trabea.auth.service;

import com.exacc.trabea.auth.dto.LoginRequest;
import com.exacc.trabea.role.entity.Role;
import com.exacc.trabea.role.repository.RoleRepository;
import com.exacc.trabea.security.jwt.JwtService;
import com.exacc.trabea.user.entity.User;
import com.exacc.trabea.user.entity.UserRole;
import com.exacc.trabea.user.repository.UserRepository;
import com.exacc.trabea.user.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    UserRepository users = mock(UserRepository.class);
    RoleRepository roles = mock(RoleRepository.class);
    UserRoleRepository userRoles = mock(UserRoleRepository.class);
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    JwtService jwt = new JwtService("test-secret-for-jwt-signing-at-least-32-bytes-long", 60000);
    AuthService service = new AuthService(users, roles, userRoles, encoder, jwt);
    User user;

    @BeforeEach void setup() {
        user = User.builder().workEmail("person@example.com").password(encoder.encode("password123")).build();
        when(users.findByWorkEmail(user.getWorkEmail())).thenReturn(Optional.of(user));
        when(roles.findAll()).thenReturn(List.of(Role.builder().name("MANAGER").build(),
                Role.builder().name("PART_TIME").build(), Role.builder().name("SUPERVISOR").build()));
    }

    @Test void loginSucceedsForOwnedRole() {
        when(userRoles.findAllByUser_WorkEmail(user.getWorkEmail())).thenReturn(List.of(link("Manager")));
        var response = service.login(new LoginRequest(user.getWorkEmail(), "password123", "Manager"));
        assertEquals("MANAGER", response.activeRole());
        assertEquals(user.getWorkEmail(), jwt.parse(response.accessToken()).getSubject());
        assertEquals("MANAGER", jwt.parse(response.accessToken()).get("activeRole"));
    }

    @Test void unknownEmailIsRejected() {
        when(users.findByWorkEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.login(new LoginRequest("missing@example.com", "password123", "Manager")));
    }

    @Test void incorrectPasswordIsRejected() {
        when(userRoles.findAllByUser_WorkEmail(user.getWorkEmail())).thenReturn(List.of(link("Manager")));
        assertThrows(ResponseStatusException.class, () -> service.login(new LoginRequest(user.getWorkEmail(), "wrong-pass", "Manager")));
    }

    @Test void unownedRoleIsRejected() {
        when(userRoles.findAllByUser_WorkEmail(user.getWorkEmail())).thenReturn(List.of(link("Administrator")));
        assertThrows(ResponseStatusException.class, () -> service.login(new LoginRequest(user.getWorkEmail(), "password123", "Manager")));
    }

    @Test void roleNotAssignedToUserIsForbidden() {
        when(userRoles.findAllByUser_WorkEmail(user.getWorkEmail())).thenReturn(List.of(link("MANAGER")));
        var exception = assertThrows(ResponseStatusException.class, () ->
                service.login(new LoginRequest(user.getWorkEmail(), "password123", "PART_TIME")));
        assertEquals(403, exception.getStatusCode().value());
    }

    @Test void roleMissingFromDatabaseIsNotFound() {
        var exception = assertThrows(ResponseStatusException.class, () ->
                service.login(new LoginRequest(user.getWorkEmail(), "password123", "part jooko")));
        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Role tidak ditemukan", exception.getReason());
    }

    @Test void newlyAddedDatabaseRoleCanBeUsedForLogin() {
        when(userRoles.findAllByUser_WorkEmail(user.getWorkEmail())).thenReturn(List.of(link("SUPERVISOR")));
        var response = service.login(new LoginRequest(user.getWorkEmail(), "password123", "supervisor"));
        assertEquals("SUPERVISOR", response.activeRole());
        assertEquals("SUPERVISOR", jwt.parse(response.accessToken()).get("activeRole"));
    }

    @Test void databaseRoleNamesAreNormalizedGenerically() {
        when(userRoles.findAllByUser_WorkEmail(user.getWorkEmail())).thenReturn(List.of(link("PART_TIME")));
        var response = service.login(new LoginRequest(user.getWorkEmail(), "password123", "part-time"));
        assertEquals("PART_TIME", response.activeRole());
    }

    @Test void nullOrBlankRoleIsBadRequest() {
        for (String role : List.of("", " ")) {
            var exception = assertThrows(ResponseStatusException.class, () ->
                    service.login(new LoginRequest(user.getWorkEmail(), "password123", role)));
            assertEquals(400, exception.getStatusCode().value());
        }
        var exception = assertThrows(ResponseStatusException.class, () ->
                service.login(new LoginRequest(user.getWorkEmail(), "password123", null)));
        assertEquals(400, exception.getStatusCode().value());
    }

    @Test void roleAuthorityNamesAreConsistent() {
        assertEquals("ROLE_ADMIN", com.exacc.trabea.security.jwt.RoleAuthority.authority("admin"));
        assertEquals("ROLE_MANAGER", com.exacc.trabea.security.jwt.RoleAuthority.authority("Manager"));
        assertEquals("ROLE_PART_TIME", com.exacc.trabea.security.jwt.RoleAuthority.authority("PART_TIME"));
        assertEquals("ROLE_SUPERVISOR", com.exacc.trabea.security.jwt.RoleAuthority.authority("SUPERVISOR"));
        assertEquals("ROLE_MANAGER", com.exacc.trabea.security.jwt.RoleAuthority.authority("ROLE_MANAGER"));
        assertThrows(IllegalArgumentException.class, () -> com.exacc.trabea.security.jwt.RoleAuthority.authority(" "));
        assertThrows(IllegalArgumentException.class, () -> com.exacc.trabea.security.jwt.RoleAuthority.authority(null));
    }

    private UserRole link(String name) {
        return UserRole.builder().role(Role.builder().name(name).build()).build();
    }
}
