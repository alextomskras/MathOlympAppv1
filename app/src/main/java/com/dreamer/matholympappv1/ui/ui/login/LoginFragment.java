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
import androidx.navigation.Navigation;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.databinding.FragmentLoginBinding;
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

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
//    public static LoginFragment newInstance() {
//        return new LoginFragment();
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
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

        // todo Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
//        if (mAuth.getCurrentUser() == null) {

            navController.clearBackStack(R.id.loginFragment);
            navController.navigate(R.id.action_loginFragment_to_zadachaFragment);
//            navController.navigate(R.id.action_loginFragment_to_RAZDELFragment);
            return;
        }
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
//                String username = usernameEditText.getText().toString().split("@")[0];
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Log.d(TAG, "username:" + username);
                checkUsernameAndGetUserId(username);
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());


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


            }
        });

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
        String username1 = username;
        String password1 = password;
//        String welcome = getString(R.string.welcome) + model.getDisplayName() + model.getPassword();
//        Log.d(TAG, "User ID1welcome: " + welcome);
//        mAuth.signInWithEmailAndPassword(model.getDisplayName(), model.getPassword()).addOnCompleteListener((Activity) getContext(),
        mAuth.signInWithEmailAndPassword(username1, password1).addOnCompleteListener((Activity) getContext(),
                task -> {
                    if (task.isSuccessful()) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                task.getResult().getUser().getEmail(), Snackbar.LENGTH_LONG).show();
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        Fragment mFrag = new ZadachaFragment();
//                        ft.replace(R.id.zadachaFragment, mFrag);
//                        ft.commit();
                        Bundle args = new Bundle();
                        args.putString("username", username);
                        args.putString("password", password);
                        args.putString("solutionlimits", "1");
                        args.putString("hintlimits", "3");
                        navController.clearBackStack(R.id.loginFragment);
                        navController.navigate(R.id.action_loginFragment_to_zadachaFragment, args);
//                        navController.navigate(R.id.action_loginFragment_to_RAZDELFragment, args);
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                task.getException().getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    }

                });
        // TODO : initiate successful logged in experience
        if (getContext() != null && getContext().getApplicationContext() != null) {
//            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();


        }
    }

    public void checkUsernameAndGetUserId(String username) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
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