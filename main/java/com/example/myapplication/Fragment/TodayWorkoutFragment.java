package com.example.myapplication.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.Adapter.WorkoutAdapter;
import com.example.myapplication.Adapter.WorkoutScheduleAdapter;
import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Domain.Plan;
import com.example.myapplication.Domain.Set;
import com.example.myapplication.Domain.Workout;
import com.example.myapplication.Enum.MuscleEnum;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentTodayWorkoutBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TodayWorkoutFragment extends Fragment {

    private FragmentTodayWorkoutBinding binding;
    private ArrayList<Workout> listWorkout;
    private WorkoutScheduleAdapter workoutScheduleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTodayWorkoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listWorkout = getListWorkout();

        binding.todayWorkoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        workoutScheduleAdapter = new WorkoutScheduleAdapter(listWorkout, new WorkoutScheduleAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Workout item) {
                editWorkoutListener(item);
            }

            @Override
            public void onDelete(Workout item) {
                deleteWorkoutListener(item);
            }
        });
        binding.todayWorkoutRecyclerView.setAdapter(workoutScheduleAdapter);
    }

    private void editWorkoutListener(Workout workout) {
        ArrayList<Plan> listPlan = getListPlan();

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_workout, null);

        Spinner spinnerPlan = dialogView.findViewById(R.id.spinnerPlan);
        TextView textExercise = dialogView.findViewById(R.id.textviewExercise);
        EditText editDate = dialogView.findViewById(R.id.editDate);

        // Set up Plan Spinner
        ArrayAdapter<Plan> planAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, listPlan);
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlan.setAdapter(planAdapter);

        // Set up Exercise Text view
        textExercise.setText(workout.getExercise().getName());

        // Pre-select existing values if editing
        if (workout != null) {
            for (int i = 0; i < listPlan.size(); i++) {
                if (listPlan.get(i).getId() == workout.getPlanId()) {
                    spinnerPlan.setSelection(i);
                    break;
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            editDate.setText(sdf.format(workout.getDate()));
        }

        // Date picker
        editDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (workout.getDate() != null) {
                calendar.setTime(workout.getDate());
            }
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                editDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Show dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Workout")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    Plan selectedPlan = (Plan) spinnerPlan.getSelectedItem();
                    Date selectedDate = null;
                    try {
                        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(editDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Update the workout object
                    if (workout != null) {
                        workout.setPlanId(selectedPlan.getId());
                        workout.setPlanName(selectedPlan.getName());
                        workout.setDate(selectedDate); // if needed
                        // Notify adapter if you're updating a list
                        //Reload today workout list
                        workoutScheduleAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteWorkoutListener(Workout item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Workout")
                .setMessage("Are you sure you want to delete this workout?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    listWorkout.remove(item);
                    workoutScheduleAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Plan deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private ArrayList<Workout> getListWorkout() {
        ArrayList<Exercise> listExercise = getListExercise();

        ArrayList<Set> sets1 = new ArrayList<>();
        sets1.add(new Set(1, 101, 1, 0, 15, 60));
        sets1.add(new Set(2, 101, 1, 0, 12, 60));

        ArrayList<Set> sets2 = new ArrayList<>();
        sets2.add(new Set(3, 102, 2, 50, 10, 90));
        sets2.add(new Set(4, 102, 2, 60, 8, 90));

        ArrayList<Set> sets3 = new ArrayList<>();
        sets3.add(new Set(5, 103, 3, 0, 10, 120));
        sets3.add(new Set(6, 103, 3, 0, 8, 120));

        ArrayList<Workout> workoutList = new ArrayList<>();
        workoutList.add(new Workout(1, 101, 1, "Loss Weight", new Date(), listExercise.get(0), sets1));
        workoutList.add(new Workout(1, 102, 2, "Loss Weight", new Date(), listExercise.get(1), sets2));
        workoutList.add(new Workout(1, 103, 3, "Loss Weight", new Date(), listExercise.get(2), sets3));

        return workoutList;
    }

    private ArrayList<Exercise> getListExercise() {
        ArrayList<Exercise> exerciseList = new ArrayList<>();

        exerciseList.add(new Exercise(
                1,
                "Seated Dumbbell Shoulder Press",
                "seated_dumbbell_shoulder_press",
                "A strength exercise that targets the shoulders using dumbbells while seated.",
                MuscleEnum.SHOULDERS,
                MuscleEnum.TRICEPS,
                null
        ));

        exerciseList.add(new Exercise(
                2,
                "Band Chest Fly",
                "band_chest_fly",
                "An isolation chest movement using resistance bands to target the pectoral muscles.",
                MuscleEnum.CHEST,
                MuscleEnum.SHOULDERS,
                null
        ));

        exerciseList.add(new Exercise(
                3,
                "Yoga Warrior I",
                "yoga_warrior_i",
                "A foundational yoga pose that strengthens the legs, opens the hips, and improves balance.",
                MuscleEnum.QUADS,
                MuscleEnum.GLUTES,
                MuscleEnum.CALVES
        ));

        return exerciseList;
    }

    private ArrayList<Plan> getListPlan() {
        ArrayList<Plan> result = new ArrayList<>();

        result.add(new Plan(1, 1, "Lost weight"));
        result.add(new Plan(2, 1, "Build muscle"));

        return result;
    }
}
