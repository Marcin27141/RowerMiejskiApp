package com.example.firstandroidapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ChooseBikeActivity extends MenuBarActivity {

    private ActivityResultLauncher<Intent> ratingActivityResultLauncher;
    private ArrayAdapter<String> bikesAdapter;
    private ListView bikesList;
    private MaterialButtonToggleGroup buttonToggleGroup;
    private SearchView searchView;
    private ArrayList<Rating> ratings;
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
        displayedBikes = new ArrayList<>(stationBikes);

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

        buttonToggleGroup = findViewById(R.id.buttonToggleGroup);
        buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
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
                        resetGradingGroups("Rating successfully added");
                    }
                });

        buttonToggleGroup = findViewById(R.id.buttonToggleGroup);
        searchView = findViewById(R.id.searchView);
        setUpSearchViewListener();
    }

    private void resetGradingGroups(String toastMessage) {
        buttonToggleGroup.clearChecked();
        getBikeGroups();
        setBikesForAdapter(stationBikes);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void setUpSearchViewListener() {
        searchView.setOnQueryTextListener(new SearchViewHandler<String>(
                stationBikes,
                String::contains,
                filtered -> {
                    setBikesForAdapter(filtered);
                    if (filtered.isEmpty())
                        Toast.makeText(this, R.string.no_bikes_found_msg, Toast.LENGTH_LONG).show();
                }
        ));
    }

    private void setBikesForAdapter(List<String> bikesList) {
        bikesAdapter.clear();
        bikesAdapter.addAll(bikesList);
        bikesAdapter.notifyDataSetChanged();
    }

    private void getBikeGroups() {
        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            ratings = dbHelper.getAllRatings();
            positiveBikes = ratings.stream().filter(r -> r.wasPositive && stationBikes.contains(r.bikeId)).map(r -> r.bikeId).collect(Collectors.toList());
            negativeBikes = ratings.stream().filter(r -> !r.wasPositive && stationBikes.contains(r.bikeId)).map(r -> r.bikeId).collect(Collectors.toList());
            ungradedBikes = stationBikes.stream().filter(b -> ratings.stream().noneMatch(r -> r.bikeId.equals(b))).collect(Collectors.toList());
        }
    }

    public void onBikeListMoreIconClicked(View view) {
        int position = bikesList.getPositionForView(view);

        if (position != ListView.INVALID_POSITION) {
            String clickedBikeId = displayedBikes.get(position);
            PopupMenu popupMenu = new PopupMenu(this, view);
            if (ratings.stream().anyMatch(r -> r.bikeId.equals(clickedBikeId))) {
                popupMenu.inflate(R.menu.bike_list_graded_popup_menu);

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.changeRatingItem) {
                        Intent intent = new Intent(this, RateBikeActivity.class);
                        intent.putExtra("BIKE_ID", clickedBikeId);
                        ratingActivityResultLauncher.launch(intent);
                    } else if (item.getItemId() == R.id.removeRatingItem) {
                        Optional<Rating> rating = ratings.stream().filter(r -> r.bikeId.equals(clickedBikeId)).findFirst();
                        rating.ifPresent(value -> new DatabaseHelper(this).removeRating(value));
                        resetGradingGroups("Rating successfully deleted");
                    } else if (item.getItemId() == R.id.descriptionItem) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.description_dialog, null);
                        TextView dialogTextView = dialogView.findViewById(R.id.descriptionDialogTxt);
                        Optional<Rating> rating = ratings.stream().filter(r -> r.bikeId.equals(clickedBikeId)).findFirst();
                        rating.ifPresent(value -> dialogTextView.setText(value.description));

                        builder.setView(dialogView)
                                .setTitle("Description")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    return true;
                });
                popupMenu.show();
            } else {
                popupMenu.inflate(R.menu.bike_list_popup_menu);

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.addRatingItem) {
                        Intent intent = new Intent(this, RateBikeActivity.class);
                        intent.putExtra("BIKE_ID", clickedBikeId);
                        ratingActivityResultLauncher.launch(intent);
                    }
                    return true;
                });
                popupMenu.show();
            }

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