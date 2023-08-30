package com.example.firstandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ChooseBikeActivity extends MenuBarActivity {

    private ActivityResultLauncher<Intent> ratingActivityResultLauncher;
    private ArrayAdapter<String> bikesAdapter;
    private ListView bikesList;
    private SearchView searchView;
    private List<String> stationBikes = new ArrayList<>();
    private List<String> displayedBikes = new ArrayList<>();
    private List<String> positiveBikes, negativeBikes, ungradedBikes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_bikes);

        WrmStation station = (WrmStation) getIntent().getSerializableExtra("SERIALIZED_STATION");
        if (station != null) stationBikes = station.bikes;

        getBikeGroups();

        bikesList = findViewById(R.id.bikesList);
        bikesAdapter = new ArrayAdapter<>(
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

        MaterialButtonToggleGroup toggleButton = findViewById(R.id.buttonToggleGroup);
        toggleButton.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked && checkedId == R.id.goodButton) {
                displayedBikes = positiveBikes;
            } else if (isChecked && checkedId == R.id.mediumButton) {
                displayedBikes = ungradedBikes;
            } else if (isChecked && checkedId == R.id.sadButton) {
                displayedBikes = negativeBikes;
            }
            setBikesForAdapter(displayedBikes);
        });

        ratingActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        toggleButton.clearChecked();
                        getBikeGroups();
                        bikesAdapter.clear();
                        bikesAdapter.addAll(displayedBikes);
                        bikesAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Rating successfully added", Toast.LENGTH_SHORT).show();
                    }
                });

        searchView = findViewById(R.id.searchView);
        setUpSearchViewListener();
    }

    private void setUpSearchViewListener() {
        searchView.setOnQueryTextListener(new SearchViewHandler<String>(
                stationBikes,
                String::contains,
                filtered -> {
                    if (filtered.isEmpty())
                        Toast.makeText(this, "No bikes found", Toast.LENGTH_LONG).show();
                    else
                        setBikesForAdapter(filtered);
                }
        ));
    }

    private void setBikesForAdapter(List<String> bikesList) {
        bikesAdapter.clear();
        bikesAdapter.addAll(bikesList);
        bikesAdapter.notifyDataSetChanged();
    }

    private void getBikeGroups() {
        ArrayList<Rating> ratings;
        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            ratings = dbHelper.getAllRatings();
            positiveBikes = ratings.stream().filter(r -> r.wasPositive).map(r -> r.bikeId).collect(Collectors.toList());
            negativeBikes = ratings.stream().filter(r -> !r.wasPositive).map(r -> r.bikeId).collect(Collectors.toList());
            ungradedBikes = stationBikes.stream().filter(b -> ratings.stream().noneMatch(r -> r.bikeId.equals(b))).collect(Collectors.toList());
        }
        displayedBikes.clear();
        displayedBikes.addAll(stationBikes);
    }

    public void onBikeListMoreIconClicked(View view) {
        int position = bikesList.getPositionForView(view);

        if (position != ListView.INVALID_POSITION) {
            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.inflate(R.menu.bike_list_popup_menu);

            // Set a click listener for the menu items
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.addRatingItem) {
                    Intent intent = new Intent(this, RateBikeActivity.class);
                    intent.putExtra("BIKE_ID", displayedBikes.get(position));
                    ratingActivityResultLauncher.launch(intent);
                }
                return true;
            });
            popupMenu.show();
        }
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