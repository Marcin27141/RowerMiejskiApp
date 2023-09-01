package com.example.firstandroidapp.StationsTabs;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

public class StationsOnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
    private TabLayout tabLayout;

    public StationsOnPageChangeCallback(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        tabLayout.getTabAt(position).select();
    }
}
