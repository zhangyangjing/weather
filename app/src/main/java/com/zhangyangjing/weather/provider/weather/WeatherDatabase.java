package com.zhangyangjing.weather.provider.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.zhangyangjing.weather.provider.weather.WeatherContract.City;
import static com.zhangyangjing.weather.provider.weather.WeatherContract.Tide;
import static com.zhangyangjing.weather.provider.weather.WeatherContract.Weather;


/**
 * Created by zhangyangjing on 5/14/16.
 */
public class WeatherDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "weather";

    public WeatherDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    interface Tables {
        String CITY = "city";
        String WEATHER = "weather";
        String TIDE = "tide";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.CITY + " ("
                + City._ID + " TEXT PRIMARY KEY,"
                + City.FILTERS + " TEXT NOT NULL,"
                + City.DISTRICT + " TEXT NOT NULL,"
                + City.CITY + " TEXT NOT NULL,"
                + City.PROVINCE + " TEXT NOT NULL,"
                + "UNIQUE (" + City._ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.WEATHER + " ("
                + Weather._ID + " TEXT PRIMARY KEY,"
                + Weather.DATA + " TEXT,"
                + Weather.DATE + " TEXT,"
                + "UNIQUE (" + Weather._ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.TIDE + " ("
                + Tide._ID + " TEXT ,"
                + Tide.DATE + " LONG,"
                + Tide.HEIGHT + " INTEGER,"
                + "PRIMARY KEY(" + Tide._ID + "," + Tide.DATE + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
