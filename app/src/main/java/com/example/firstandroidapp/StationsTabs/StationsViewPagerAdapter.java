package com.example.firstandroidapp.StationsTabs;

import android.content.Context;

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

public class StationsViewPagerAdapter extends FragmentStateAdapter {

    private Context context;
    private ArrayList<WrmStation> stations;

    public StationsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Context context, ArrayList<WrmStation> stations) {
        super(fragmentActivity);
        this.context = context;
        this.stations = stations;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
                ArrayList<String> likedStationsIds = dbHelper.getLikedStationsIds();
                List<WrmStation> likedStations = stations.stream().filter(s -> likedStationsIds.contains(s.id)).collect(Collectors.toList());
                return new LikedStationsFragment(context, new ArrayList<>(likedStations));
            }
        }
        return new AllStationsFragment(context, stations);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
