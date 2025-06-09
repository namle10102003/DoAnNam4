package com.example.myapplication.Fragment;

import android.app.AlertDialog;
import android.content.SharedPreferences;
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
import com.example.myapplication.Service.PlanService;
import com.example.myapplication.databinding.FragmentMyPlanBinding;
import com.example.myapplication.databinding.FragmentTodayWorkoutBinding;

import java.util.ArrayList;
import java.util.List;

public class MyPlanFragment extends Fragment {
    private FragmentMyPlanBinding binding;
    private ArrayList<Plan> listPlan;
    private PlanAdapter planAdapter;
    private PlanService planService;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyPlanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        planService = new PlanService();

        listPlan = new ArrayList<>();

        getUserId();
        //fetchListPlan();

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

    @Override
    public void onResume() {
        super.onResume();
        fetchListPlan();
    }

    private void addPlanListener() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_plan, null);
        EditText nameEditText = dialogView.findViewById(R.id.editName);

        new AlertDialog.Builder(requireContext())
                .setTitle("Create New Plan")
                .setView(dialogView)
                .setPositiveButton("Create", (dialog, which) -> {
                    String newPlanName = nameEditText.getText().toString();
                    Plan newPlan =  new Plan(0, 1, newPlanName);
                    createPlan(newPlan);
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
                    updatePlan(item);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePlanListener(Plan item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Plan")
                .setMessage("Are you sure you want to delete this plan?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deletePlan(item.getId());
                    Toast.makeText(requireContext(), "Plan deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", 0);
        String currentUserId = prefs.getString("user_id", "-1");
        userId = Integer.parseInt(currentUserId);
    }

    private void fetchListPlan() {
        planService.getPlansByUserId(userId, new PlanService.ListPlanDataListener() {
            @Override
            public void onPlansLoaded(List<Plan> plans) {
                listPlan.clear();
                listPlan.addAll(plans);
                planAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPlan(Plan plan) {
        planService.createPlan(plan, new PlanService.PlanDataListener() {
            @Override
            public void onPlanLoaded(Plan plan) {
                fetchListPlan();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePlan(Plan plan) {
        planService.updatePlan(plan, new PlanService.PlanDataListener() {
            @Override
            public void onPlanLoaded(Plan plan) {
                fetchListPlan();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletePlan(int id) {
        planService.deletePlan(id, new PlanService.ListPlanDataListener() {
            @Override
            public void onPlansLoaded(List<Plan> plans) {
                fetchListPlan();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

