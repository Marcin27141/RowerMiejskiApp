package com.example.firstandroidapp.Services;

import android.view.MenuItem;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.firstandroidapp.R;

public class LightModeHelper {
    public static void setLightModeIcon(MenuItem lightModeItem) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            lightModeItem.setIcon(R.drawable.ic_sun);
        } else {
            lightModeItem.setIcon(R.drawable.ic_moon);
        }
    }
    public static void changeLightMode(MenuItem lightModeItem) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}
