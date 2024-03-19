package com.example.firstandroidapp.Activities.ChooseBike;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.firstandroidapp.R;

import java.util.ArrayList;
import java.util.List;

public class BikesListView {
    private final Context context;
    private final ListView bikesList;
    private ArrayAdapter<String> bikesAdapter;
    private List<String> displayedBikes = new ArrayList<>();


    public BikesListView(Context context, ListView bikes) {
        this.context = context;
        this.bikesList = bikes;
        setUpBikesListAdapter();
    }

    public void setUpBikesListAdapter() {
        bikesAdapter = new ArrayAdapter<>(
                context,
                R.layout.bikes_list_item,
                R.id.bikeNumber,
                displayedBikes
        );
        bikesList.setAdapter(bikesAdapter);

    }

    public void displayBikes(List<String> bikesList) {
        displayedBikes = bikesList;
        bikesAdapter.clear();
        bikesAdapter.addAll(displayedBikes);
        bikesAdapter.notifyDataSetChanged();
    }
    public List<String> getDisplayedBikes() {
        return displayedBikes;
    }
    public int getPositionForView(View view) {
        return bikesList.getPositionForView(view);
    }
}
