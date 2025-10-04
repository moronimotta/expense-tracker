package com.expensetracker.app.models.enums;

/**
 * Enumeration of user roles in the expense tracker system.
 */
public enum UserRole {
    ADMIN("admin", "System Administrator"),
    USER("user", "Regular User");

    private final String code;
    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UserRole fromCode(String code) {
        for (UserRole role : UserRole.values()) {
            if (role.getCode().equalsIgnoreCase(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role code: " + code);
    }

    @Override
    public String toString() {
        return code;
    }
}