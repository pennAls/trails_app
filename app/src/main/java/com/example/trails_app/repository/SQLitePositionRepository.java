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
    public void save(PositionEntity position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trailId", position.getTrailId());
        values.put("latitude", position.getLatitude());
        values.put("longitude", position.getLongitude());
        values.put("instantaneousSpeed", position.getInstantaneousSpeed());
        if (position.getTimestamp() != null) {
            values.put("timestamp", position.getTimestamp().toString());
        }
        db.insert("Positions", null, values);
        db.close();

    }

    @Override
    public void saveAll(List<PositionEntity> positions) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (PositionEntity position : positions) {
                ContentValues values = new ContentValues();
                values.put("trailId", position.getTrailId());
                values.put("latitude", position.getLatitude());
                values.put("longitude", position.getLongitude());
                values.put("instantaneousSpeed", position.getInstantaneousSpeed());
                if (position.getTimestamp() != null) {
                    values.put("timestamp", position.getTimestamp().toString());
                }
                db.insert("Positions", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}