package com.example.firstandroidapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ChooseBikeActivity extends MenuBarActivity {

    private ListView bikesList;
    private List<String> stationBikes = new ArrayList<>();
    private List<String> positiveBikes;
    private List<String> negativeBikes;
    private List<String> ungradedBikes;
    private List<String> displayedBikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_bikes);

        WrmStation station = (WrmStation) getIntent().getSerializableExtra("SERIALIZED_STATION");
        if (station != null) stationBikes = station.bikes;

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ArrayList<Rating> ratings = dbHelper.getAllRatings();
        positiveBikes = ratings.stream().filter(r -> r.wasPositive).map(r -> r.bikeId).collect(Collectors.toList());
        negativeBikes = ratings.stream().filter(r -> !r.wasPositive).map(r -> r.bikeId).collect(Collectors.toList());
        ungradedBikes = stationBikes.stream().filter(b -> ratings.stream().noneMatch(r -> r.bikeId.equals(b))).collect(Collectors.toList());
        displayedBikes = new ArrayList<>(stationBikes);

        bikesList = findViewById(R.id.bikesList);
        ArrayAdapter<String> bikesAdapter = new ArrayAdapter<>(
                this,
                R.layout.bikes_list_item,
                R.id.bikeNumber,
                displayedBikes
        );
        bikesList.setAdapter(bikesAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the up button
        }

        MaterialButtonToggleGroup toggleButton = findViewById(R.id.toggleButton);
        toggleButton.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked && checkedId == R.id.goodButton) {
                displayedBikes = positiveBikes;
            } else if (isChecked && checkedId == R.id.mediumButton) {
                displayedBikes = ungradedBikes;
            } else if (isChecked && checkedId == R.id.sadButton) {
                displayedBikes = negativeBikes;
            }
            bikesAdapter.clear();
            bikesAdapter.addAll(displayedBikes);
            bikesAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}