package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

abstract class MenuBarActivity extends AppCompatActivity {
    private static String activeLanguage = "en";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_menu) {
            Toast.makeText(this, R.string.settings_menu_title, Toast.LENGTH_SHORT).show();
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