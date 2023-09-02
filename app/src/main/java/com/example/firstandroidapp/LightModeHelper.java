package com.example.firstandroidapp;

import android.view.MenuItem;

import androidx.appcompat.app.AppCompatDelegate;

public class LightModeHelper {
    public static void changeLightMode() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}
