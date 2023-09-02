package com.example.firstandroidapp.StationsTabs;

import android.content.Context;

import com.example.firstandroidapp.DatabaseHelpers.DatabaseHelper;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class FragmentsHelper implements OnStationLikedListener {
    private static FragmentsHelper fragmentsHelper;

    private ArrayList<WrmStation> stations = new ArrayList<>();
    private ArrayList<WrmStation> likedStations;
    private StationsRecViewAdapter allStationsAdapter, likedStationsAdapter;

    public static FragmentsHelper createFragmentsHelper(Context context, ArrayList<WrmStation> stations) {
        if (fragmentsHelper == null)
            fragmentsHelper = new FragmentsHelper(context, stations);
        return fragmentsHelper;
    }

    public static FragmentsHelper getFragmentsHelper() {
        return fragmentsHelper;
    }
    private FragmentsHelper(Context context, ArrayList<WrmStation> stations) {
        this.stations = stations;

        ArrayList<String> likedStationsIds = new DatabaseHelper(context).getLikedStationsIds();
        likedStations = stations.stream().filter(s -> likedStationsIds.contains(s.id)).collect(Collectors.toCollection(ArrayList::new));

        allStationsAdapter = new StationsRecViewAdapter(context);
        allStationsAdapter.addOnStationLikedListener(this);

        likedStationsAdapter = new StationsRecViewAdapter(context);
        likedStationsAdapter.addOnStationLikedListener(this);
    }

    public StationsRecViewAdapter getAllStationsAdapter() {
        return allStationsAdapter;
    }

    public StationsRecViewAdapter getLikedStationsAdapter() {
        return likedStationsAdapter;
    }

    public ArrayList<WrmStation> getStations() {
        return stations;
    }

    public ArrayList<WrmStation> getLikedStations() {
        return likedStations;
    }

    @Override
    public void onStationLiked(StationsRecViewAdapter sender, WrmStation station, boolean isLiked) {
        if (isLiked)
            likedStations.add(station);
        else if (sender == allStationsAdapter) {
            likedStations.remove(station);
        } else if (sender == likedStationsAdapter) {
            likedStations.remove(station);
            allStationsAdapter.setStations(stations);
        }
        likedStationsAdapter.setStations(likedStations);
        likedStationsAdapter.notifyDataSetChanged();
    }
}
