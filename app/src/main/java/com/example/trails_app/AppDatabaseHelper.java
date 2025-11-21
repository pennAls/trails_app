package com.example.trails_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class AppDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "trilha_db";
    private static final int VERSION = 1;

    public AppDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Trails ("
                + "trailId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL, "
                + "beginning TEXT, "
                + "ending TEXT, "
                + "caloric_expenditure REAL, "
                + "average_speed REAL, "
                + "maximum_speed REAL"
                + ")");

        db.execSQL("CREATE TABLE Positions ("
                + "positionId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "trailId TEXT NOT NULL, "
                + "latitude REAL, "
                + "longitude REAL, "
                + "instantaneousSpeed REAL, "
                + "timestamp TEXT, "
                + "FOREIGN KEY(trailId) REFERENCES Trails(trailId) ON DELETE CASCADE"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Positions");
        db.execSQL("DROP TABLE IF EXISTS Trails");
        onCreate(db);
    }
}