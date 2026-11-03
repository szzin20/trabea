package com.exacc.trabea.auth.service;

import com.exacc.trabea.auth.dto.LoginRequest;
import com.exacc.trabea.role.entity.Role;
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
    UserRoleRepository userRoles = mock(UserRoleRepository.class);
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    JwtService jwt = new JwtService("test-secret-for-jwt-signing-at-least-32-bytes-long", 60000);
    AuthService service = new AuthService(users, userRoles, encoder, jwt);
    User user;

    @BeforeEach void setup() {
        user = User.builder().workEmail("person@example.com").password(encoder.encode("password123")).build();
        when(users.findByWorkEmail(user.getWorkEmail())).thenReturn(Optional.of(user));
    }

    @Test void loginSucceedsForOwnedRole() {
        when(userRoles.findAllByUser_WorkEmail(user.getWorkEmail())).thenReturn(List.of(link("Manager")));
        var response = service.login(new LoginRequest(user.getWorkEmail(), "password123", "Manager"));
        assertEquals("Manager", response.activeRole());
        assertEquals(user.getWorkEmail(), jwt.parse(response.accessToken()).getSubject());
        assertEquals("Manager", jwt.parse(response.accessToken()).get("activeRole"));
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

    @Test void unknownRoleIsBadRequest() {
        var exception = assertThrows(ResponseStatusException.class, () ->
                service.login(new LoginRequest(user.getWorkEmail(), "password123", "SUPERUSER")));
        assertEquals(400, exception.getStatusCode().value());
    }

    @Test void legacySeedRoleNamesMapToCanonicalActiveRole() {
        when(userRoles.findAllByUser_WorkEmail(user.getWorkEmail())).thenReturn(List.of(link("PART_TIME")));
        var response = service.login(new LoginRequest(user.getWorkEmail(), "password123", "PartTimer"));
        assertEquals("PART_TIME", response.activeRole());
    }

    @Test void roleAuthorityNamesAreConsistent() {
        assertEquals("ROLE_ADMINISTRATOR", com.exacc.trabea.security.jwt.RoleAuthority.authority("Administrator"));
        assertEquals("ROLE_MANAGER", com.exacc.trabea.security.jwt.RoleAuthority.authority("Manager"));
        assertEquals("ROLE_PARTTIMER", com.exacc.trabea.security.jwt.RoleAuthority.authority("PartTimer"));
    }

    private UserRole link(String name) {
        return UserRole.builder().role(Role.builder().name(name).build()).build();
    }
}
