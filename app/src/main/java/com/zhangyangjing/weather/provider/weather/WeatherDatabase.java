package com.zhangyangjing.weather.provider.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhangyangjing on 5/14/16.
 */
public class WeatherDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "weather.db";

    public WeatherDatabase(Context context) {
        super(context, "weather", null, DATABASE_VERSION);
    }

    interface Tables {
        String CITY = "city";
        String WEATHER = "weather";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.CITY + " ("
                + WeatherContract.City._ID + " TEXT PRIMARY KEY,"
                + WeatherContract.City.FILTERS + " TEXT NOT NULL,"
                + WeatherContract.City.DISTRICT + " TEXT NOT NULL,"
                + WeatherContract.City.CITY + " TEXT NOT NULL,"
                + WeatherContract.City.PROVINCE + " TEXT NOT NULL,"
                + "UNIQUE (" + WeatherContract.City._ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.WEATHER + " ("
                + WeatherContract.Weather._ID + " TEXT PRIMARY KEY,"
                + WeatherContract.Weather.WEATHER + " TEXT,"
                + WeatherContract.Weather.TIME + " DATETIME,"
                + "UNIQUE (" + WeatherContract.Weather._ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
