package com.example.myapplication.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

        navProfile.setOnClickListener(v -> {
            // Lấy user_id từ SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String userId = prefs.getString("user_id", null);  // Lấy user_id từ SharedPreferences

            // Truyền user_id cho ProfileActivity
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("user_id", userId);  // Truyền user_id vào ProfileActivity
            context.startActivity(intent);
        });


    }
}
