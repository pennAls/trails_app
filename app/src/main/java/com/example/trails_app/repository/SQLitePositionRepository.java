package com.example.trails_app.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.trails_app.AppDatabaseHelper;
import com.example.trails_app.domain.entities.PositionEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLitePositionRepository implements PositionRepository {
    private final AppDatabaseHelper dbHelper;
    public SQLitePositionRepository(AppDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void save(PositionEntity position,String trailId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trailId", trailId);
        values.put("latitude", position.getLatitude());
        values.put("longitude", position.getLongitude());
        values.put("instantaneousSpeed", position.getInstantaneousSpeed());
        values.put("accuracy",position.getAccuracy());
        if (position.getTimestamp() != null) {
            values.put("timestamp", position.getTimestamp().toString());
        }
        db.insert("Positions", null, values);
        db.close();

    }
}