package com.example.myapplication.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Domain.User;
import com.example.myapplication.R;
import com.example.myapplication.Service.UserService;

import java.util.Calendar;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private EditText etEmail, etFullName, etPhone, etDob;
    private Spinner spGender;
    private Button btnLogout, btnEdit;

    private boolean isEditing = false;
    private String currentUserId;
    private User user;

    private final UserService userService = new UserService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUsername = findViewById(R.id.tvUsername);
        etEmail = findViewById(R.id.etEmail);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDob);
        spGender = findViewById(R.id.spGender);
        btnLogout = findViewById(R.id.btnLogout);
        btnEdit = findViewById(R.id.btnEdit);

        etDob.setInputType(0);
        etDob.setFocusable(false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(adapter);
        spGender.setEnabled(false);

        getUserId();
        fetchUserData();

        btnEdit.setOnClickListener(v -> {
            if (user == null) return;

            if (isEditing) {
                // Save
                String newFullName = etFullName.getText().toString();
                String newEmail = etEmail.getText().toString();
                String newPhone = etPhone.getText().toString();
                String newDob = etDob.getText().toString();
                String newGender = spGender.getSelectedItem().toString();

                if (!isValidDate(newDob)) {
                    etDob.setError("Invalid date. Please select a valid date.");
                    return;
                } else {
                    etDob.setError(null);
                }

                user.setFullName(newFullName);
                user.setEmail(newEmail);
                user.setPhoneNumber(newPhone);
                user.setDateOfBirth(newDob);
                user.setGender(newGender);

                updateUser(user);

                btnEdit.setText("Edit Info");
                isEditing = false;

                etEmail.setEnabled(false);
                etFullName.setEnabled(false);
                etPhone.setEnabled(false);
                etDob.setEnabled(false);
                spGender.setEnabled(false);
            } else {
                // Edit
                btnEdit.setText("Save");
                isEditing = true;

                etEmail.setEnabled(true);
                etFullName.setEnabled(true);
                etPhone.setEnabled(true);
                etDob.setEnabled(true);
                spGender.setEnabled(true);
            }
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        etDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        etDob.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void fetchUserData() {
        if (currentUserId != null) {
            userService.getUserById(currentUserId, new UserService.UserDataListener() {
                @Override
                public void onUsersLoaded(User users) {
                    user = users;
                    displayUserData();
                }

                @Override
                public void onError(String message) {
                    tvUsername.setText("Failed to load user");
                    Toast.makeText(ProfileActivity.this, "Error loading profile: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getUserId() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        currentUserId = prefs.getString("user_id", "-1");
    }

    private void updateUser(User user) {
        userService.update(user, new UserService.UserDataListener() {
            @Override
            public void onUsersLoaded(User users) {
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("full_name", users.getFullName());
                editor.apply();

                fetchUserData();
            }

            @Override
            public void onError(String message) {
                tvUsername.setText("Failed to load user");
                Toast.makeText(ProfileActivity.this, "Error loading profile: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserData() {
        if (user == null) return;

        tvUsername.setText(user.getUsername());
        etFullName.setText(user.getFullName());
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhoneNumber());

        String[] parts = user.getDateOfBirth().split("/");
        if (parts.length == 3) {
            String formattedDob = parts[0] + "/" + parts[1] + "/" + parts[2];
            etDob.setText(formattedDob);
        }

        spGender.setSelection(user.getGender().equalsIgnoreCase("Male") ? 0 : 1);
    }

    private boolean isValidDate(String dateStr) {
        try {
            String[] parts = dateStr.split("/");
            if (parts.length != 3) return false;
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;
            int year = Integer.parseInt(parts[2]);

            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setLenient(false);
            selectedDate.set(year, month, day);
            selectedDate.getTime();

            Calendar today = Calendar.getInstance();
            return !selectedDate.after(today);
        } catch (Exception e) {
            return false;
        }
    }
}