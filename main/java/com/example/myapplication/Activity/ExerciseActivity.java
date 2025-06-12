package com.example.myapplication.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Domain.Plan;
import com.example.myapplication.Domain.Set;
import com.example.myapplication.Domain.Workout;
import com.example.myapplication.Enum.MuscleEnum;
import com.example.myapplication.R;
import com.example.myapplication.Service.PlanService;
import com.example.myapplication.Service.WorkoutService;
import com.example.myapplication.databinding.ActivityExerciseBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseActivity extends AppCompatActivity {
    ActivityExerciseBinding binding;
    private Exercise exercise;
    private ArrayList<Plan> listPlan;
    private int userId;
    private PlanService planService;
    private WorkoutService workoutService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Optional: Fullscreen image
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        getObject();
        setVariables();
    }

    private void getObject() {
        planService = new PlanService();
        workoutService = new WorkoutService();

        exercise = (Exercise) getIntent().getSerializableExtra("object");
        getUserId();
        listPlan = new ArrayList<>();
        fetchListPlan();
    }

    private void setVariables() {
        // Set image
        int resId = getResources().getIdentifier(exercise.getImageUrl(), "drawable", getPackageName());
        Glide.with(this).load(resId).into(binding.pic);

        // Set title
        binding.titleTxt.setText(exercise.getName());

        // Set back button
        binding.backBtn.setOnClickListener(view -> finish());

        // Set description if any (optional field)
        if (exercise.getDescription() != null) {
            binding.descriptionTxt.setText(exercise.getDescription());
        } else {
            binding.descriptionTxt.setText("No description available.");
        }

        // Set level (placeholder — update this based on your logic)
        binding.levelTxt.setText("Intermediate");

        // Add target muscles
        addMuscleIfNotNull(exercise.getTargetMuscle1());
        addMuscleIfNotNull(exercise.getTargetMuscle2());
        addMuscleIfNotNull(exercise.getTargetMuscle3());

        // Optional: button listener
        binding.startBtn.setOnClickListener(v -> createWorkoutDialog());

        binding.watchTutorialLayout.setOnClickListener(v -> {
            String videoUrl = exercise.getVideoUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
            startActivity(intent);
        });

    }

    private void addMuscleIfNotNull(MuscleEnum muscle) {
        if (muscle != null) {
            TextView textView = new TextView(this);
            textView.setText(muscle.toString());
            textView.setTextColor(getResources().getColor(android.R.color.white));
            textView.setTextSize(12);
            textView.setPadding(8, 0, 8, 0);
            binding.targetMuscleLayout.addView(textView);
        }
    }

    private void getUserId() {
        userId = 1;
    }

    private void fetchListPlan() {
        planService.getPlansByUserId(userId, new PlanService.ListPlanDataListener() {
            @Override
            public void onPlansLoaded(List<Plan> plans) {
                listPlan.clear();
                listPlan.addAll(plans);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ExerciseActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createWorkout(Workout workout) {
        workoutService.createWorkout(workout, new WorkoutService.WorkoutDataListener() {
            @Override
            public void onWorkoutLoaded(Workout workout) {
                String message = "Create workout successfully";
                Toast.makeText(ExerciseActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ExerciseActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createWorkoutDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_workout, null);

        Spinner spinnerPlan = dialogView.findViewById(R.id.spinnerPlan);
        TextView textExercise = dialogView.findViewById(R.id.textviewExercise);
        EditText editDate = dialogView.findViewById(R.id.editDate);
        EditText editSets = dialogView.findViewById(R.id.editSets);
        EditText editReps = dialogView.findViewById(R.id.editReps);
        EditText editVolume = dialogView.findViewById(R.id.editVolume);
        EditText editRestTime = dialogView.findViewById(R.id.editRestTime);

        // Set up Plan Spinner
        ArrayAdapter<Plan> planAdapter = new ArrayAdapter<>(ExerciseActivity.this, android.R.layout.simple_spinner_item, listPlan);
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlan.setAdapter(planAdapter);

        // You might want to show the selected exercise name if already selected somewhere in your UI
        Exercise selectedExercise = exercise; // Replace with your actual logic
        if (selectedExercise != null) {
            textExercise.setText(selectedExercise.getName());
        }

        // Date Picker
        editDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(ExerciseActivity.this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                editDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Show dialog
        new AlertDialog.Builder(ExerciseActivity.this)
                .setTitle("Create Workout")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    Plan selectedPlan = (Plan) spinnerPlan.getSelectedItem();
                    Date selectedDate = null;
                    try {
                        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(editDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Get values from input fields
                    int sets = Integer.parseInt(editSets.getText().toString());
                    int reps = Integer.parseInt(editReps.getText().toString());
                    int volume = Integer.parseInt(editVolume.getText().toString());
                    int restTime = Integer.parseInt(editRestTime.getText().toString());

                    if (sets < 0 || reps < 1 || volume < 0 || restTime < 1) {
                        String message = "Invalid input";
                        Toast.makeText(ExerciseActivity.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create new Workout
                    Workout newWorkout = new Workout(
                            selectedPlan.getId(),
                            0,
                            selectedExercise.getId(),
                            selectedPlan.getName(),
                            selectedDate,
                            selectedExercise,
                            new ArrayList<>());

                    newWorkout.setSets(generateSets(sets, selectedExercise.getId(), reps, volume, restTime));

                    // Save to your list or database
                    createWorkout(newWorkout);

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public ArrayList<Set> generateSets(int numSet, int exerciseId, int repPerSet, int volume, int restTime) {
        ArrayList<Set> result = new ArrayList<>();

        for (int i = 0; i < numSet; i++) {
            result.add(new Set(0, 0, exerciseId, volume, repPerSet, restTime));
        }

        return result;
    }

}
