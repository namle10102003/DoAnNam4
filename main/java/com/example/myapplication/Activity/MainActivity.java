package com.example.myapplication.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.Adapter.ExerciseAdapter;
import com.example.myapplication.Adapter.WorkoutAdapter;
import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Domain.Lesson;
import com.example.myapplication.Domain.Set;
import com.example.myapplication.Domain.Workout;
import com.example.myapplication.Enum.MuscleEnum;
import com.example.myapplication.R;
import com.example.myapplication.Service.ExerciseService;
import com.example.myapplication.Service.WorkoutService;
import com.example.myapplication.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private String username = "";
    ArrayList<Exercise> listExercise;
    ArrayList<Workout> listWorkout;
    private ExerciseService exerciseService;
    private ExerciseAdapter exerciseAdapter;
    private WorkoutService workoutService;
    private WorkoutAdapter workoutAdapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        exerciseService = new ExerciseService();
        workoutService = new WorkoutService();

        listExercise = new ArrayList<>();
        listWorkout = new ArrayList<>();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding.view1.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
        exerciseAdapter = new ExerciseAdapter(listExercise);
        binding.view1.setAdapter(exerciseAdapter);

        binding.viewWorkout.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
        workoutAdapter = new WorkoutAdapter(listWorkout);
        binding.viewWorkout.setAdapter(workoutAdapter);

        binding.seeAllWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        binding.seeAllExercise.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AllExerciseActivity.class);
            startActivity(intent);
        });

        getUserInfo();
        fetchListExercise();
        fetchListWorkout();
    }

    private void getUserInfo() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        username = prefs.getString("full_name", "defaultUsername");
        String currentUserId = prefs.getString("user_id", "-1");
        userId = Integer.parseInt(currentUserId);
    }

    private void fetchListExercise() {
        exerciseService.GetAll(new ExerciseService.ListExerciseDataListener() {
            @Override
            public void onExerciseLoaded(List<Exercise> exercises) {
                listExercise.clear();
                int limit = Math.min(10, exercises.size());
                listExercise.addAll(exercises.subList(0, limit));
                exerciseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchListWorkout() {
        workoutService.getByUserId(userId, new WorkoutService.ListWorkoutDataListener() {
            @Override
            public void onWorkoutsLoaded(List<Workout> workouts) {
                listWorkout.clear();
                Date today = new Date();

                for (Workout workout : workouts) {
                    if (isSameDay(workout.getDate(), today)) {
                        listWorkout.add(workout);
                    }
                }

                workoutAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isSameDay(Date date1, Date date2) {
        // Implement a method to check if two Date objects represent the same day
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }
}