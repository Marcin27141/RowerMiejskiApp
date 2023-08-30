package com.example.firstandroidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wrm_app_database.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (id INTEGER PRIMARY KEY, bikeId TEXT, wasPositive BOOLEAN, description TEXT)",
                RatingsTable.tableName));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ratings");
        onCreate(db);
    }

    public ArrayList<Rating> getAllRatings() {
        Cursor cursor = getReadableDatabase().query(
                "ratings",
                RatingsTable.projection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<Rating> ratingsList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String bikeId = cursor.getString(cursor.getColumnIndexOrThrow("bikeId"));
                int wasPositiveInt = cursor.getInt(cursor.getColumnIndexOrThrow("wasPositive"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                boolean wasPositive = (wasPositiveInt == 1);

                Rating rating = new Rating(id, bikeId, wasPositive, description);
                ratingsList.add(rating);
            }
            cursor.close();
        }

        return ratingsList;
    }

    public boolean addRating(Rating rating) {
        ContentValues values = new ContentValues();
        values.put("bikeId", rating.bikeId);
        values.put("wasPositive", rating.wasPositive);
        values.put("description", rating.description);
        long newRowId = getWritableDatabase().insert("ratings", null, values);
        return true;
    }
}

class RatingsTable {
    public final static String tableName = "ratings";
    public final static String[] projection = {
            "id",
            "bikeId",
            "wasPositive",
            "description"
    };
}

class Rating {
    public int reviewId = -1;
    public final String bikeId;
    public final boolean wasPositive;
    public final String description;

    public Rating(int reviewId, String bikeId, boolean wasPositive, String description) {
        this(bikeId, wasPositive, description);
        this.reviewId = reviewId;
    }

    public Rating(String bikeId, boolean wasPositive, String description) {
        this.bikeId = bikeId;
        this.wasPositive = wasPositive;
        this.description = description;
    }
}