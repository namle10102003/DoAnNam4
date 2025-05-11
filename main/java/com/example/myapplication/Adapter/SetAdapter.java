package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Domain.Set;
import com.example.myapplication.databinding.ViewholderSetBinding;

import java.util.ArrayList;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.Viewholder> {
    private final ArrayList<Set> list;

    public SetAdapter(ArrayList<Set> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SetAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderSetBinding binding = ViewholderSetBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SetAdapter.Viewholder holder, int position) {
        Set set = list.get(position);

        holder.binding.titleTxt.setText("Set " + (position + 1));
        holder.binding.durationTxt.setText(set.getReps() + " reps | " + set.getWeight() + " kg");
        // Optional: handle clicks, remove/edit logic here
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        ViewholderSetBinding binding;

        public Viewholder(ViewholderSetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
