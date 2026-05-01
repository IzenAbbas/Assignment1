package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private SessionManager sessionManager;
    private EditText emailInput;
    private EditText passwordInput;
    private CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        if (auth.getCurrentUser() != null && sessionManager.isLoggedIn()) {
            openMainScreen();
            return;
        }

        emailInput = findViewById(R.id.login_email);
        passwordInput = findViewById(R.id.login_password);
        rememberMe = findViewById(R.id.login_remember_me);

        ImageButton backButton = findViewById(R.id.login_back_button);
        Button loginButton = findViewById(R.id.login_button);
        TextView goToSignup = findViewById(R.id.login_register_link);

        backButton.setOnClickListener(v -> finish());

        goToSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!isLoginInputValid(email, password)) {
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        sessionManager.setLoggedIn(rememberMe.isChecked());
                        if (!rememberMe.isChecked()) {
                            Toast.makeText(this, R.string.remember_me_hint, Toast.LENGTH_SHORT).show();
                        }
                        openMainScreen();
                    } else {
                        Toast.makeText(this, getString(R.string.login_failed, getErrorMessage(task.getException())), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isLoginInputValid(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailInput.setError(getString(R.string.error_email_required));
            emailInput.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError(getString(R.string.error_password_required));
            passwordInput.requestFocus();
            return false;
        }

        return true;
    }

    private void openMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String getErrorMessage(Exception exception) {
        if (exception == null || TextUtils.isEmpty(exception.getMessage())) {
            return getString(R.string.error_unknown);
        }
        return exception.getMessage();
    }
}
