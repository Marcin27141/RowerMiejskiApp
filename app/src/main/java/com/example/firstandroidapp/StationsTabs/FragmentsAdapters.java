package com.example.firstandroidapp.StationsTabs;

import android.content.Context;

import com.example.firstandroidapp.DatabaseHelper;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class FragmentsAdapters  implements OnStationLikedListener {
    private static FragmentsAdapters fragmentsAdapters;

    private ArrayList<WrmStation> stations = new ArrayList<>();
    private ArrayList<WrmStation> likedStations;
    private StationsRecViewAdapter allStationsAdapter, likedStationsAdapter;

    public static FragmentsAdapters getFragmentsAdapters(Context context, ArrayList<WrmStation> stations) {
        if (fragmentsAdapters == null)
            fragmentsAdapters = new FragmentsAdapters(context, stations);
        return fragmentsAdapters;
    }
    private FragmentsAdapters(Context context, ArrayList<WrmStation> stations) {
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

    @Override
    public void onStationLiked(WrmStation station, boolean isLiked) {
        if (isLiked)
            likedStations.add(station);
        else {
            likedStations.remove(station);
            allStationsAdapter.uncheckStarIconCheckBox(station.id);
        }

        likedStationsAdapter.setStations(likedStations);
        likedStationsAdapter.notifyDataSetChanged();
    }
}
