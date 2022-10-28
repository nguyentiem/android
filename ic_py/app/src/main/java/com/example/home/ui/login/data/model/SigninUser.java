package com.example.home.ui.login.data.model;

public class SigninUser {
    private String userId;
    private String displayName;

    public SigninUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
