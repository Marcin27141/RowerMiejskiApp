package com.example.firstandroidapp.StationsTabs;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.firstandroidapp.DatabaseHelper;
import com.example.firstandroidapp.StationsTabs.AllStationsFragment;
import com.example.firstandroidapp.StationsTabs.LikedStationsFragment;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StationsViewPagerAdapter extends FragmentStateAdapter implements OnStationLikedListener {

    private Context context;
    private ArrayList<WrmStation> stations;
    private ArrayList<WrmStation> likedStations = new ArrayList<>();
    private StationsRecViewAdapter allStationsAdapter, likedStationsAdapter;

    public StationsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Context context, ArrayList<WrmStation> stations) {
        super(fragmentActivity);
        this.context = context;
        this.stations = stations;

        allStationsAdapter = new StationsRecViewAdapter(context);
        allStationsAdapter.addOnStationLikedListener(this);
        likedStationsAdapter = new StationsRecViewAdapter(context);
        likedStationsAdapter.addOnStationLikedListener(this);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
                ArrayList<String> likedStationsIds = dbHelper.getLikedStationsIds();
                likedStations = new ArrayList<>(stations.stream().filter(s -> likedStationsIds.contains(s.id)).collect(Collectors.toList()));
                return new LikedStationsFragment(context, likedStationsAdapter, new ArrayList<>(likedStations));
            }
        }
        return new AllStationsFragment(context, allStationsAdapter, stations);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public void onStationLiked(WrmStation station, boolean isLiked) {
        if (isLiked)
            likedStations.add(station);
        else {
            likedStations.remove(station);
            stations.stream().filter(s ->)
        }

        likedStationsAdapter.setStations(likedStations);
        likedStationsAdapter.notifyDataSetChanged();
    }
}
