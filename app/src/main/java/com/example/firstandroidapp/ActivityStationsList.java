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

import com.example.firstandroidapp.StationsTabs.StationsOnPageChangeCallback;
import com.example.firstandroidapp.StationsTabs.StationsTabSelectedListener;
import com.example.firstandroidapp.StationsTabs.StationsViewPagerAdapter;
import com.example.firstandroidapp.WrmModel.WrmStation;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


public class ActivityStationsList extends MenuBarActivity {

    private ArrayList<WrmStation> wrmStationsList;
    private TabLayout tabLayout;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_list);

        tabLayout = findViewById(R.id.tab_layout);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        progressBar = findViewById(R.id.progressBar);

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
        PagerAdapter adapter = new PagerAdapter(this);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0)
                tab.setText("Wszystkie");
            else
                tab.setText("Polubione");
        }).attach();
    }

    private void showView() {
        progressBar.setVisibility(View.INVISIBLE);
        coordinatorLayout.setVisibility(View.VISIBLE);
    }

    static class PagerAdapter extends FragmentStateAdapter {
        private final int PAGE_COUNT = 2;

        public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0)
                return new AllStationsFragmentTest();
            return new LikedStationsFragmentTest();
        }

        @Override
        public int getItemCount() {
            return PAGE_COUNT;
        }
    }


}