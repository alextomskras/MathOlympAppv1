package com.dreamer.matholympappv1.ui.ui.register.ui.register;

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
import com.dreamer.matholympappv1.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class RegisterFragment extends Fragment {

    NavController navController;
    DatabaseReference databaseReference;
    private RegisterViewModel registerViewModel;
    private FragmentRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseMessaging mFmess;
    private String fbToken;
    private String mToken;
    private String TAG;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //init Firebase

        mFmess = FirebaseMessaging.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        intNavcontroller();
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.register;
        final ProgressBar loadingProgressBar = binding.loading;

        registerViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }
                loginButton.setEnabled(registerFormState.isDataValid());
                if (registerFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(registerFormState.getPasswordError()));
                }
            }
        });

        registerViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<RegisterResult>() {
            @Override
            public void onChanged(@Nullable RegisterResult registerResult) {
                if (registerResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (registerResult.getError() != null) {
                    showLoginFailed(registerResult.getError());
                }
                if (registerResult.getSuccess() != null) {
                    updateUiWithUser(registerResult.getSuccess());
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
                registerViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            private void onComplete(Task<String> task1) {
                if (!task1.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task1.getException());
                    return;
                } else {

// Get new FCM registration token
                    mToken = task1.getResult();
                    Log.d(TAG, "Refreshed token: " + mToken);


// Log and toast
//                                                if (getContext() != null && getContext().getApplicationContext() != null) {
//                                                    Toast.makeText(
//                                                            getContext().getApplicationContext(),
//                                                            "errorString",
//                                                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                registerViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());

                mAuth.createUserWithEmailAndPassword(usernameEditText.getText().toString(), passwordEditText.getText().toString()).addOnCompleteListener((Activity) getContext(),
                        task -> {
                            if (task.isSuccessful()) {


                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                                    return;
                                                }

                                                // Get new FCM registration token
                                                String token = task.getResult();
                                                mToken = token;
                                                Log.d(TAG, "Refreshed token2: " + mToken);
                                                //Get-user-id
                                                String user_id = mAuth.getCurrentUser().getUid();
                                                DatabaseReference user_data = databaseReference.child(user_id);

                                                user_data.child("token_id").setValue(mToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
//                                        progressBar.setVisibility(View.INVISIBLE);
//                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
//                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
//                                        progressBar.setVisibility(View.INVISIBLE);
//                                        Toast.makeText(LoginActivity.this,"Failure",Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                user_data.child("username").setValue(usernameEditText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
//                                        progressBar.setVisibility(View.INVISIBLE);
//                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
//                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
//                                        progressBar.setVisibility(View.INVISIBLE);
//                                        Toast.makeText(LoginActivity.this,"Failure",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
//
//                                                user_data.child("password").setValue(passwordEditText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
////                                        progressBar.setVisibility(View.INVISIBLE);
////                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
////                                        finish();
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
////                                        progressBar.setVisibility(View.INVISIBLE);
////                                        Toast.makeText(LoginActivity.this,"Failure",Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
//                                                // Log and toast
//                                                String msg = getString(R.string.msg_token_fmt, token);
//                                                Log.d(TAG, msg);
//                                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                Log.d(TAG, "Refreshed token3: " + mToken);


//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        Fragment mFrag = new ZadachaFragment();
//                        ft.replace(R.id.zadachaFragment, mFrag);
//                        ft.commit();
                                //navigate back to loginfargment
//                                navController.navigateUp();


                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        task.getResult().getUser().getEmail(), Snackbar.LENGTH_LONG).show();

                                navController.clearBackStack(R.id.registerFragment);
                                navController.navigate(R.id.action_registerFragment_to_loginFragment2);
                            } else {
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        task.getException().getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                            }

                        });
            }
        });
    }

    private void updateUiWithUser(RegisterInUserView model) {
//        String welcome = getString(R.string.welcome) + model.getDisplayName();
//        // TODO : initiate successful logged in experience
//        if (getContext() != null && getContext().getApplicationContext() != null) {
//            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
//        }

    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void intNavcontroller() {
        Activity MainActivity = getActivity();
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class LatestFirebaseMessagingService extends FirebaseMessagingService {

        @Override
        public void onNewToken(String mToken) {
            super.onNewToken(mToken);
            Log.e("TOKEN", mToken);
        }

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
        }
    }
}