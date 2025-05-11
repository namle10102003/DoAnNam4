package com.example.myapplication.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Enum.MuscleEnum;
import com.example.myapplication.databinding.ActivityExerciseBinding;

public class ExerciseActivity extends AppCompatActivity {
    ActivityExerciseBinding binding;
    private Exercise exercise;

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
        exercise = (Exercise) getIntent().getSerializableExtra("object");
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
        binding.startBtn.setOnClickListener(v -> {
            // Handle start button action (e.g., open tracking)
        });

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
}
