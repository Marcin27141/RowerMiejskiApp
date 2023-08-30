package com.example.firstandroidapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RateBikeActivity extends MenuBarActivity {
    private String bikeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_add_rating);

        bikeId = getIntent().getStringExtra("BIKE_ID");
        TextView bikeIdText = findViewById(R.id.bikeId);
        bikeIdText.setText(bikeId);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the up button
        }
    }

    public void onSubmitRating(View view) {
        MaterialButtonToggleGroup buttonToggleGroup = findViewById(R.id.buttonToggleGroup);
        TextInputLayout description = findViewById(R.id.descriptionBox);
        boolean wasPositive = buttonToggleGroup.getCheckedButtonId() == R.id.goodButton;
        EditText descriptionEditText = description.getEditText();
        String descriptionValue = descriptionEditText == null || descriptionEditText.getText() == null ? "" : descriptionEditText.getText().toString();

        Rating rating = new Rating(bikeId, wasPositive, descriptionValue);
        addRating(rating);
    }

    private void addRating(Rating rating) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
                dbHelper.addRating(rating);
            }
            handler.post(() -> {
                progressBar.setVisibility(View.INVISIBLE);
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}