package com.example.firstandroidapp.StationsTabs;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.firstandroidapp.R;
import com.example.firstandroidapp.SearchViewHandler;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;

public class LikedStationsFragment extends Fragment {

    private Context context;
    private ArrayList<WrmStation> likedStations;
    private StationsRecViewAdapter adapter;
    private SearchView searchView;


    public LikedStationsFragment(Context context, ArrayList<WrmStation> likedStations) {
        this.context = context;
        this.likedStations = likedStations;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_stations, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.stationsRecView);
        adapter = new StationsRecViewAdapter(context);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        adapter.setStations(likedStations);

        searchView = view.findViewById(R.id.searchView);
        setUpSearchViewListener();
        searchView.clearFocus();

        return view;
    }

    private void setUpSearchViewListener() {
        searchView.setOnQueryTextListener(new SearchViewHandler<WrmStation>(
                likedStations,
                (station, text) -> (station.id.contains(text) || station.location.name.toLowerCase().contains(text.toLowerCase())),
                filtered -> {
                    adapter.setStations(filtered);
                    if (filtered.isEmpty())
                        Toast.makeText(context, R.string.no_stations_found_msg, Toast.LENGTH_LONG).show();
                }
        ));
    }
}