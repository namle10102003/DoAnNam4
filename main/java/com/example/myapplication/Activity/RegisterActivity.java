package com.example.myapplication.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.Utils.ValidationUtil;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private TextView errorTextView, loginRedirectText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        errorTextView = findViewById(R.id.errorTextView);
        errorTextView.setTextColor(Color.RED);
        errorTextView.setVisibility(View.GONE);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            String usernameError = ValidationUtil.validateUsername(username);
            String passwordError = ValidationUtil.validatePassword(password);

            if (usernameError != null) {
                showError(usernameError);
            } else if (passwordError != null) {
                showError(passwordError);
            } else if (confirmPassword.isEmpty()) {
                showError("Please confirm your password");
            } else if (!password.equals(confirmPassword)) {
                showError("Passwords do not match");
            } else {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("registeredUsername", username);
                startActivity(intent);
                finish();
            }
        });

        setupLoginRedirectText();

        loginRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
    }

    private void setupLoginRedirectText() {
        String fullText = "Already have an account? Login now";
        SpannableString spannableString = new SpannableString(fullText);

        ForegroundColorSpan whiteColor = new ForegroundColorSpan(getResources().getColor(R.color.white));
        spannableString.setSpan(whiteColor, 0, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ForegroundColorSpan whiteColorForQuestionMark = new ForegroundColorSpan(getResources().getColor(R.color.white));
        spannableString.setSpan(whiteColorForQuestionMark, 23, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ForegroundColorSpan orangeColor = new ForegroundColorSpan(getResources().getColor(R.color.orange));
        spannableString.setSpan(orangeColor, 25, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginRedirectText.setText(spannableString);
    }
}