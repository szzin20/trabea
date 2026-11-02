package com.exacc.trabea.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "Work email wajib diisi")
        @Email(message = "Format work email tidak valid")
        String workEmail,

        @NotBlank(message = "Password wajib diisi")
        @Size(min = 8, message = "Password minimal 8 karakter")
        String password,

        @NotBlank(message = "Role wajib dipilih")
        String role

) {
}

