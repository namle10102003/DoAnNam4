package com.example.myapplication.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapter.SetAdapter;
import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Domain.Workout;
import com.example.myapplication.databinding.ActivityWorkoutBinding;

public class WorkoutActivity extends AppCompatActivity {
ActivityWorkoutBinding binding;
private Workout workout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWorkoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        getObject();
        setVariable();

    }

    private void getObject() {
        workout= (Workout) getIntent().getSerializableExtra("object");
    }

    private void setVariable() {
        Exercise itemExercise = workout.getExercise();

        int resId=getResources().getIdentifier(itemExercise.getImageUrl(),"drawable",getPackageName());
        Glide.with(this)
                .load(resId)
                .into(binding.pic);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.titleTxt.setText(itemExercise.getName());
        binding.descriptionTxt.setText(itemExercise.getDescription());

        binding.watchTutorialLayout.setOnClickListener(v -> {
            String videoUrl = itemExercise.getVideoUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
            startActivity(intent);
        });

        binding.view3.setLayoutManager(new LinearLayoutManager(WorkoutActivity.this,LinearLayoutManager.VERTICAL,false));
        binding.view3.setAdapter(new SetAdapter(workout.getSets()));
    }
}