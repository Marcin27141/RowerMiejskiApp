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

    public StationsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Context context, ArrayList<WrmStation> stations) {
        super(fragmentActivity);
        this.context = context;
        FragmentsHelper.createFragmentsHelper(context, stations);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new LikedStationsFragment();
        }
        return new AllStationsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
