package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Button nextButton = findViewById(R.id.onboarding_button);
        nextButton.setOnClickListener(v -> {
            com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
            SessionManager sessionManager = new SessionManager(this);

            if (auth.getCurrentUser() != null && sessionManager.isLoggedIn()) {
                startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
            }
            finish();
        });
    }
}
