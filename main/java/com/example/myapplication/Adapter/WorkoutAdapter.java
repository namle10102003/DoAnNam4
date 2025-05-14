package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Activity.WorkoutActivity;
import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Domain.Plan;
import com.example.myapplication.Domain.Workout;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ViewholderWorkoutBinding;

import java.util.ArrayList;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.Viewholder> {
    private final ArrayList<Workout> list;
    private Context context;

    public WorkoutAdapter(ArrayList<Workout> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public WorkoutAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        ViewholderWorkoutBinding binding=ViewholderWorkoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutAdapter.Viewholder holder, int position) {
        Exercise itemExercise = list.get(position).getExercise();

        holder.binding.titleTxt.setText(itemExercise.getName());
        int resId=context.getResources().getIdentifier(itemExercise.getImageUrl(),"drawable",context.getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(resId)
                .into(holder.binding.pic);

        holder.binding.setTxt.setText(list.get(position).getNumberOfSet() + " sets");

        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent=new Intent(context, WorkoutActivity.class);
            intent.putExtra("object",list.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderWorkoutBinding binding;
        public Viewholder(ViewholderWorkoutBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
