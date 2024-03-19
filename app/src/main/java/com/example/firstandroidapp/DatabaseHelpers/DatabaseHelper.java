package com.example.firstandroidapp.DatabaseHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.firstandroidapp.WrmModel.BikeRating;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wrm_app_database.db";
    private static final int DATABASE_VERSION = 2;
    private final RatingsDbHelper ratingsDbHelper;
    private final LikedStationsDbHelper likedStationsDbHelper;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.ratingsDbHelper = new RatingsDbHelper(this);
        this.likedStationsDbHelper = new LikedStationsDbHelper(this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (id INTEGER PRIMARY KEY, bikeId TEXT, wasPositive BOOLEAN, description TEXT)",
                RatingsTable.tableName));
        db.execSQL(String.format("CREATE TABLE %s (id INTEGER PRIMARY KEY, stationId TEXT)",
                LikedStationsTable.tableName));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", RatingsTable.tableName));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", LikedStationsTable.tableName));
        onCreate(db);
    }

    public ArrayList<BikeRating> getAllRatings() {
        return ratingsDbHelper.getAllRatings();
    }

    public boolean addRating(BikeRating rating) {
        return ratingsDbHelper.addRating(rating);
    }

    public void removeRatingForBike(String bike) {
        ratingsDbHelper.removeRatingForBike(bike);
    }

    public ArrayList<String> getLikedStationsIds() {
        return likedStationsDbHelper.getLikedStationsIds();
    }

    public boolean addLikedStation(String stationId) {
        return likedStationsDbHelper.addLikedStation(stationId);
    }

    public void removeLikedStation(String stationId) {
        likedStationsDbHelper.removeLikedStation(stationId);
    }
}

