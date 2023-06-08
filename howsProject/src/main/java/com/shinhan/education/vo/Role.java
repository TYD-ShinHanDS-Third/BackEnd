package com.shinhan.education.vo;

public enum Role {
    USER("User"),
    ADMIN("Admin"),
    TELLER("Teller");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}