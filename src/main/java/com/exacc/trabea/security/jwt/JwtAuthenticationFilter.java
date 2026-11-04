package com.exacc.trabea.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import com.exacc.trabea.user.repository.UserRepository;
import com.exacc.trabea.user.repository.UserRoleRepository;
import com.exacc.trabea.security.service.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.exacc.trabea.common.ApiResponse;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserRepository users;
    private final UserRoleRepository userRoles;


    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                               FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            Claims claims;
            try {
                claims = jwtService.parse(header.substring(7));
            } catch (JwtException | IllegalArgumentException ex) {
                rejectUnauthorized(response);
                return;
            }
            String email = claims.getSubject();
            String role = claims.get("activeRole", String.class);
            if (email == null || role == null) {
                rejectUnauthorized(response);
                return;
            }
            String canonicalRole;
            String authority;
            try {
                canonicalRole = RoleAuthority.canonical(role);
                authority = RoleAuthority.authority(role);
            } catch (IllegalArgumentException ex) {
                rejectUnauthorized(response);
                return;
            }
            var user = users.findByWorkEmail(email).orElse(null);
            if (user == null) {
                rejectUnauthorized(response);
                return;
            }
            boolean ownsRole = userRoles.findAllByUser_WorkEmail(email).stream()
                    .map(link -> link.getRole().getName())
                    .anyMatch(userRole -> {
                        try { return RoleAuthority.canonical(userRole).equals(canonicalRole); }
                        catch (IllegalArgumentException ignored) { return false; }
                    });
            if (!ownsRole) {
                rejectUnauthorized(response);
                return;
            }
            var authentication = new UsernamePasswordAuthenticationToken(
                    new CustomUserDetails(user, canonicalRole), null, List.of(() -> authority));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private void rejectUnauthorized(HttpServletResponse response) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "Bearer");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), ApiResponse.failure("Token tidak valid atau kedaluwarsa"));
    }
}
