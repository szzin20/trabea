package com.exacc.trabea.auth.service;

import com.exacc.trabea.auth.dto.LoginRequest;
import com.exacc.trabea.common.BusinessRuleException;
import com.exacc.trabea.common.ForbiddenException;
import com.exacc.trabea.common.ResourceNotFoundException;
import com.exacc.trabea.common.UnauthorizedException;
import com.exacc.trabea.auth.dto.LoginResponse;
import com.exacc.trabea.role.repository.RoleRepository;
import com.exacc.trabea.security.jwt.JwtService;
import com.exacc.trabea.security.jwt.RoleAuthority;
import com.exacc.trabea.user.repository.UserRepository;
import com.exacc.trabea.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final RoleRepository roles;
    private final UserRoleRepository userRoles;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        var user = users.findByWorkEmail(request.workEmail())
                .orElseThrow(() -> new UnauthorizedException("Email atau password salah"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Email atau password salah");
        }
        String requestedRole;
        try { requestedRole = RoleAuthority.canonical(request.role()); }
        catch (IllegalArgumentException exception) {
            throw new BusinessRuleException("Role tidak valid");
        }
        boolean roleExists = roles.findAll().stream()
                .map(role -> role.getName())
                .anyMatch(name -> {
                    try { return RoleAuthority.canonical(name).equals(requestedRole); }
                    catch (IllegalArgumentException ignored) { return false; }
                });
        if (!roleExists) {
            throw new ResourceNotFoundException("Role tidak ditemukan");
        }
        boolean ownsRole = userRoles.findAllByUser_WorkEmail(user.getWorkEmail()).stream()
                .map(link -> link.getRole().getName())
                .anyMatch(name -> {
                    try { return RoleAuthority.canonical(name).equals(requestedRole); }
                    catch (IllegalArgumentException ignored) { return false; }
                });
        if (!ownsRole) {
            throw new ForbiddenException("Role tidak dimiliki user");
        }
        return new LoginResponse(jwtService.generateToken(user.getWorkEmail(), requestedRole), "Bearer",
                user.getWorkEmail(), requestedRole);
    }
}
