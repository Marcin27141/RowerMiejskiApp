package com.example.firstandroidapp.DatabaseHelpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.firstandroidapp.WrmModel.BikeRating;

import java.util.ArrayList;

class RatingsDbHelper {
    private final SQLiteOpenHelper generalDbHelper;

    public RatingsDbHelper(SQLiteOpenHelper generalDbHelper) {
        this.generalDbHelper = generalDbHelper;
    }

    public ArrayList<BikeRating> getAllRatings() {
        Cursor cursor = generalDbHelper.getReadableDatabase().query(
                RatingsTable.tableName,
                RatingsTable.projection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<BikeRating> ratingsList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String bikeId = cursor.getString(cursor.getColumnIndexOrThrow("bikeId"));
                int wasPositiveInt = cursor.getInt(cursor.getColumnIndexOrThrow("wasPositive"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                boolean wasPositive = (wasPositiveInt == 1);

                BikeRating rating = new BikeRating(id, bikeId, wasPositive, description);
                ratingsList.add(rating);
            }
            cursor.close();
        }

        return ratingsList;
    }

    public boolean addRating(BikeRating rating) {
        SQLiteDatabase db = generalDbHelper.getWritableDatabase();
        try {
            db.beginTransaction();

            db.delete("ratings", "bikeId = ?", new String[]{rating.bikeId});

            ContentValues values = new ContentValues();
            values.put("bikeId", rating.bikeId);
            values.put("wasPositive", rating.wasPositive);
            values.put("description", rating.description);
            db.insert(RatingsTable.tableName, null, values);

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public void removeRatingForBike(String bikeId) {
        generalDbHelper.getWritableDatabase().delete(RatingsTable.tableName, "bikeId = ?", new String[]{bikeId});
    }
}
