package com.fitness.tracker.enums;

public enum UserRole {

    GUEST("GUEST", "Guest"),
    USER("USER", "User"),
    ADMIN("ADMIN", "Admin");

    private final String code;
    private final String name;

    UserRole(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
