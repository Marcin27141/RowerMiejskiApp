package com.example.firstandroidapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;


public class ChooseBikeActivity extends MenuBarActivity {

    private ListView bikesList;
    private ArrayList<Integer> stationBikes = new ArrayList<>();
    private ArrayList<Integer> displayedBikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_bikes);

        WrmStation station = (WrmStation) getIntent().getSerializableExtra("SERIALIZED_STATION");
        if (station != null) stationBikes = station.bikes;

        bikesList = findViewById(R.id.bikesList);
        displayedBikes = new ArrayList<>(stationBikes);
        ArrayAdapter<Integer> bikesAdapter = new ArrayAdapter<>(
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
                displayedBikes = new ArrayList<>(stationBikes.subList(0, 2)); //for testing
            } else if (isChecked && checkedId == R.id.mediumButton) {
                displayedBikes = new ArrayList<>(stationBikes.subList(2, 4)); //for testing
            } else if (isChecked && checkedId == R.id.sadButton) {
                displayedBikes = new ArrayList<>(stationBikes.subList(4, 6)); //for testing
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