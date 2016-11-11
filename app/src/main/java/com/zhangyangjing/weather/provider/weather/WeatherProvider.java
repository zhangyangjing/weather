package com.zhangyangjing.weather.provider.weather;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zhangyangjing.weather.sync.heweather.model.DailyForecast;
import com.zhangyangjing.weather.sync.heweather.model.HeWeatherData;
import com.zhangyangjing.weather.sync.heweather.model.HourlyForecast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by zhangyangjing on 5/13/16.
 */
public class WeatherProvider extends ContentProvider {
    private static final String TAG = WeatherProvider.class.getSimpleName();
    private static final boolean DEBUG = true;

    private WeatherProviderUriMatcher mWeatherProviderUriMatcher;
    private SQLiteOpenHelper mOpenHelper;
    private CityFilter mCityFilter;

    @Override
    public boolean onCreate() {
        mOpenHelper = new WeatherDatabase(getContext());
        mWeatherProviderUriMatcher = new WeatherProviderUriMatcher();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (DEBUG) Log.d(TAG, "query() called with: uri = [" + uri + "], projection = ["
                + projection + "], selection = [" + selection + "], selectionArgs = ["
                + selectionArgs + "], sortOrder = [" + sortOrder + "]");

        WeatherUriEnum weatherUriEnum = mWeatherProviderUriMatcher.matchUri(uri);
        if (false == weatherUriEnum.canRead)
            return null;

        switch (weatherUriEnum) {
            case CITY: {
                String filter = WeatherContract.City.getSearchFilter(uri);
                Cursor cursor = doQueryCityWithFilter(projection,
                        selection, selectionArgs, sortOrder, filter);
                cursor.setNotificationUri(getContext().getContentResolver(),
                        WeatherContract.City.CONTENT_URI);
                return cursor;
            }
            case WEATHER_NOW: {
                String city = WeatherContract.WeatherNow.getCityId(uri);
                Cursor cursor = doQueryWeatherNow(city);
                cursor.setNotificationUri(getContext().getContentResolver(),
                        WeatherContract.WeatherNow.buildObserveUri(city));
                return cursor;
            }
            case WEATHER_HOURLY: {
                String city = WeatherContract.WeatherHourly.getCityId(uri);
                Cursor cursor = doQueryWeatherHourly(city);
                cursor.setNotificationUri(getContext().getContentResolver(),
                        WeatherContract.WeatherHourly.buildObserveUri(city));
                return cursor;
            }
            case WEATHER_DAILY: {
                String city = WeatherContract.WeatherDaily.getCityId(uri);
                Cursor cursor = doQueryWeatherDaily(city);
                cursor.setNotificationUri(getContext().getContentResolver(),
                        WeatherContract.WeatherDaily.buildObserveUri(city));
                return cursor;
            }
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return mWeatherProviderUriMatcher.matchUri(uri).contentType;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Log.v(TAG, "insert " + uri + " " + values);

        WeatherUriEnum weatherUriEnum = mWeatherProviderUriMatcher.matchUri(uri);
        if (false == weatherUriEnum.canWrite)
            return null;

        if (weatherUriEnum.table != null) {
            mOpenHelper.getWritableDatabase().insertOrThrow(weatherUriEnum.table, null, values);
        }

        switch (weatherUriEnum) {
            case WEATHER:
                Log.v(TAG, "notifychange on:" + WeatherContract.Weather.buildObserveUri(
                        values.getAsString(WeatherContract.Weather._ID)));
                getContext().getContentResolver().notifyChange(
                        WeatherContract.Weather.buildObserveUri(
                                values.getAsString(WeatherContract.Weather._ID)),
                        null);
                break;
            case CITY:
                return WeatherContract.City.buildCityUri(
                        values.getAsString(WeatherContract.City._ID));
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "delete() called with: uri = [" + uri + "], selection = ["
                + selection + "], selectionArgs = [" + selectionArgs + "]");
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "update() called with: uri = [" + uri + "], values = [" + values
                + "], selection = [" + selection + "], selectionArgs = [" + selectionArgs + "]");
        return 0;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(WeatherContract.City.CONTENT_URI, null);
            return results;
        } finally {
            db.endTransaction();
        }
    }

    private Cursor doQueryCityWithFilter(String[] projection, String selection,
                                         String[] selectionArgs, String sortOrder, String filter) {
        if (DEBUG) Log.d(TAG, "doQueryCityWithFilter() called with: projection = ["
                + projection + "], selection = [" + selection + "], selectionArgs = ["
                + selectionArgs + "], sortOrder = [" + sortOrder + "], filter = [" + filter + "]");

        synchronized (this) {
            if (null == mCityFilter)
                mCityFilter = new CityFilter(getContext());
        }

        if (TextUtils.isEmpty(filter))
            return mOpenHelper.getReadableDatabase().query(WeatherUriEnum.CITY.table,
                    projection, selection, selectionArgs, null, null, sortOrder);


        Set<String> result = mCityFilter.filter(filter);
        if (DEBUG) Log.v(TAG, "doQueryCityWithFilter result:" + result);

        if (0 == result.size())
            return new MatrixCursor(null == projection ? new String[]{"_id"} : projection);

        String queryStr = String.format("SELECT * FROM %s WHERE %S IN ('%S')",
                WeatherUriEnum.CITY.table, WeatherContract.City._ID, TextUtils.join("','", result));
        return mOpenHelper.getReadableDatabase().rawQuery(queryStr, null);
    }

    private Cursor doQueryWeatherNow(String city) {
        MatrixCursor cursor = new MatrixCursor(WeatherContract.WeatherNow.getColumns());
        HeWeatherData data = getWeatherData(city);
        if (null == data)
            return cursor;

        cursor.addRow(new String[]{
                city,
                data.aqi.city.aqi,
                data.aqi.city.co,
                data.aqi.city.no2,
                data.aqi.city.o3,
                data.aqi.city.pm10,
                data.aqi.city.pm25,
                data.aqi.city.so2,
                data.now.tmp,
                data.now.fl,
                data.now.hum,
                data.now.pres,
                data.now.vis,
                translateUv(data.suggestion.uv.brf),
                data.now.cond.code,
                data.now.wind.spd,
                data.now.wind.sc,
                translateWindDirect(data.now.wind.dir),
                data.now.wind.deg});

        return cursor;
    }

    private Cursor doQueryWeatherHourly(String city) {
        MatrixCursor cursor = new MatrixCursor(WeatherContract.WeatherHourly.getColumns());
        HeWeatherData data = getWeatherData(city);
        if (null == data)
            return cursor;

        for (HourlyForecast forecast : data.hourlyForecast) {
            cursor.addRow(new String[]{
                    city,
                    forecast.date,
                    forecast.tmp,
                    forecast.wind.spd,
                    forecast.wind.sc,
                    translateWindDirect(forecast.wind.dir),
                    forecast.wind.deg});
        }

        return cursor;
    }

    private Cursor doQueryWeatherDaily(String city) {
        MatrixCursor cursor = new MatrixCursor(WeatherContract.WeatherDaily.getColumns());
        HeWeatherData data = getWeatherData(city);
        if (null == data)
            return cursor;

        for (DailyForecast forecast : data.dailyForecast) {
            cursor.addRow(new String[]{
                    city,
                    forecast.date,
                    forecast.tmp.max,
                    forecast.tmp.min,
                    forecast.cond.codeD,
                    forecast.cond.codeN,
                    forecast.wind.spd,
                    forecast.wind.sc,
                    translateWindDirect(forecast.wind.dir),
                    forecast.wind.deg});
        }

        return cursor;
    }

    private HeWeatherData getWeatherData(String city) {
        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                WeatherUriEnum.WEATHER.table,
                new String[]{WeatherContract.Weather.DATA},
                WeatherContract.Weather._ID + "=?",
                new String[]{city},
                null,
                null,
                null);

        if (0 == cursor.getCount())
            return null;

        cursor.moveToFirst();
        String dataStr = cursor.getString(cursor.getColumnIndex(WeatherContract.Weather.DATA));
        return new Gson().fromJson(dataStr, HeWeatherData.class);
    }

    private String translateUv(String uvStr) {
        String uv = WeatherContract.Uv.Hig.toString();
        switch (uvStr) {
            case "强":
            case "最强":
                uv = WeatherContract.Uv.Hig.toString();
                break;
            case "中":
                uv = WeatherContract.Uv.Med.toString();
                break;
            case "弱":
            case "最弱":
                uv = WeatherContract.Uv.Low.toString();
                break;
        }
        return uv;
    }

    private String translateWindDirect(String windDir) {
        String wd = WeatherContract.WindDirect.E.toString();
        switch (windDir) {
            case "北风":
                wd = WeatherContract.WindDirect.N.toString();
                break;
            case "东北风":
                wd = WeatherContract.WindDirect.NE.toString();
                break;
            case "东风":
                wd = WeatherContract.WindDirect.E.toString();
                break;
            case "东南风":
                wd = WeatherContract.WindDirect.SE.toString();
                break;
            case "南风":
                wd = WeatherContract.WindDirect.S.toString();
                break;
            case "西南风":
                wd = WeatherContract.WindDirect.SW.toString();
                break;
            case "西风":
                wd = WeatherContract.WindDirect.W.toString();
                break;
            case "西北风":
                wd = WeatherContract.WindDirect.NW.toString();
                break;
        }
        return wd;
    }
}
