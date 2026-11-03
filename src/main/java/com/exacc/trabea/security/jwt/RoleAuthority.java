package com.exacc.trabea.security.jwt;

public final class RoleAuthority {
    private RoleAuthority() {}

    public static String authority(String role) {
        if (role == null) throw new IllegalArgumentException("Role tidak dikenal");
        return switch (role.trim().toUpperCase().replace('-', '_').replace(' ', '_')) {
            case "ADMIN", "ADMINISTRATOR" -> "ROLE_ADMINISTRATOR";
            case "MANAGER" -> "ROLE_MANAGER";
            case "PARTTIMER", "PART_TIME" -> "ROLE_PARTTIMER";
            default -> throw new IllegalArgumentException("Role tidak dikenal");
        };
    }

    public static String canonical(String role) {
        return switch (authority(role)) {
            case "ROLE_ADMINISTRATOR" -> "ADMIN";
            case "ROLE_MANAGER" -> "MANAGER";
            default -> "PART_TIME";
        };
    }
}
