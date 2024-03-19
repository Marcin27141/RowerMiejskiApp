package com.example.firstandroidapp.Activities.ChooseBike;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.firstandroidapp.Activities.MenuBarActivity;
import com.example.firstandroidapp.R;
import com.example.firstandroidapp.Activities.RateBikeActivity;
import com.example.firstandroidapp.Activities.SearchViewHandler;
import com.example.firstandroidapp.Services.RatingHelper;
import com.example.firstandroidapp.Services.WrmHelper;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;
import java.util.List;


public class ChooseBikeActivity extends MenuBarActivity {
    private WrmStation station;
    private RatingHelper ratingHelper;
    private IRatingGroupsHolder ratingGroups;
    private BikesListView bikesListView;
    private ButtonToggleGroup buttonToggleGroup;
    private ProgressBar progressBar;
    private ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_bikes);

        ratingHelper = new RatingHelper(this);
        station = (WrmStation) getIntent().getSerializableExtra("SERIALIZED_STATION");
        if (station != null) {
            activityResultLauncher = getRatingActivityResultLauncher();

            getBikeRatingGroups();
            setUpBikesList();

            buttonToggleGroup = new ButtonToggleGroup(findViewById(R.id.buttonToggleGroup));
            setUpButtonToggleGroupCheckedListener();

            SearchView searchView = findViewById(R.id.searchView);
            setUpSearchViewListener(searchView);

            progressBar = findViewById(R.id.progressBar);
            setUpRefreshLayout();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh_menu) {
            refreshBikesList();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    private void refreshBikesList() {
        progressBar.setVisibility(View.VISIBLE);
        WrmHelper.loadWrmStationsList(result -> {
            station.bikes = result.stream().filter(st -> st.id.equals(station.id)).findFirst().get().bikes;
            Toast.makeText(this, "Successfully refreshed", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        });
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

    private void setUpRefreshLayout() {
        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipeLayout);
        refreshLayout.setOnRefreshListener(() -> WrmHelper.loadWrmStationsList(result -> {
            station.bikes = result.stream().filter(st -> st.id.equals(station.id)).findFirst().get().bikes;
            Toast.makeText(this, "Successfully refreshed", Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(false);
        }));
    }

    private void setUpBikesList() {
        ListView bikesList = findViewById(R.id.bikesList);
        bikesListView = new BikesListView(this, bikesList);
        bikesList.setOnItemClickListener((parent, view, position, id) -> {
            String clickedBike = bikesListView.getDisplayedBikes().get(position);
            launchRatingActivity(clickedBike);
        });
        bikesListView.displayBikes(new ArrayList<>(station.bikes));
    }

    private void setUpButtonToggleGroupCheckedListener() {
        buttonToggleGroup.setUpListeners(
                () -> setDisplayedBikes(ratingGroups.getPositiveBikes()),
                () -> setDisplayedBikes(ratingGroups.getUngradedBikes()),
                () -> setDisplayedBikes(ratingGroups.getNegativeBikes())
        );
    }

    private void setUpSearchViewListener(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchViewHandler<>(
            station.bikes,
            String::contains,
            filtered -> {
                bikesListView.displayBikes(filtered);
                if (filtered.isEmpty())
                    Toast.makeText(this, R.string.no_bikes_found_msg, Toast.LENGTH_LONG).show();
            }
        ));
    }

    private void setDisplayedBikes(List<String> bikes) {
        bikesListView.displayBikes(bikes);
    }

    private void resetGradingGroups(String toastMessage) {
        buttonToggleGroup.clearChecked();
        getBikeRatingGroups();
        bikesListView.displayBikes(station.bikes);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void getBikeRatingGroups() {
        ratingGroups = ratingHelper.getRatingGroups(station);
    }

    public void onBikeListMoreIconClicked(View view) {
        int position = bikesListView.getPositionForView(view);

        if (position != ListView.INVALID_POSITION) {
            String clickedBikeId = bikesListView.getDisplayedBikes().get(position);
            PopupMenu popupMenu = new PopupMenu(this, view);
            configurePopupMenu(clickedBikeId, popupMenu);
            popupMenu.show();
        }
    }

    private void configurePopupMenu(String clickedBikeId, PopupMenu popupMenu) {
        if (!ratingGroups.getUngradedBikes().contains(clickedBikeId)) {
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
                new RatingDescriptionDialog(this).showDescription(clickedBikeId);
            return true;
        });
    }

    private void removeBikeRating(String clickedBikeId) {
        ratingHelper.RemoveBikeRating(clickedBikeId);
        resetGradingGroups(getResources().getString(R.string.rating_deleted_info));
    }

    private void launchRatingActivity(String clickedBikeId) {
        Intent intent = new Intent(this, RateBikeActivity.class);
        intent.putExtra("BIKE_ID", clickedBikeId);
        activityResultLauncher.launch(intent);
    }
}