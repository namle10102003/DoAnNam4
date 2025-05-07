package com.example.myapplication.Activity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.myapplication.R;

public class NavigationComponent extends LinearLayout {
    public NavigationComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.component_bottom_navigation, this, true);

        LinearLayout navChart = findViewById(R.id.navChart);
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navFavorite = findViewById(R.id.navFavorite);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        navChart.setOnClickListener(v -> {
            context.startActivity(new Intent(context, ChartActivity.class));
        });

        navHome.setOnClickListener(v -> {
            context.startActivity(new Intent(context, MainActivity.class));
        });
    }
}
