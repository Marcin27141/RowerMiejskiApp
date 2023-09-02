package com.example.firstandroidapp.StationsTabs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;

public class StationsViewPagerAdapter extends FragmentStateAdapter {

    private Context context;
    private ArrayList<WrmStation> stations;
    private ArrayList<WrmStation> likedStations = new ArrayList<>();

    public StationsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Context context, ArrayList<WrmStation> stations) {
        super(fragmentActivity);
        this.context = context;
        this.stations = stations;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new LikedStationsFragment(context, stations);
        }
        return new AllStationsFragment(context, stations);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
