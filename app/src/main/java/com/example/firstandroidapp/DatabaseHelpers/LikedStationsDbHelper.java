package com.example.firstandroidapp.DatabaseHelpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class LikedStationsDbHelper {
    private final SQLiteOpenHelper generalDbHelper;

    public LikedStationsDbHelper(SQLiteOpenHelper generalDbHelper) {
        this.generalDbHelper = generalDbHelper;
    }

    public ArrayList<String> getLikedStationsIds() {
        Cursor cursor = generalDbHelper.getReadableDatabase().query(
                LikedStationsTable.tableName,
                LikedStationsTable.projection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<String> likedStationsIds = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String stationId = cursor.getString(cursor.getColumnIndexOrThrow("stationId"));
                likedStationsIds.add(stationId);
            }
            cursor.close();
        }

        return likedStationsIds;
    }

    public boolean addLikedStation(String stationId) {
        SQLiteDatabase db = generalDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stationId", stationId);
        db.insert(LikedStationsTable.tableName, null, values);
        return true;
    }

    public void removeLikedStation(String stationId) {
        generalDbHelper.getWritableDatabase().delete(LikedStationsTable.tableName, "stationId = ?", new String[]{stationId});
    }
}
