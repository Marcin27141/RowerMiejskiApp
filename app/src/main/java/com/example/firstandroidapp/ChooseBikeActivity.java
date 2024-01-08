package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;

import com.example.firstandroidapp.DatabaseHelpers.BikeRating;
import com.example.firstandroidapp.DatabaseHelpers.DatabaseHelper;
import com.example.firstandroidapp.WrmModel.WrmStation;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ChooseBikeActivity extends MenuBarActivity {

    private ActivityResultLauncher<Intent> ratingActivityResultLauncher;
    private ArrayAdapter<String> bikesAdapter;
    private ListView bikesList;
    private MaterialButtonToggleGroup buttonToggleGroup;
    private SearchView searchView;
    private ArrayList<BikeRating> ratings;
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
        setUpBikesListAdapter();

        buttonToggleGroup = findViewById(R.id.buttonToggleGroup);
        setUpButtonToggleGroupCheckedListener();

        ratingActivityResultLauncher = getRatingActivityResultLauncher();

        searchView = findViewById(R.id.searchView);
        setUpSearchViewListener();

    }

    @NonNull
    private ActivityResultLauncher<Intent> getRatingActivityResultLauncher() {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        resetGradingGroups(getResources().getString(R.string.rating_added_info));
                    }
                });
    }

    private void setUpButtonToggleGroupCheckedListener() {
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
    }

    private void setUpBikesListAdapter() {
        bikesAdapter = new ArrayAdapter<>(
                this,
                R.layout.bikes_list_item,
                R.id.bikeNumber,
                displayedBikes
        );
        bikesList.setAdapter(bikesAdapter);
        bikesList.setOnItemClickListener((parent, view, position, id) -> {
            String clickedBike = displayedBikes.get(position);
            launchRatingActivity(clickedBike);
        });
    }

    private void resetGradingGroups(String toastMessage) {
        buttonToggleGroup.clearChecked();
        getBikeGroups();
        setBikesForAdapter(stationBikes);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void setUpSearchViewListener() {
        searchView.setOnQueryTextListener(new SearchViewHandler<>(
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
            configurePopupMenu(clickedBikeId, popupMenu);
            popupMenu.show();
        }
    }

    private void configurePopupMenu(String clickedBikeId, PopupMenu popupMenu) {
        if (ratings.stream().anyMatch(r -> r.bikeId.equals(clickedBikeId))) {
            popupMenu.inflate(R.menu.bike_list_graded_popup_menu);
            setUpRatedBikeMenuClickedListener(clickedBikeId, popupMenu);
        } else {
            popupMenu.inflate(R.menu.bike_list_popup_menu);
            setUpUnratedBikeMenuClickedListener(clickedBikeId, popupMenu);
        }
    }

    private void setUpUnratedBikeMenuClickedListener(String clickedBikeId, PopupMenu popupMenu) {
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.addRatingItem) {
                launchRatingActivity(clickedBikeId);
            }
            return true;
        });
    }

    private void setUpRatedBikeMenuClickedListener(String clickedBikeId, PopupMenu popupMenu) {
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.changeRatingItem)
                launchRatingActivity(clickedBikeId);
            else if (item.getItemId() == R.id.removeRatingItem)
                removeBikeRating(clickedBikeId);
            else if (item.getItemId() == R.id.descriptionItem)
                showRatingDescription(clickedBikeId);
            return true;
        });
    }

    private void showRatingDescription(String clickedBikeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.description_dialog, null);
        setDialogViewContent(clickedBikeId, dialogView);

        builder.setView(dialogView)
                .setTitle(R.string.description)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setDialogViewContent(String clickedBikeId, View dialogView) {
        TextView dialogTextView = dialogView.findViewById(R.id.descriptionDialogTxt);
        Optional<BikeRating> rating = ratings.stream().filter(r -> r.bikeId.equals(clickedBikeId)).findFirst();
        boolean descriptionProvided = rating.isPresent() && !rating.get().description.isEmpty();
        String descriptionTxt = descriptionProvided ? rating.get().description : getResources().getString(R.string.no_description);
        dialogTextView.setText(descriptionTxt);
    }

    private void removeBikeRating(String clickedBikeId) {
        Optional<BikeRating> rating = ratings.stream().filter(r -> r.bikeId.equals(clickedBikeId)).findFirst();
        rating.ifPresent(value -> new DatabaseHelper(this).removeRating(value));
        resetGradingGroups(getResources().getString(R.string.rating_deleted_info));
    }

    private void launchRatingActivity(String clickedBikeId) {
        Intent intent = new Intent(this, RateBikeActivity.class);
        intent.putExtra("BIKE_ID", clickedBikeId);
        ratingActivityResultLauncher.launch(intent);
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