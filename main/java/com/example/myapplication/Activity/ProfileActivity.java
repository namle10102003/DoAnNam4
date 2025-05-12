package com.example.myapplication.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Domain.User;
import com.example.myapplication.R;

import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private EditText etEmail, etFullName, etPhone, etDob;
    private Spinner spGender;
    private Button btnLogout, btnEdit;

    private boolean isEditing = false;
    private String currentUserId;
    private User user;

    private final HashMap<String, User> userProfiles = new HashMap<String, User>() {{
        put("1", new User("1", "nam", "Pass1234", "Nam Nguyen", "nam@gmail.com", "0905000001", "1999-01-01", "Male"));
        put("2", new User("2", "long", "Secure123", "Long Tran", "long@gmail.com", "0905000002", "1998-02-02", "Male"));
        put("3", new User("3", "dat", "Dat32145", "Dat Pham", "dat@gmail.com", "0905000003", "2000-03-03", "Male"));
        put("4", new User("4", "sy", "Sy2024ok", "Sy Le", "sy@gmail.com", "0905000004", "1997-04-04", "Male"));
        put("5", new User("5", "tien", "Tien9999", "Tien Vo", "tien@gmail.com", "0905000005", "1996-05-05", "Male"));
    }};

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

        currentUserId = getIntent().getStringExtra("user_id");
        user = getUserData(currentUserId);

        if (user != null) {
            tvUsername.setText(user.getUsername());
            etFullName.setText(user.getFullName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhoneNumber());

            // Chuyển đổi "1999-01-01" sang "01/01/1999"
            String[] parts = user.getDateOfBirth().split("-");
            if (parts.length == 3) {
                String formattedDob = parts[2] + "/" + parts[1] + "/" + parts[0];
                etDob.setText(formattedDob);
            }

            spGender.setSelection(user.getGender().equals("Male") ? 0 : 1);
        }

        btnEdit.setOnClickListener(v -> {
            if (isEditing) {
                // Khi đang sửa, nhấn Save sẽ lưu dữ liệu và chuyển về chế độ xem
                String newFullName = etFullName.getText().toString();
                String newEmail = etEmail.getText().toString();
                String newPhone = etPhone.getText().toString();
                String newDob = etDob.getText().toString();
                String newGender = spGender.getSelectedItem().toString();

                if (!isValidDate(newDob)) {
                    etDob.setError("Invalid date. Please select a valid date.");
                    return;
                } else {
                    etDob.setError(null); // Xóa dấu chấm than nếu hợp lệ
                }

                // Cập nhật thông tin người dùng
                user.setFullName(newFullName);
                user.setEmail(newEmail);
                user.setPhoneNumber(newPhone);
                user.setDateOfBirth(newDob);
                user.setGender(newGender);

                // Đổi nút về Edit Info sau khi lưu
                btnEdit.setText("Edit Info");
                isEditing = false;

                // Tắt sửa các trường thông tin
                etEmail.setEnabled(false);
                etFullName.setEnabled(false);
                etPhone.setEnabled(false);
                etDob.setEnabled(false);
                spGender.setEnabled(false);
            } else {
                // Chuyển sang chế độ sửa khi đang ở chế độ xem
                btnEdit.setText("Save");
                isEditing = true;

                // Bật sửa các trường thông tin
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

    private User getUserData(String id) {
        return userProfiles.get(id);
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