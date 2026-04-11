package com.dreamer.matholympappv1.ui.ui.register.data;

import com.dreamer.matholympappv1.ui.ui.register.data.model.RegisteredInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class RegisterDataSource {

    public Result<RegisteredInUser> login(String username, String password) {

        try {

            RegisteredInUser fakeUser =
                    new RegisteredInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {

    }
}