package com.example.firstandroidapp.Services;

import android.content.Context;

import com.example.firstandroidapp.DatabaseHelpers.BikeRating;
import com.example.firstandroidapp.DatabaseHelpers.DatabaseHelper;
import com.example.firstandroidapp.R;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RatingHelper {
    private DatabaseHelper dbHelper;
    private ArrayList<BikeRating> ratings;
    public RatingHelper(Context context) {
        try(DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            this.dbHelper = dbHelper;
            ratings = dbHelper.getAllRatings();
        }
    }

    public ArrayList<BikeRating> GetAllRatings() {
        return ratings;
    }

    public enum RatedStatus {
        Positive,
        Negative,
        Ungraded
    }

    public Map<RatedStatus, List<String>> GetStationGradedBikes(WrmStation station) {
        ArrayList<BikeRating> ratings = GetAllRatings();

        List<String> positiveBikes = ratings.stream().filter(r -> r.wasPositive && station.bikes.contains(r.bikeId)).map(r -> r.bikeId).collect(Collectors.toList());
        List<String> negativeBikes = ratings.stream().filter(r -> !r.wasPositive && station.bikes.contains(r.bikeId)).map(r -> r.bikeId).collect(Collectors.toList());
        List<String> ungradedBikes = station.bikes.stream().filter(b -> ratings.stream().noneMatch(r -> r.bikeId.equals(b))).collect(Collectors.toList());

        Map<RatedStatus, List<String>> result = new HashMap<>();
        result.put(RatedStatus.Positive, positiveBikes);
        result.put(RatedStatus.Negative, negativeBikes);
        result.put(RatedStatus.Ungraded, ungradedBikes);
        return result;
    }

    public Optional<BikeRating> GetBikeRating(String bikeId) {
        return ratings.stream().filter(r -> r.bikeId.equals(bikeId)).findFirst();
    }

    public void RemoveBikeRating(String bikeId) {
        dbHelper.removeRatingForBike(bikeId);
    }
}
