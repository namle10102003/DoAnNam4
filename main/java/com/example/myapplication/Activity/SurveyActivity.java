package com.example.myapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class SurveyActivity extends AppCompatActivity {

    private Spinner spinnerGoal, spinnerDaysPerWeek, spinnerLevel;
    private RadioGroup injuryGroup;
    private Button btnSubmit;

    private String selectedGoal, selectedLevel, selectedInjury;
    private int selectedDaysPerWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        // Map views to IDs
        spinnerGoal = findViewById(R.id.goalSpinner);
        spinnerDaysPerWeek = findViewById(R.id.sessionsSpinner);
        spinnerLevel = findViewById(R.id.levelSpinner);
        injuryGroup = findViewById(R.id.injuryGroup);
        btnSubmit = findViewById(R.id.submitSurveyBtn);

        setupSpinners();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get injury selection from RadioGroup
                int selectedInjuryId = injuryGroup.getCheckedRadioButtonId();
                if (selectedInjuryId == -1) {
                    Toast.makeText(SurveyActivity.this, "Please select injury history", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton selectedRadioButton = findViewById(selectedInjuryId);
                selectedInjury = selectedRadioButton.getText().toString();

                if (validateInputs()) {
                    Intent intent = new Intent(SurveyActivity.this, MainActivity.class);
                    intent.putExtra("goal", selectedGoal);
                    intent.putExtra("daysPerWeek", selectedDaysPerWeek);
                    intent.putExtra("level", selectedLevel);
                    intent.putExtra("injury", selectedInjury);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SurveyActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSpinners() {
        // Training Goal spinner items from array resource
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                R.array.goals_array, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoal.setAdapter(goalAdapter);
        spinnerGoal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGoal = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGoal = null;
            }
        });

        // Sessions per week spinner
        ArrayAdapter<CharSequence> daysAdapter = ArrayAdapter.createFromResource(this,
                R.array.days_array, android.R.layout.simple_spinner_item);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDaysPerWeek.setAdapter(daysAdapter);
        spinnerDaysPerWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectedDaysPerWeek = Integer.parseInt(parent.getItemAtPosition(position).toString());
                } catch (NumberFormatException e) {
                    selectedDaysPerWeek = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDaysPerWeek = 0;
            }
        });

        // Training Level spinner
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this,
                R.array.levels_array, android.R.layout.simple_spinner_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(levelAdapter);
        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLevel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLevel = null;
            }
        });
    }

    private boolean validateInputs() {
        return selectedGoal != null && selectedLevel != null &&
                selectedInjury != null && selectedDaysPerWeek > 0;
    }
}