package com.example.firstandroidapp.StationsTabs;

import com.example.firstandroidapp.WrmModel.WrmStation;

public interface OnStationLikedListener {
    void onStationLiked(StationsRecViewAdapter sender, WrmStation station, boolean isLiked);
}
