package com.example.myapplication.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Domain.Plan;
import com.example.myapplication.Domain.Workout;
import com.example.myapplication.Enum.DayOfWeek;
import com.example.myapplication.R;
import com.example.myapplication.Service.ExerciseService;
import com.example.myapplication.Service.PlanService;
import com.example.myapplication.Service.WorkoutService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SurveyActivity extends AppCompatActivity {

    private Spinner spinnerGoal, spinnerLevel;
    private RadioGroup injuryGroup;
    private EditText planNameTxt;
    private Button btnSubmit;

    private String planName, selectedGoal, selectedLevel, selectedInjury;
    private int selectedDaysPerWeek;
    private ArrayList<DayOfWeek> listSelectedDays;
    private ExerciseService exerciseService;
    private WorkoutService workoutService;
    private PlanService planService;
    private ArrayList<Exercise> listExercise;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        listExercise = new ArrayList<>();

        exerciseService = new ExerciseService();
        workoutService = new WorkoutService();
        planService = new PlanService();

        // Map views
        spinnerGoal = findViewById(R.id.goalSpinner);
        spinnerLevel = findViewById(R.id.levelSpinner);
        injuryGroup = findViewById(R.id.injuryGroup);
        planNameTxt = findViewById(R.id.planNameTxt);
        btnSubmit = findViewById(R.id.submitSurveyBtn);

        setupSpinners();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelectedDays();

                int selectedInjuryId = injuryGroup.getCheckedRadioButtonId();
                if (selectedInjuryId == -1) {
                    Toast.makeText(SurveyActivity.this, "Please select injury history", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedInjuryId);
                selectedInjury = selectedRadioButton.getText().toString();

                planName = planNameTxt.getText().toString().trim();

                if (planName.isEmpty()) {
                    planName = "New Plan";
                }

                if (validateInputs()) {
                    createPlanWorkout();
                } else {
                    Toast.makeText(SurveyActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getUserId();
        fetchListExercise();
    }

    private void getSelectedDays() {
        ToggleButton mondayBtn = findViewById(R.id.btnMonday);
        ToggleButton tuesdayBtn = findViewById(R.id.btnTuesday);
        ToggleButton wednesdayBtn = findViewById(R.id.btnWednesday);
        ToggleButton thursdayBtn = findViewById(R.id.btnThursday);
        ToggleButton fridayBtn = findViewById(R.id.btnFriday);
        ToggleButton saturdayBtn = findViewById(R.id.btnSaturday);
        ToggleButton sundayBtn = findViewById(R.id.btnSunday);

        listSelectedDays = new ArrayList<>();

        if (mondayBtn.isChecked()) listSelectedDays.add(DayOfWeek.MONDAY);
        if (tuesdayBtn.isChecked()) listSelectedDays.add(DayOfWeek.TUESDAY);
        if (wednesdayBtn.isChecked()) listSelectedDays.add(DayOfWeek.WEDNESDAY);
        if (thursdayBtn.isChecked()) listSelectedDays.add(DayOfWeek.THURSDAY);
        if (fridayBtn.isChecked()) listSelectedDays.add(DayOfWeek.FRIDAY);
        if (saturdayBtn.isChecked()) listSelectedDays.add(DayOfWeek.SATURDAY);
        if (sundayBtn.isChecked()) listSelectedDays.add(DayOfWeek.SUNDAY);

        selectedDaysPerWeek = listSelectedDays.size();
    }

    private void createPlanWorkout() {
        Plan newPlan = new Plan(0, userId, planName);
        planService.createPlan(newPlan, new PlanService.PlanDataListener() {
            @Override
            public void onPlanLoaded(Plan plan) {
                Map<DayOfWeek, List<Workout>> weeklyWorkout = generateListWorkout();
                List<Workout> workoutList = convertScheduleToDatePlan(weeklyWorkout, plan.getId());
                Log.d("ListPlan", "" + weeklyWorkout.size());
                Log.d("ListWorkout", "" + workoutList.size());

                workoutService.createListWorkout(workoutList, new WorkoutService.ListWorkoutDataListener() {
                    @Override
                    public void onWorkoutsLoaded(List<Workout> workouts) {
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(SurveyActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SurveyActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Map<DayOfWeek, List<Workout>> generateListWorkout() {
        Set<Integer> injuryRisk = Set.of(4, 5, 6, 7, 22); // high-risk exercises

        Map<String, List<Integer>> goalMap = Map.of(
                "Lose Weight", List.of(3, 18, 24, 11, 26, 28, 25),
                "Build Muscle", List.of(1, 4, 5, 6, 7, 8, 10, 15, 16, 20, 21, 22, 23),
                "Improve Endurance", List.of(3, 11, 18, 24, 28, 10, 29, 26, 25),
                "General Fitness", List.of(3, 6, 8, 11, 13, 14, 24, 25, 26)
        );

        Map<String, int[]> levelMap = Map.of(
                "Beginner", new int[]{3, 12, 10},
                "Intermediate", new int[]{4, 10, 15},
                "Advanced", new int[]{5, 8, 20}
        );

        int[] levelConfig = levelMap.getOrDefault(selectedLevel, new int[]{3, 12, 10});
        int sets = levelConfig[0];
        int reps = levelConfig[1];
        int weight = levelConfig[2];

        int exercisesPerDay = Math.min(6, 3 + selectedDaysPerWeek / 2);

        List<Integer> goalExerciseIds = goalMap.getOrDefault(selectedGoal, new ArrayList<>());

        List<Exercise> filtered = new ArrayList<>();
        for (Exercise e : listExercise) {
            if (goalExerciseIds.contains(e.getId())) {
                if (!selectedInjury.equalsIgnoreCase("yes") || !injuryRisk.contains(e.getId())) {
                    filtered.add(e);
                }
            }
        }

        Collections.shuffle(filtered);

        return generateWorkoutForWeek(filtered, selectedDaysPerWeek, exercisesPerDay, sets, reps, weight);
    }

    private Map<DayOfWeek, List<Workout>> generateWorkoutForWeek(List<Exercise> exercises,
                                                                 int daysPerWeek, int exercisesPerDay,
                                                                 int set, int rep, int weight) {
        Map<DayOfWeek, List<Workout>> weeklyPlan = new LinkedHashMap<>();

        List<DayOfWeek> Days = new ArrayList<>();
        Days.add(DayOfWeek.MONDAY);
        Days.add(DayOfWeek.TUESDAY);
        Days.add(DayOfWeek.WEDNESDAY);
        Days.add(DayOfWeek.THURSDAY);
        Days.add(DayOfWeek.FRIDAY);
        Days.add(DayOfWeek.SATURDAY);
        Days.add(DayOfWeek.SUNDAY);

        List<DayOfWeek> chosenDays = listSelectedDays;

        for (DayOfWeek day : Days) {
            if (chosenDays.contains(day)) {
                Collections.shuffle(exercises);
                List<Workout> entries = new ArrayList<>();
                for (int i = 0; i < Math.min(exercisesPerDay, exercises.size()); i++) {
                    ArrayList<com.example.myapplication.Domain.Set> sets = new ArrayList<>();
                    for (int j = 0; j < set; j++) {
                        sets.add(new com.example.myapplication.Domain.Set(0, 0,
                                exercises.get(i).getId(), weight, rep, 60));
                    }
                    entries.add(new Workout(0, 0, exercises.get(i).getId(),
                            planName, new Date(), exercises.get(i), sets));
                }
                weeklyPlan.put(day, entries);
            }
            else {
                weeklyPlan.put(day, List.of());
            }
        }

        return weeklyPlan;
    }

    public static List<Workout> convertScheduleToDatePlan(Map<DayOfWeek, List<Workout>> weeklySchedule, int planId) {
        List<Workout> plan = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(); // Start from today

        for (int i = 0; i < 30; i++) {
            // Convert Calendar's day to DayOfWeek enum
            int calendarDay = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 7 = Saturday
            DayOfWeek customDay = getCustomDayOfWeek(calendarDay); // Map to DayOfWeek (1 = Monday)

            if (weeklySchedule.containsKey(customDay)) {
                Date workoutDate = stripTime(calendar.getTime()); // optional: clear time part

                for (Workout workout : weeklySchedule.get(customDay)) {
                    Workout copy = new Workout(workout);
                    copy.setPlanId(planId);
                    copy.setDate(workoutDate);
                    plan.add(copy);
                }
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to next day
        }

        plan.sort(Comparator.comparing(Workout::getDate));
        return plan;
    }

    public static Date stripTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static DayOfWeek getCustomDayOfWeek(int calendarDay) {
        switch (calendarDay) {
            case Calendar.MONDAY: return DayOfWeek.MONDAY;
            case Calendar.TUESDAY: return DayOfWeek.TUESDAY;
            case Calendar.WEDNESDAY: return DayOfWeek.WEDNESDAY;
            case Calendar.THURSDAY: return DayOfWeek.THURSDAY;
            case Calendar.FRIDAY: return DayOfWeek.FRIDAY;
            case Calendar.SATURDAY: return DayOfWeek.SATURDAY;
            case Calendar.SUNDAY: return DayOfWeek.SUNDAY;
            default: throw new IllegalArgumentException("Invalid calendar day: " + calendarDay);
        }
    }

    private void fetchListExercise() {
        exerciseService.GetAll(new ExerciseService.ListExerciseDataListener() {
            @Override
            public void onExerciseLoaded(List<Exercise> exercises) {
                listExercise.clear();
                listExercise.addAll(exercises);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SurveyActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserId() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String currentUserId = prefs.getString("user_id", "-1");
        userId = Integer.parseInt(currentUserId);
    }

    private void setupSpinners() {
        // Training Goal spinner
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                R.array.goals_array, R.layout.spinner_item_white);
        goalAdapter.setDropDownViewResource(R.layout.spinner_dropdown_black);
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

        // Training Level spinner
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this,
                R.array.levels_array, R.layout.spinner_item_white);
        levelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_black);
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
                selectedInjury != null && selectedDaysPerWeek > 0 && planName != null;
    }
}