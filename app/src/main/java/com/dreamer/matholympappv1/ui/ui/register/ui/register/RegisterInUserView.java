package com.dreamer.matholympappv1.ui.ui.register.ui.register;

/**
 * Class exposing authenticated user details to the UI.
 */
class RegisterInUserView {
    private String displayName;
    //... other data fields that may be accessible to the UI

    RegisterInUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}