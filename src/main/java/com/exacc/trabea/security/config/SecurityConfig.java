package com.exacc.trabea.security.config;

import com.exacc.trabea.security.jwt.JwtAuthenticationFilter;
import com.exacc.trabea.security.jwt.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.exacc.trabea.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    com.fasterxml.jackson.databind.ObjectMapper objectMapper() {
        return new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules();
    }

    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService,
                                                  ObjectMapper objectMapper) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/part-time-employees/**").hasAnyRole("ADMINISTRATOR", "MANAGER")
                        .requestMatchers("/api/v1/part-time-employees/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/api/v1/manager/**").hasRole("MANAGER")
                        .requestMatchers("/api/v1/part-timer/**").hasRole("PARTTIMER")
                        .anyRequest().authenticated())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, exception) -> writeError(
                                response, objectMapper, HttpServletResponse.SC_UNAUTHORIZED, "Autentikasi diperlukan"))
                        .accessDeniedHandler((request, response, exception) -> writeError(
                                response, objectMapper, HttpServletResponse.SC_FORBIDDEN, "Akses ditolak")))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, objectMapper), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private static void writeError(HttpServletResponse response, ObjectMapper mapper, int status, String message)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getOutputStream(), ApiResponse.failure(message));
    }
}
