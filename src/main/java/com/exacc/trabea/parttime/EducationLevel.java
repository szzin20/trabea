package com.exacc.trabea.parttime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum EducationLevel {
    ELEMENTARY("ELEMENTARY"),
    JUNIOR_HIGH("JUNIOR HIGH"),
    HIGH_SCHOOL("HIGH SCHOOL"),
    BACHELOR("BACHELOR"),
    MASTER("MASTER"),
    DOCTORATE("DOCTORATE");

    private final String label;

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static EducationLevel from(String value) {
        if (value == null || value.isBlank()) return null;
        String key = normalize(value);
        for (EducationLevel level : values()) {
            if (normalize(level.label).equals(key)) return level;
        }
        throw new IllegalArgumentException("Nilai pendidikan '" + value + "' tidak valid. Gunakan salah satu dari: "
                + Arrays.stream(values()).map(EducationLevel::getLabel).collect(Collectors.joining(", ")));
    }

    private static String normalize(String s) {
        return s.trim().toUpperCase().replaceAll("[\\s_-]+", "");
    }
}
