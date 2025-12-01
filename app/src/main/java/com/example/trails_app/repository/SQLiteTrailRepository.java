package com.example.trails_app.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.trails_app.AppDatabaseHelper;
import com.example.trails_app.domain.entities.PositionEntity;
import com.example.trails_app.domain.entities.TrailEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLiteTrailRepository implements TrailRepository {
    private final AppDatabaseHelper dbHelper;

    public SQLiteTrailRepository(AppDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void save(TrailEntity trail) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trailId",trail.trailId());
        values.put("name", trail.name());
        values.put("beginning", trail.beginning() != null ? trail.beginning().toString() : null);
        values.put("ending", trail.ending() != null ? trail.ending().toString() : null);
        values.put("caloric_expenditure", trail.caloricExpenditure());
        values.put("maximum_speed", trail.maximumSpeed());
        values.put("distance", trail.distance());
        values.put("duration", trail.duration());

        long id = db.insert("Trails", null, values);
        db.close();
    }
    @Override
    public List<TrailEntity> findAll() {
        List<TrailEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Trails", null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String id = String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("trailId")));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String begStr = cursor.getString(cursor.getColumnIndexOrThrow("beginning"));
                    String endStr = cursor.getString(cursor.getColumnIndexOrThrow("ending"));
                    double cal = cursor.getDouble(cursor.getColumnIndexOrThrow("caloric_expenditure"));
                    double avg = cursor.getDouble(cursor.getColumnIndexOrThrow("average_speed"));
                    double max = cursor.getDouble(cursor.getColumnIndexOrThrow("maximum_speed"));
                    double dist = cursor.getDouble(cursor.getColumnIndexOrThrow("distance"));
                    String dur = cursor.getString(cursor.getColumnIndexOrThrow("duration"));

                    list.add(new TrailEntity(
                            id,
                            name,
                            begStr != null ? LocalDateTime.parse(begStr) : null,
                            endStr != null ? LocalDateTime.parse(endStr) : null,
                            cal,
                            avg,
                            max,
                            dist,
                            dur
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    @Override
    public void deleteById(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Trails", "trailId = ?", new String[]{id});
        db.close();
    }

    @Override
    public List<PositionEntity> findPositionsByTrailId(String trailId) {
        List<PositionEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "Positions",
                null,
                "trailId = ?",
                new String[]{trailId},
                null, null, null
        );

        try {
            if (cursor.moveToFirst()) {
                do {
                    String posId = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("positionId")));
                    String tId = cursor.getString(cursor.getColumnIndexOrThrow("trailId"));
                    double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                    double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                    double speed = cursor.getDouble(cursor.getColumnIndexOrThrow("instantaneousSpeed"));
                    String timeStr = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                    LocalDateTime time = (timeStr != null) ? LocalDateTime.parse(timeStr) : null;
                    float accuracy = cursor.getFloat(cursor.getColumnIndexOrThrow("accuracy"));
                    list.add(new PositionEntity(posId, tId, lat, lng, time, speed,accuracy));

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();

        }
        return list;
    }
    @Override
    public void updateTrail(TrailEntity trail) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", trail.name());
        values.put("beginning", trail.beginning() != null ? trail.beginning().toString() : null);
        values.put("ending", trail.ending() != null ? trail.ending().toString() : null);
        values.put("caloric_expenditure", trail.caloricExpenditure());
        values.put("average_speed", trail.averageSpeed());
        values.put("maximum_speed", trail.maximumSpeed());
        values.put("distance", trail.distance());
        values.put("duration", trail.duration());

        db.update("Trails", values, "trailId = ?", new String[]{trail.trailId()});
        db.close();
    }
}
