package com.example.myapplication.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Domain.Lesson;
import com.example.myapplication.databinding.ViewholderExerciseBinding;
import com.example.myapplication.databinding.ViewholderWorkoutBinding;

import java.util.ArrayList;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.Viewholder> {

    private final ArrayList<Lesson> list;
    private Context context;

    public LessonsAdapter(ArrayList<Lesson> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public LessonsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        ViewholderExerciseBinding binding=ViewholderExerciseBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonsAdapter.Viewholder holder, int position) {
        holder.binding.titleTxt.setText(list.get(position).getTitle());
        holder.binding.durationTxt.setText(list.get(position).getDuration());

        int resId=context.getResources().getIdentifier(list.get(position).getPicPath(),"drawable",context.getPackageName());
        Glide.with(context)
                .load(resId)
                .into(holder.binding.pic);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+list.get(position).getLink()));
                Intent webIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+list.get(position).getLink()));

                try {
                    context.startActivity(appIntent);
                }catch (ActivityNotFoundException ex) {
                    context.startActivity(webIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderExerciseBinding binding;
        public Viewholder(ViewholderExerciseBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
