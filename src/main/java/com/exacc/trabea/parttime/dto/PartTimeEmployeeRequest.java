package com.exacc.trabea.parttime.dto;

import com.exacc.trabea.parttime.EducationLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PartTimeEmployeeRequest(
        @NotBlank @Size(max = 25) String firstName,
        @NotBlank @Size(max = 50) String lastName,
        @NotBlank @Email @Size(max = 100) String personalEmail,
        @NotBlank @Size(max = 20) String personalPhoneNumber,
        @NotNull EducationLevel lastEducation,
        EducationLevel onGoingEducation
) {}
