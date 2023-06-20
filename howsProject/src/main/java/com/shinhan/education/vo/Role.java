package com.shinhan.education.vo;

//열거형
public enum Role {
    USER("USER"),
    ADMIN("ADMIN"),
    TELLER("TELLER");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}