package com.example.firstandroidapp;


import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.firstandroidapp.Services.WrmHelper;
import com.example.firstandroidapp.WrmModel.WrmStation;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


public class ActivityStationsList extends MenuBarActivity {

    private ArrayList<WrmStation> wrmStationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_list);

        if (savedInstanceState != null) {
            wrmStationsList = WrmHelper.getWrmStations();
            setUpViewPager();
            showView();
        }
        else {
            WrmHelper.loadWrmStationsList(generatedList -> {
                wrmStationsList = generatedList;
                setUpViewPager();
                showView();
            });
        }
    }

    private void setUpViewPager() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        PagerAdapter adapter = new PagerAdapter(this, wrmStationsList);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0)
                tab.setText(getString(R.string.all_stations_txt));
            else
                tab.setText(getString(R.string.liked_stations_txt));
        }).attach();
    }

    private void showView() {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_layout);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);
        coordinatorLayout.setVisibility(View.VISIBLE);
    }

    static class PagerAdapter extends FragmentStateAdapter {
        private final int PAGE_COUNT = 2;
        private final ArrayList<WrmStation> wrmStationsList;

        public PagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<WrmStation> wrmStationsList) {
            super(fragmentActivity);
            this.wrmStationsList = wrmStationsList;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0)
                return new AllStationsFragment(wrmStationsList);
            return new LikedStationsFragment(wrmStationsList);
        }

        @Override
        public int getItemCount() {
            return PAGE_COUNT;
        }
    }


}