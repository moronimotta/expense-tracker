package com.expensetracker.app.models.enums;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public enum ExpenseCategory {
    FOOD,
    TRAVEL,
    UTILITIES,
    HOUSING,
    TRANSPORTATION,
    ENTERTAINMENT,
    GENERAL,
    OTHER;

    public static ExpenseCategory from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Category is required");
        }
        String normalized = value.trim().replace(' ', '_').toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(v -> v.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid category '" + value + "'. Allowed: " + allowedList()
                ));
    }

    public static boolean isValid(String value) {
        try { from(value); return true; } catch (Exception e) { return false; }
    }

    public static String allowedList() {
        return Arrays.stream(values())
                .map(v -> v.name().toLowerCase(Locale.ROOT).replace('_', ' '))
                .collect(Collectors.joining(", "));
    }
}
