package com.example.firstandroidapp;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

abstract class MenuBarActivity extends AppCompatActivity {
    private static String activeLanguage = "en";
    private MenuItem lightModeItem;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        lightModeItem = menu.findItem(R.id.light_mode_menu);
        LightModeHelper.setLightModeIcon(lightModeItem);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.light_mode_menu) {
            LightModeHelper.changeLightMode(lightModeItem);
            return true;
        } else if (id == R.id.language_menu) {
            activeLanguage = (activeLanguage.equals("en")) ? "pl" : "en";
            LocaleHelper.setLocale(this, activeLanguage);
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}