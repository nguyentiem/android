package com.example.home.ui.login.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home.R;

import com.example.home.ui.MainActivity;
import com.example.home.ui.login.ui.login.LoginViewModel;
import com.example.home.ui.login.ui.login.LoginViewModelFactory;
import com.example.home.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                switch (loginViewModel.getStatusLayout().getValue()) {
                    case StatusMode.LOGIN:
                        loginButton.setEnabled(loginFormState.isDataValid());
                        if (loginFormState.getUsernameError() != null) {
                            usernameEditText.setError(getString(loginFormState.getUsernameError()));
                        }
                        if (loginFormState.getPasswordError() != null) {
                            passwordEditText.setError(getString(loginFormState.getPasswordError()));
                        }
                        break;
                    case StatusMode.SIGNIN:
                        binding.actionSignin.setEnabled(loginFormState.isDataValid());
                        if (loginFormState.getUsernameError() != null) {
                            binding.susername.setError(getString(loginFormState.getUsernameError()));
                        }
                        if (loginFormState.getPasswordError() != null) {
                            binding.spassword.setError(getString(loginFormState.getPasswordError()));
                        }
                        break;
                    case StatusMode.FORGET_PASS:
                        break;
                    default:
                        break;
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
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
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        loginViewModel.getStatusLayout().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer aBoolean) {
                switch (aBoolean) {
                    case StatusMode.LOGIN:
                        binding.loginLayout.setVisibility(View.VISIBLE);
                        binding.signinLayout.setVisibility(View.GONE);
                        break;
                    case StatusMode.SIGNIN:
                        binding.loginLayout.setVisibility(View.GONE);
                        binding.signinLayout.setVisibility(View.VISIBLE);
                        break;
                    case StatusMode.FORGET_PASS:
                        break;
                    default:
                        break;
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
                loginViewModel.typeDataChanged(usernameEditText.getText().toString(),
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

        TextWatcher siginAfterTextChangedListener = new TextWatcher() {
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
                loginViewModel.typeDataChanged(binding.susername.getText().toString(),
                        binding.spassword.getText().toString());
            }
        };
        binding.susername.addTextChangedListener(siginAfterTextChangedListener);
        binding.spassword.addTextChangedListener(siginAfterTextChangedListener);
        binding.spassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.signin(binding.susername.getText().toString(),
                            binding.spassword.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        binding.actionSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViewModel.changeMode(StatusMode.SIGNIN);
            }
        });

        binding.backToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (loginViewModel.getStatusLayout().getValue()) {
                    case StatusMode.LOGIN:
//                        finish();
                        break;
                    case StatusMode.SIGNIN:
                        loginViewModel.changeMode(StatusMode.LOGIN);
                        break;
                    case StatusMode.FORGET_PASS:
                        loginViewModel.changeMode(StatusMode.LOGIN);
                        break;
                    default:
                        break;
                }

            }
        });

        binding.actionSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViewModel.changeMode(StatusMode.SIGNIN);
            }
        });

        binding.actionForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViewModel.changeMode(StatusMode.FORGET_PASS);
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}