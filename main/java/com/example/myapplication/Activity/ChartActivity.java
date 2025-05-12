package com.example.myapplication.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.app.AlertDialog;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.example.myapplication.Adapter.BodyTrackAdapter;
import com.example.myapplication.Domain.BodyTrack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

import com.example.myapplication.R;

public class ChartActivity extends AppCompatActivity {

    private LineChart lineChart;
    private Spinner timeRangeSpinner;
    private Spinner bodyMeasureSpinner;
    private RecyclerView bodyTrackRecyclerView;
    private ArrayList<BodyTrack> bodyTrackList;
    private BodyTrackAdapter bodyTrackAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        lineChart = findViewById(R.id.lineChart);
        timeRangeSpinner = findViewById(R.id.timeRangeSpinner);
        bodyMeasureSpinner = findViewById(R.id.bodyMeasureSpinner);

        bodyTrackRecyclerView = findViewById(R.id.historyRecyclerView);
        bodyTrackList = generateDummyBodyTracks();

        setupChart();
        loadChartData();

        timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadChartData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bodyMeasureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadChartData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bodyTrackAdapter = new BodyTrackAdapter(this, bodyTrackList, new BodyTrackAdapter.OnItemActionListener() {
            @Override
            public void onEdit(BodyTrack item) {
                View dialogView = LayoutInflater.from(ChartActivity.this).inflate(R.layout.dialog_edit_bodytrack, null);

                EditText heightEditText = dialogView.findViewById(R.id.editHeight);
                EditText weightEditText = dialogView.findViewById(R.id.editWeight);

                heightEditText.setText(String.valueOf(item.Height));
                weightEditText.setText(String.valueOf(item.Weight));

                new AlertDialog.Builder(ChartActivity.this)
                        .setTitle("Edit Record")
                        .setView(dialogView)
                        .setPositiveButton("Save", (dialog, which) -> {
                            try {
                                item.Height = Integer.parseInt(heightEditText.getText().toString());
                                item.Weight = Integer.parseInt(weightEditText.getText().toString());
                                // Optional: Update date to current date if needed
                                bodyTrackAdapter.notifyDataSetChanged();
                                loadChartData();
                            } catch (NumberFormatException e) {
                                Toast.makeText(ChartActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onDelete(BodyTrack item) {
                new AlertDialog.Builder(ChartActivity.this)
                        .setTitle("Delete Record")
                        .setMessage("Are you sure you want to delete this record?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            bodyTrackList.remove(item);
                            bodyTrackAdapter.notifyDataSetChanged();
                            loadChartData();
                            Toast.makeText(ChartActivity.this, "Record deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        bodyTrackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bodyTrackRecyclerView.setAdapter(bodyTrackAdapter);

        Button btnAddRecord = findViewById(R.id.btnAddRecord);
        btnAddRecord.setOnClickListener(v -> showAddRecordDialog());
    }

    private void setupChart() {
        int whiteColor = getResources().getColor(R.color.white);

        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(whiteColor);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextColor(whiteColor);

        lineChart.getAxisRight().setEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(whiteColor);
    }

    private void loadChartData() {
        String range = timeRangeSpinner.getSelectedItem().toString();
        String measure = bodyMeasureSpinner.getSelectedItem().toString();

        List<Entry> entries = new ArrayList<>();

        // Determine time range in days
        int daysBack = range.equals("1 Week") ? 7 : range.equals("1 Month") ? 30 : 365;

        // Calculate cutoff date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -daysBack);
        Date cutoffDate = calendar.getTime();

        // Sort records by date ascending
        List<BodyTrack> sortedList = new ArrayList<>(bodyTrackList);
        Collections.sort(sortedList, Comparator.comparing(track -> track.Date));

        int index = 0;
        for (BodyTrack track : sortedList) {
            if (track.Date.before(cutoffDate)) continue;

            float value;
            switch (measure) {
                case "Height":
                    value = track.Height;
                    break;
                case "Weight":
                    value = track.Weight;
                    break;
                case "BMI":
                    value = calculateBMI(track.Height, track.Weight);
                    break;
                default:
                    continue;
            }

            entries.add(new Entry(index++, value));
        }

        int colorRes = R.color.white;

        LineDataSet dataSet = new LineDataSet(entries, measure);
        dataSet.setColor(getResources().getColor(colorRes));
        dataSet.setCircleColor(getResources().getColor(colorRes));

        lineChart.setData(new LineData(dataSet));
        lineChart.invalidate(); // Refresh chart
    }


    private float calculateBMI(float heightCm, float weightKg) {
        float heightM = heightCm / 100f;
        return weightKg / (heightM * heightM);
    }


    private ArrayList<BodyTrack> generateDummyBodyTracks() {
        ArrayList<BodyTrack> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            BodyTrack track = new BodyTrack();
            track.Id = i;
            track.Weight = 60 + i;
            track.Height = 170 + (i % 3);
            track.Date = new Date(System.currentTimeMillis() - i * 86400000L); // subtract i days
            track.UserId = 1;
            list.add(track);
        }
        return list;
    }

    private void showAddRecordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_bodytrack, null);

        EditText editHeight = dialogView.findViewById(R.id.editHeight);
        EditText editWeight = dialogView.findViewById(R.id.editWeight);

        new AlertDialog.Builder(this)
                .setTitle("Add Body Record")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    try {
                        int height = Integer.parseInt(editHeight.getText().toString());
                        int weight = Integer.parseInt(editWeight.getText().toString());
                        Date currentDate = new Date();

                        BodyTrack newRecord = new BodyTrack();
                        newRecord.Height = height;
                        newRecord.Weight = weight;
                        newRecord.Date = currentDate;
                        newRecord.UserId = 1; // Replace with actual user ID if needed

                        bodyTrackList.add(0, newRecord);
                        bodyTrackAdapter.notifyItemInserted(0);
                        bodyTrackRecyclerView.scrollToPosition(0);

                        loadChartData();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}