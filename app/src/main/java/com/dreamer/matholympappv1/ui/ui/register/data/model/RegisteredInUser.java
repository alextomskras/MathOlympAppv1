package com.dreamer.matholympappv1.ui.ui.register.data.model;

/**
 * Data class that captures user information for logged in users retrieved from RegisterRepository
 */
public class RegisteredInUser {

    private String userId;
    private String displayName;

    public RegisteredInUser(String userId, String displayName) {
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