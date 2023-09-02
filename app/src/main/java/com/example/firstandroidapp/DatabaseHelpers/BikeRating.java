package com.example.firstandroidapp.DatabaseHelpers;

public class BikeRating {
    public int reviewId = -1;
    public final String bikeId;
    public final boolean wasPositive;
    public final String description;

    public BikeRating(int reviewId, String bikeId, boolean wasPositive, String description) {
        this(bikeId, wasPositive, description);
        this.reviewId = reviewId;
    }

    public BikeRating(String bikeId, boolean wasPositive, String description) {
        this.bikeId = bikeId;
        this.wasPositive = wasPositive;
        this.description = description;
    }
}
