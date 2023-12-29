package com.example.firstandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayoutMediator;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navContainer);
//        navController = navHostFragment.getNavController();

        MyPagerAdapter adapter = new MyPagerAdapter(this);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(findViewById(R.id.tabLayout), viewPager, (tab, position) -> {
            tab.setText("Tab " + (position + 1));
            //tab.setIcon(viewPagerAdapter.TAB_ICONS[position]);
        }).attach();
    }

    class MyPagerAdapter extends FragmentStateAdapter {
        private final int PAGE_COUNT = 2;

        public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
//            if (position == 0)
//                return new AllStationsFragmentTest();
//            return new LikedStationsFragmentTest();
            return null;
        }

        @Override
        public int getItemCount() {
            return PAGE_COUNT;
        }
    }
}