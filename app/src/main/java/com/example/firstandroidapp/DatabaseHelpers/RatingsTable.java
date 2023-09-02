package com.example.firstandroidapp.DatabaseHelpers;

class RatingsTable {
    public final static String tableName = "ratings";
    public final static String[] projection = {
            "id",
            "bikeId",
            "wasPositive",
            "description"
    };
}
