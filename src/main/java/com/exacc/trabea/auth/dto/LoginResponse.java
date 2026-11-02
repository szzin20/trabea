package com.exacc.trabea.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        String workEmail,
        String activeRole
) {
}

