package com.exacc.trabea.parttime.dto;

import java.time.LocalDateTime;

public record PartTimeEmployeeResponse(
        Integer id,
        String fullName,
        String personalEmail,
        String workEmail,
        String personalPhoneNumber,
        LocalDateTime joinDate
) {}
