package com.example.firstandroidapp.ChooseBike;

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

import com.example.firstandroidapp.MenuBarActivity;
import com.example.firstandroidapp.R;
import com.example.firstandroidapp.RateBikeActivity;
import com.example.firstandroidapp.SearchViewHandler;
import com.example.firstandroidapp.Services.RatingHelper;
import com.example.firstandroidapp.Services.WrmHelper;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChooseBikeActivity extends MenuBarActivity {

    private ActivityResultLauncher<Intent> ratingActivityResultLauncher;
    private WrmStation station;
    private List<String> positiveBikes, negativeBikes, ungradedBikes, displayedBikes;
    private RatingHelper ratingHelper;
    private BikesListView bikesListView;
    private ButtonToggleGroup buttonToggleGroup;
    private SearchView searchView;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_bikes);

        ratingHelper = new RatingHelper(this);
        WrmStation station = (WrmStation) getIntent().getSerializableExtra("SERIALIZED_STATION");
        if (station != null) {
            this.station = station;

            getBikeRatingGroups();
            displayedBikes = new ArrayList<>(station.bikes);

            setUpBikesList();

            buttonToggleGroup = new ButtonToggleGroup(findViewById(R.id.buttonToggleGroup));
            setUpButtonToggleGroupCheckedListener();

            ratingActivityResultLauncher = getRatingActivityResultLauncher();

            searchView = findViewById(R.id.searchView);
            setUpSearchViewListener();

            progressBar = findViewById(R.id.progressBar);
            refreshLayout = findViewById(R.id.swipeLayout);
            refreshLayout.setOnRefreshListener(() -> WrmHelper.loadWrmStationsList(result -> {
                station.bikes = result.stream().filter(st -> st.id.equals(station.id)).findFirst().get().bikes;
                Toast.makeText(this, "Successfully refreshed", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }));
        }
    }

    private void setUpBikesList() {
        ListView bikesList = findViewById(R.id.bikesList);
        bikesList.setOnItemClickListener((parent, view, position, id) -> {
            String clickedBike = displayedBikes.get(position);
            launchRatingActivity(clickedBike);
        });
        bikesListView = new BikesListView(this, bikesList);
        bikesListView.DisplayBikes(displayedBikes);
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
            progressBar.setVisibility(View.VISIBLE);
            WrmHelper.loadWrmStationsList(result -> {
                station.bikes = result.stream().filter(st -> st.id.equals(station.id)).findFirst().get().bikes;
                Toast.makeText(this, "Successfully refreshed", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            });
            return true;
        } else return super.onOptionsItemSelected(item);
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
        buttonToggleGroup.setUpListeners(
                () -> setDisplayedBikes(positiveBikes),
                () -> setDisplayedBikes(ungradedBikes),
                () -> setDisplayedBikes(negativeBikes)
        );
    }

    private void setDisplayedBikes(List<String> bikes) {
        displayedBikes = bikes;
        bikesListView.DisplayBikes(displayedBikes);
    }

    private void resetGradingGroups(String toastMessage) {
        buttonToggleGroup.clearChecked();
        getBikeRatingGroups();
        bikesListView.DisplayBikes(station.bikes);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void setUpSearchViewListener() {
        searchView.setOnQueryTextListener(new SearchViewHandler<>(
                station.bikes,
                String::contains,
                filtered -> {
                    bikesListView.DisplayBikes(filtered);
                    if (filtered.isEmpty())
                        Toast.makeText(this, R.string.no_bikes_found_msg, Toast.LENGTH_LONG).show();
                }
        ));
    }

    private void getBikeRatingGroups() {
        Map<RatingHelper.RatedStatus, List<String>> bikes = ratingHelper.GetStationGradedBikes(station);
        positiveBikes = bikes.get(RatingHelper.RatedStatus.Positive);
        negativeBikes = bikes.get(RatingHelper.RatedStatus.Negative);
        ungradedBikes = bikes.get(RatingHelper.RatedStatus.Ungraded);
    }

    public void onBikeListMoreIconClicked(View view) {
        int position = bikesListView.GetPositionForView(view);

        if (position != ListView.INVALID_POSITION) {
            String clickedBikeId = displayedBikes.get(position);
            PopupMenu popupMenu = new PopupMenu(this, view);
            configurePopupMenu(clickedBikeId, popupMenu);
            popupMenu.show();
        }
    }

    private void configurePopupMenu(String clickedBikeId, PopupMenu popupMenu) {
        if (positiveBikes.contains(clickedBikeId)) {
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
        ratingActivityResultLauncher.launch(intent);
    }
}