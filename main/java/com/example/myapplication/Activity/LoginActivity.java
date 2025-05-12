package com.example.myapplication.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.Utils.ValidationUtil;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private TextView errorTextView, registerTextView;
    private Button loginButton;

    private final HashMap<String, String> mockUsers = new HashMap<String, String>() {{
        put("nam", "Pass1234");
        put("long", "Secure123");
        put("dat", "Dat32145");
        put("sy", "Sy2024ok");
        put("tien", "Tien9999");
    }};

    private final HashMap<String, String> userIdMap = new HashMap<String, String>() {{
        put("nam", "1");
        put("long", "2");
        put("dat", "3");
        put("sy", "4");
        put("tien", "5");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);
        errorTextView = findViewById(R.id.errorTextView);
        errorTextView.setTextColor(Color.RED);
        errorTextView.setVisibility(View.GONE);

        setupRegisterSpan();

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            String usernameError = ValidationUtil.validateUsername(username);
            String passwordError = ValidationUtil.validatePassword(password);

            if (usernameError != null) {
                showError(usernameError);
            } else if (passwordError != null) {
                showError(passwordError);
            } else if (!mockUsers.containsKey(username) || !mockUsers.get(username).equals(password)) {
                showError("Incorrect username or password");
            } else {
                String userId = userIdMap.get(username);
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", username);
                editor.putString("user_id", userId);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
    }

    private void setupRegisterSpan() {
        String fullText = "Don't have an account? Register now";
        SpannableString spannable = new SpannableString(fullText);

        ForegroundColorSpan whiteColor = new ForegroundColorSpan(getResources().getColor(R.color.white));
        spannable.setSpan(whiteColor, 0, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ForegroundColorSpan orangeColor = new ForegroundColorSpan(getResources().getColor(R.color.orange));
        spannable.setSpan(clickableSpan, 24, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(orangeColor, 24, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        registerTextView.setText(spannable);
        registerTextView.setMovementMethod(LinkMovementMethod.getInstance());
        registerTextView.setHighlightColor(Color.TRANSPARENT);
    }
}