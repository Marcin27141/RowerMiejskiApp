package com.example.firstandroidapp;

import android.os.Bundle;
import android.widget.TextView;


public class ChooseBikeActivity extends MenuBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_bikes);

        WrmStation station = (WrmStation) getIntent().getSerializableExtra("SERIALIZED_STATION");
        TextView totalBikesTxt = findViewById(R.id.bikesNumberInfoText);
        totalBikesTxt.setText(String.format("Total bikes: %d", station == null ? -1 : station.bikes.size()));
    }
}