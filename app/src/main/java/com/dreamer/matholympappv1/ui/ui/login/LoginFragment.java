package com.dreamer.matholympappv1.ui.ui.login;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.databinding.FragmentLoginBinding;
import com.dreamer.matholympappv1.utils.SecureSharedPrefsUtils;
import com.dreamer.matholympappv1.utils.InputValidator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {
    NavController navController;
    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;
    private SecureSharedPrefsUtils sharedPrefs;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    //    public static LoginFragment newInstance() {
//        return new LoginFragment();
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        sharedPrefs = new SecureSharedPrefsUtils(getContext());
        
        // Проверяем, есть ли активная сессия Firebase при создании фрагмента
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Пользователь уже авторизован в Firebase: " + currentUser.getEmail());
            // Данные пользователя будут загружены через isUserAlreadyLoggedIn() в onViewCreated()
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        mUser = FirebaseAuth.getInstance().getCurrentUser();

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private DatabaseReference mDatabase;

    private void firebaseSignOut() {
        FirebaseAuth.getInstance().signOut();
//        finish();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        intNavcontroller();

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

// 🔁 Автологин
        if (isUserAlreadyLoggedIn()) {
            Log.d(TAG, "Пользователь уже авторизован");
            navController.clearBackStack(R.id.loginFragment);
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build();
            navController.navigate(R.id.RAZDELFragment, null, navOptions);
            return;
        }
//        mAuth = FirebaseAuth.getInstance();
//        if (mAuth.getCurrentUser() != null) {
////        if (mAuth.getCurrentUser() == null) {
//            Log.d(TAG, "Пользователь уже авторизован: " + mUser.getEmail());
//            navController.clearBackStack(R.id.loginFragment);
////            navController.navigate(R.id.action_loginFragment_to_zadachaFragment);
//            navController.navigate(R.id.action_loginFragment_to_RAZDELFragment);
//            return;
//        }
//        else {
//            navController.clearBackStack(R.id.loginFragment);
//            navController.navigate(R.id.loginFragment);
//        }


        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final Button registerButton = binding.btnregister;
        final Button signoutButton = binding.btnsignout;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    updateUiWithUser(username, password);
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                
                // Санитизация и валидация ввода перед отправкой
                String sanitizedUsername = InputValidator.validateAndSanitize(username);
                String sanitizedPassword = InputValidator.sanitizeInput(password);
                
                if (sanitizedUsername == null || sanitizedUsername.isEmpty()) {
                    usernameEditText.setError("Неверный формат имени пользователя");
                    return;
                }
                
                if (sanitizedPassword == null || sanitizedPassword.isEmpty()) {
                    passwordEditText.setError("Неверный формат пароля");
                    return;
                }
                
                Log.d(TAG, "username:" + sanitizedUsername);
                loadingProgressBar.setVisibility(View.VISIBLE);
                
                // Выполняем аутентификацию через Firebase Auth
                performFirebaseLogin(sanitizedUsername, sanitizedPassword);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.clearBackStack(R.id.loginFragment);
                navController.navigate(R.id.action_loginFragment_to_registerFragment);


            }
        });


        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseSignOut();
                sharedPrefs.clearData(); // 💾 очищаем sharedPrefs при выходе
                Snackbar.make(requireView(), "Выход выполнен", Snackbar.LENGTH_SHORT).show();


            }
        });

    }

    private boolean isUserAlreadyLoggedIn() {
        // Проверяем активную сессию Firebase Auth
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Log.d(TAG, "Пользователь авторизован в Firebase: " + firebaseUser.getEmail());
            // Загружаем данные пользователя из Firebase Database (без навигации)
            loadUserDataFromFirebase(firebaseUser.getUid(), firebaseUser.getEmail(), true);
            return true;
        }
        // Также проверяем локальный статус входа (на случай если сессия Firebase ещё не восстановилась)
        return sharedPrefs.loadLoginStatus();
    }

    private void intNavcontroller() {
        Activity MainActivity = getActivity();
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateUiWithUser(String username, String password) {
        // Больше не передаем пароль между экранами - используем Firebase Auth
        String welcome = "Добро пожаловать, " + username;
        
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Выполняет вход через Firebase Authentication используя email и пароль.
     * После успешной аутентификации загружает данные пользователя из Firebase Database.
     */
    private void performFirebaseLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    loadingProgressBar.setVisibility(View.GONE);
                    
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();
                        
                        Log.d(TAG, "Успешный вход. User ID: " + userId);
                        
                        // Сохраняем статус авторизации и username в зашифрованном хранилище
                        sharedPrefs.saveLoginStatus(true);
                        sharedPrefs.saveUsername(email);
                        sharedPrefs.saveUid(userId);
                        
                        // Загружаем дополнительные данные пользователя из Firebase Database
                        loadUserDataFromFirebase(userId, email);
                        
                    } else {
                        Log.e(TAG, "Ошибка входа: " + task.getException().getMessage());
                        showLoginFailed(R.string.login_failed);
                    }
                });
    }
    
    /**
     * Загружает данные пользователя (solutionlimits, hintlimits) из Firebase Database
     * после успешной аутентификации.
     */
    private void loadUserDataFromFirebase(String userId, String email) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Получаем данные пользователя
                    String solutionLimits = dataSnapshot.child("solutionlimits").getValue(String.class);
                    String hintLimits = dataSnapshot.child("hintlimits").getValue(String.class);
                    
                    // Значения по умолчанию
                    if (solutionLimits == null) solutionLimits = "1";
                    if (hintLimits == null) hintLimits = "3";
                    
                    Log.d(TAG, "Данные загружены. solutionLimits: " + solutionLimits + ", hintLimits: " + hintLimits);
                    
                    // Навигация к главному экрану с передачей только необходимых данных
                    navigateToMainScreen(email, solutionLimits, hintLimits);
                    
                } else {
                    Log.w(TAG, "Данные пользователя не найдены в базе. Используем значения по умолчанию.");
                    // Если данных нет, используем значения по умолчанию
                    navigateToMainScreen(email, "1", "3");
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Ошибка загрузки данных: " + databaseError.getMessage());
                // При ошибке всё равно переходим на главный экран с дефолтными значениями
                navigateToMainScreen(email, "1", "3");
            }
        });
    }
    
    /**
     * Перегрузка для авто-входа (без навигации, если пользователь уже на главном экране)
     */
    private void loadUserDataFromFirebase(String userId, String email, boolean isAutoLogin) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Получаем данные пользователя
                    String solutionLimits = dataSnapshot.child("solutionlimits").getValue(String.class);
                    String hintLimits = dataSnapshot.child("hintlimits").getValue(String.class);
                    
                    // Сохраняем в SharedPreferences для использования в других экранах
                    sharedPrefs.saveString("solutionlimits", solutionLimits != null ? solutionLimits : "1");
                    sharedPrefs.saveString("hintlimits", hintLimits != null ? hintLimits : "3");
                    
                    Log.d(TAG, "Данные загружены (auto-login). solutionLimits: " + solutionLimits + ", hintLimits: " + hintLimits);
                    
                } else {
                    Log.w(TAG, "Данные пользователя не найдены в базе (auto-login).");
                    // Сохраняем значения по умолчанию
                    sharedPrefs.saveString("solutionlimits", "1");
                    sharedPrefs.saveString("hintlimits", "3");
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Ошибка загрузки данных (auto-login): " + databaseError.getMessage());
            }
        });
    }
    
    /**
     * Переход на главный экран (RAZDELFragment) без передачи пароля.
     * Передаём только email и лимиты, которые будут использованы в приложении.
     */
    private void navigateToMainScreen(String email, String solutionLimits, String hintLimits) {
        Bundle args = new Bundle();
        args.putString("username", email);
        // Пароль больше не передаём! Он не нужен после аутентификации Firebase
        // args.putString("password", password); // <-- УДАЛЕНО
        args.putString("solutionlimits", solutionLimits);
        args.putString("hintlimits", hintLimits);
        
        // Навигация с очисткой loginFragment из back stack
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build();
        
        navController.navigate(R.id.RAZDELFragment, args, navOptions);
    }

    private void checkUsernameAndGetUserId(String username) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        
        // Проверка на NoSQL инъекции перед запросом к Firebase
        if (InputValidator.containsNoSqlInjection(username)) {
            Log.w(TAG, "Обнаружена попытка NoSQL инъекции: " + username);
            return;
        }
        
        Query query = mDatabase.orderByChild("username").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username exists in the database
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String userId = childSnapshot.getKey();
                        Log.d(TAG, "User ID1: " + userId);
                    }
                } else {
                    // Username does not exist in the database
                    Log.d(TAG, "Username does not exist in the database");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error reading username from Firebase Database: " + databaseError.getMessage());
            }
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) getContext(), task -> {
                    if (task.isSuccessful()) {
                        // TODO: Handle successful sign-in
                    } else {
                        // TODO: Handle sign-in failure
                    }
                });
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        FirebaseAuth.getInstance().signOut();
//    }


}