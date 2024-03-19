package com.example.firstandroidapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.firstandroidapp.DatabaseHelpers.DatabaseHelper;
import com.example.firstandroidapp.WrmModel.BikeRating;
import com.example.firstandroidapp.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RateBikeActivity extends MenuBarActivity {
    private String bikeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rating);

        bikeId = getIntent().getStringExtra("BIKE_ID");
        TextView bikeIdText = findViewById(R.id.bikeId);
        bikeIdText.setText(bikeId);
    }

    public void onSubmitRating(View view) {
        BikeRating rating = getRating();
        addRating(rating);
    }

    @NonNull
    private BikeRating getRating() {
        MaterialButtonToggleGroup buttonToggleGroup = findViewById(R.id.buttonToggleGroup);
        TextInputLayout description = findViewById(R.id.descriptionBox);

        boolean wasPositive = buttonToggleGroup.getCheckedButtonId() == R.id.goodButton;
        EditText descriptionEditText = description.getEditText();
        String descriptionValue = descriptionEditText == null || descriptionEditText.getText() == null ? "" : descriptionEditText.getText().toString();

        return new BikeRating(bikeId, wasPositive, descriptionValue);
    }

    private void addRating(BikeRating rating) {
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
                finishRatingActivity();
            });
        });
    }

    private void finishRatingActivity() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("RATING_ADDED", true);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}