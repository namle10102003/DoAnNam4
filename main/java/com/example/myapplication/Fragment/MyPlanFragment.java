package com.example.myapplication.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.Activity.ChartActivity;
import com.example.myapplication.Adapter.PlanAdapter;
import com.example.myapplication.Domain.Plan;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMyPlanBinding;
import com.example.myapplication.databinding.FragmentTodayWorkoutBinding;

import java.util.ArrayList;

public class MyPlanFragment extends Fragment {
    private FragmentMyPlanBinding binding;
    private ArrayList<Plan> listPlan;
    private PlanAdapter planAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyPlanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listPlan = getListPlan();

        binding.myPlanRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        planAdapter = new PlanAdapter(requireContext(), listPlan, new PlanAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Plan item) {
                editPlanListener(item);
            }

            @Override
            public void onDelete(Plan item) {
                deletePlanListener(item);
            }
        });
        binding.myPlanRecyclerView.setAdapter(planAdapter);

        binding.btnAddRecord.setOnClickListener(v -> addPlanListener());
    }

    private void addPlanListener() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_plan, null);
        EditText nameEditText = dialogView.findViewById(R.id.editName);

        new AlertDialog.Builder(requireContext())
                .setTitle("Create New Plan")
                .setView(dialogView)
                .setPositiveButton("Create", (dialog, which) -> {
                    String newPlan = nameEditText.getText().toString();
                    listPlan.add(new Plan(listPlan.size(), 1, newPlan));
                    // Optional: Update date to current date if needed
                    planAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void editPlanListener(Plan item) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_plan, null);
        EditText nameEditText = dialogView.findViewById(R.id.editName);

        nameEditText.setText(item.getName());

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Plan")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = nameEditText.getText().toString();
                    item.setName(newName);
                    // Optional: Update date to current date if needed
                    planAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePlanListener(Plan item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Plan")
                .setMessage("Are you sure you want to delete this plan?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    listPlan.remove(item);
                    planAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Plan deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private ArrayList<Plan> getListPlan() {
        ArrayList<Plan> result = new ArrayList<>();

        result.add(new Plan(1, 1, "Lost weight"));
        result.add(new Plan(2, 1, "Build muscle"));

        return result;
    }
}

