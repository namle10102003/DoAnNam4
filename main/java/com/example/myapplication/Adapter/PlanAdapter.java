package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Domain.Plan;
import com.example.myapplication.Fragment.MyPlanFragment;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ViewholderPlanBinding;

import java.util.ArrayList;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.Viewholder> {
    private final ArrayList<Plan> list;
    private final Context context;
    private final PlanAdapter.OnItemActionListener actionListener;

    public PlanAdapter(Context context, ArrayList<Plan> list, OnItemActionListener listener) {
        this.context = context;
        this.list = list;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public PlanAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderPlanBinding binding = ViewholderPlanBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanAdapter.Viewholder holder, int position) {
        Plan plan = list.get(position);

        holder.binding.titleTxt.setText(plan.getName());

        holder.binding.btnMore.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, holder.binding.btnMore);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.item_menu, popup.getMenu()); // you'll need to define this menu

            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.edit) {
                    actionListener.onEdit(plan);
                    return true;
                } else if (menuItem.getItemId() == R.id.delete) {
                    actionListener.onDelete(plan);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        ViewholderPlanBinding binding;

        public Viewholder(ViewholderPlanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemActionListener {
        void onEdit(Plan item);
        void onDelete(Plan item);
    }
}
