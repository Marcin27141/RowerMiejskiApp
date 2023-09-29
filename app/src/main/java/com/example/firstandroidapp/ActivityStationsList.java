package com.example.firstandroidapp;


import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.example.firstandroidapp.StationsTabs.StationsOnPageChangeCallback;
import com.example.firstandroidapp.StationsTabs.StationsTabSelectedListener;
import com.example.firstandroidapp.StationsTabs.StationsViewPagerAdapter;
import com.example.firstandroidapp.WrmModel.WrmStation;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;


public class ActivityStationsList extends MenuBarActivity {

    private ArrayList<WrmStation> wrmStationsList = new ArrayList<>();
    private ArrayList<WrmStation> displayedStations = new ArrayList<>();

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private StationsViewPagerAdapter stationsViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_list);

        WrmHelper.getWrmStationsList(generatedList -> {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);

            tabLayout = findViewById(R.id.tab_layout);
            tabLayout.setVisibility(View.VISIBLE);

            wrmStationsList = generatedList;
            displayedStations = new ArrayList<>(wrmStationsList);

            viewPager2 = findViewById(R.id.view_pager);
            stationsViewPagerAdapter = new StationsViewPagerAdapter(this, this, displayedStations);
            viewPager2.setAdapter(stationsViewPagerAdapter);

            tabLayout.addOnTabSelectedListener(new StationsTabSelectedListener(viewPager2));
            viewPager2.registerOnPageChangeCallback(new StationsOnPageChangeCallback(tabLayout));
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("STATIONS_LIST", wrmStationsList);
    }


}