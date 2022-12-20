package com.dreamer.matholympappv1.data.data.model;

/**
 * Data class that captures user information for logged in users retrieved from RegisterRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String passwordUser;

    public LoggedInUser(String userId, String displayName, String passwordUser) {
        this.userId = userId;
        this.displayName = displayName;
        this.passwordUser = passwordUser;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return passwordUser;
    }
}