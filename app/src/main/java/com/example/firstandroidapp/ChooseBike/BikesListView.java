package com.example.firstandroidapp.ChooseBike;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.firstandroidapp.R;

import java.util.ArrayList;
import java.util.List;

public class BikesListView {
    private Context context;
    private ListView bikesList;
    private ArrayAdapter<String> bikesAdapter;
    private ArrayList<String> displayedBikes = new ArrayList<>();


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

    public void DisplayBikes(List<String> bikesList) {
        bikesAdapter.clear();
        bikesAdapter.addAll(bikesList);
        bikesAdapter.notifyDataSetChanged();
    }

    public int GetPositionForView(View view) {
        return bikesList.getPositionForView(view);
    }
}
