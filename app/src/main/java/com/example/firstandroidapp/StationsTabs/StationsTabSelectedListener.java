package com.example.firstandroidapp.StationsTabs;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

public class StationsTabSelectedListener implements TabLayout.OnTabSelectedListener {
    private ViewPager2 viewPager2;

    public StationsTabSelectedListener(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager2.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
