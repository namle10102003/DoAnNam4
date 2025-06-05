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
import com.example.myapplication.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private String username = "";
    ArrayList<Exercise> listExercise;
    ArrayList<Workout> listWorkout;
    private ExerciseService exerciseService;
    private ExerciseAdapter exerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        exerciseService = new ExerciseService();

        listExercise = new ArrayList<>();

        fetchListExercise();
        listWorkout = getListWorkout();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding.view1.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
        exerciseAdapter = new ExerciseAdapter(listExercise);
        binding.view1.setAdapter(exerciseAdapter);

        binding.viewWorkout.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
        binding.viewWorkout.setAdapter(new WorkoutAdapter(listWorkout));

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        username = prefs.getString("full_name", "defaultUsername");
        String userId = prefs.getString("user_id", null); // <-- Lấy user_id từ SharedPreferences

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

    private  ArrayList<Workout> getListWorkout() {
        ArrayList<Exercise> listExercise = getListExercise();
        // Example Sets
        ArrayList<Set> sets1 = new ArrayList<>();
        sets1.add(new Set(1, 101, 1, 0, 15, 60));
        sets1.add(new Set(2, 101, 1, 0, 12, 60));

        ArrayList<Set> sets2 = new ArrayList<>();
        sets2.add(new Set(3, 102, 2, 50, 10, 90));
        sets2.add(new Set(4, 102, 2, 60, 8, 90));

        ArrayList<Set> sets3 = new ArrayList<>();
        sets3.add(new Set(5, 103, 3, 0, 10, 120));
        sets3.add(new Set(6, 103, 3, 0, 8, 120));

        // Create Workouts
        ArrayList<Workout> workoutList = new ArrayList<>();
        workoutList.add(new Workout(1, 101, 1, new Date(), listExercise.get(0), sets1));
        workoutList.add(new Workout(1, 102, 2, new Date(), listExercise.get(1), sets2));
        workoutList.add(new Workout(1, 103, 3, new Date(), listExercise.get(2), sets3));

        return workoutList;
    }

    private ArrayList<Exercise> getListExercise() {
        ArrayList<Exercise> exerciseList = new ArrayList<>();

        exerciseList.add(new Exercise(
                1,
                "Seated Dumbbell Shoulder Press",
                "https://www.youtube.com/watch?v=B-aVuyhvLHU",
                "A strength exercise that targets the shoulders using dumbbells while seated.",
                MuscleEnum.SHOULDERS,
                MuscleEnum.TRICEPS,
                null
        ));

        exerciseList.add(new Exercise(
                2,
                "Band Chest Fly",
                "https://www.youtube.com/watch?v=JA8lXeb9yEo",
                "An isolation chest movement using resistance bands to target the pectoral muscles.",
                MuscleEnum.CHEST,
                MuscleEnum.SHOULDERS,
                null
        ));

        exerciseList.add(new Exercise(
                3,
                "Yoga Warrior I",
                "https://www.youtube.com/watch?v=Q9vQ3Vxg3IQ",
                "A foundational yoga pose that strengthens the legs, opens the hips, and improves balance.",
                MuscleEnum.QUADS,
                MuscleEnum.GLUTES,
                MuscleEnum.CALVES
        ));


        return exerciseList;
    }
}