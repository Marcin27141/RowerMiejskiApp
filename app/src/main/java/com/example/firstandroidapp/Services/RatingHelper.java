package com.example.firstandroidapp.Services;

import android.content.Context;

import com.example.firstandroidapp.Activities.ChooseBike.IRatingGroupsHolder;
import com.example.firstandroidapp.Activities.ChooseBike.IRatingGroupsProvider;
import com.example.firstandroidapp.Activities.RatedStatus;
import com.example.firstandroidapp.WrmModel.BikeRating;
import com.example.firstandroidapp.DatabaseHelpers.DatabaseHelper;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RatingHelper implements IRatingGroupsProvider {
    private final Context context;
    public RatingHelper(Context context) {
        this.context = context;
    }

    public ArrayList<BikeRating> GetAllRatings() {
        try(DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            return dbHelper.getAllRatings();
        }
    }

    public Optional<BikeRating> GetBikeRating(String bikeId) {
        ArrayList<BikeRating> ratings = GetAllRatings();
        return ratings.stream().filter(r -> r.bikeId.equals(bikeId)).findFirst();
    }

    public void RemoveBikeRating(String bikeId) {
        try(DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            dbHelper.removeRatingForBike(bikeId);
        }
    }

    @Override
    public IRatingGroupsHolder getRatingGroups(WrmStation station) {
        ArrayList<BikeRating> ratings = GetAllRatings();

        List<String> positiveBikes = ratings.stream().filter(r -> r.wasPositive && station.bikes.contains(r.bikeId)).map(r -> r.bikeId).collect(Collectors.toList());
        List<String> negativeBikes = ratings.stream().filter(r -> !r.wasPositive && station.bikes.contains(r.bikeId)).map(r -> r.bikeId).collect(Collectors.toList());
        List<String> ungradedBikes = station.bikes.stream().filter(b -> ratings.stream().noneMatch(r -> r.bikeId.equals(b))).collect(Collectors.toList());

        Map<RatedStatus, List<String>> result = new HashMap<>();
        result.put(RatedStatus.Positive, positiveBikes);
        result.put(RatedStatus.Negative, negativeBikes);
        result.put(RatedStatus.Ungraded, ungradedBikes);

        return new RatingGroupsHolder(result);
    }

    static class RatingGroupsHolder implements IRatingGroupsHolder {
        private final Map<RatedStatus, List<String>> groups;
        public RatingGroupsHolder(Map<RatedStatus, List<String>> groups) {
            this.groups = groups;
        }

        @Override
        public List<String> getPositiveBikes() {
            return groups.get(RatedStatus.Positive);
        }

        @Override
        public List<String> getUngradedBikes() {
            return groups.get(RatedStatus.Ungraded);
        }

        @Override
        public List<String> getNegativeBikes() {
            return groups.get(RatedStatus.Negative);
        }
    }
}
