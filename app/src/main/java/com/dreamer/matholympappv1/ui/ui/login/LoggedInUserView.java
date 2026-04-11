package com.dreamer.matholympappv1.ui.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    public String displayPassword;
    private String displayName;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, String displayPassword) {

        this.displayName = displayName;
        this.displayPassword = displayPassword;
    }

    String getDisplayName() {
        return displayName;
    }

    String getPassword() {
        return displayPassword;
    }
}