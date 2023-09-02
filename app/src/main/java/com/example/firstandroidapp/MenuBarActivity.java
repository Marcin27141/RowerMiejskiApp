package com.example.firstandroidapp;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

abstract class MenuBarActivity extends AppCompatActivity {
    private static String activeLanguage = "en";
    private static boolean disableIcons = false;
    private final static int RELOAD_TIME = 4000;
    private MenuItem lightModeItem, languageItem;
    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        this.lightModeItem = menu.findItem(R.id.light_mode_menu);
        this.languageItem = menu.findItem(R.id.language_menu);
        if (disableIcons) {
            setMenuIconsEnabled(false);
            enableIconsAfterReload();
            disableIcons = false;
        }
        return true;
    }

    private void setMenuIconsEnabled(boolean isEnabled) {
        lightModeItem.setEnabled(isEnabled);
        languageItem.setEnabled(isEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        this.lightModeItem = menu.findItem(R.id.light_mode_menu);
        this.languageItem = menu.findItem(R.id.language_menu);

        if (id == R.id.light_mode_menu) {
            disableIcons = true;
            LightModeHelper.changeLightMode();
            return true;
        } else if (id == R.id.language_menu) {
            activeLanguage = (activeLanguage.equals("en")) ? "pl" : "en";
            disableIcons = true;
            LocaleHelper.setLocale(this, activeLanguage);
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enableIconsAfterReload() {
        Handler handler = new Handler();
        handler.postDelayed(() -> setMenuIconsEnabled(true), RELOAD_TIME);
    }
}
