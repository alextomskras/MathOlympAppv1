package com.dreamer.matholympappv1.ui.ui.register.ui.register;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.ui.ui.register.data.RegisterRepository;
import com.dreamer.matholympappv1.ui.ui.register.data.Result;
import com.dreamer.matholympappv1.ui.ui.register.data.model.RegisteredInUser;
import com.dreamer.matholympappv1.utils.InputValidator;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> loginResult = new MutableLiveData<>();
    private RegisterRepository registerRepository;

    RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    LiveData<RegisterFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<RegisterResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<RegisteredInUser> result = registerRepository.login(username, password);

        if (result instanceof Result.Success) {
            RegisteredInUser data = ((Result.Success<RegisteredInUser>) result).getData();
            loginResult.setValue(new RegisterResult(new RegisterInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new RegisterResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new RegisterFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new RegisterFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new RegisterFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            // Используем улучшенную валидацию имени пользователя
            return InputValidator.isValidUsername(username);
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }
        // Минимум 8 символов, должен содержать буквы и цифры
        return password.trim().length() >= 8 && 
               password.matches(".*[A-Za-z].*") && 
               password.matches(".*\\d.*");
    }
}