package com.exacc.trabea.security.jwt;

public final class RoleAuthority {
    private RoleAuthority() {}

    public static String authority(String role) {
        return "ROLE_" + canonical(role);
    }

    public static String canonical(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role tidak boleh kosong");
        }

        String normalized = role.trim().toUpperCase(java.util.Locale.ROOT)
                .replace('-', '_').replace(' ', '_');
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring("ROLE_".length());
        }
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Role tidak boleh kosong");
        }
        return normalized;
    }
}
