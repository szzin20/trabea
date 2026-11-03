package com.exacc.trabea.parttime.dto;

import com.exacc.trabea.parttime.EducationLevel;
import java.time.LocalDateTime;

public record PartTimeEmployeeResponse(
        Integer id, String firstName, String lastName, String personalEmail,
        String workEmail, String personalPhoneNumber, LocalDateTime joinDate
) {}
