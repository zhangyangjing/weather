package com.zhangyangjing.weather.provider.weather;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Process;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.Set;

/**
 * Created by zhangyangjing on 5/13/16.
 */
public class WeatherProvider extends ContentProvider {
    private static final String TAG = WeatherContract.class.getSimpleName();
    private static final boolean DEBUG = false;

    private WeatherProviderUriMatcher mWeatherProviderUriMatcher;
    private SQLiteOpenHelper mOpenHelper;
    private CityFilter mCityFilter;

    @Override
    public boolean onCreate() {
        if (DEBUG) Log.v(TAG, String.format("new WeatherProvider tname:%s tid:%d pid:%d id:%s",
                Thread.currentThread().getName(), Process.myTid(), Process.myPid(), this));
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
        switch (weatherUriEnum) {
            case WEATHER:
                String cityId = WeatherContract.Weather.getCityId(uri);
                break;
            case CITY:
                String filter = WeatherContract.City.getSearchFilter(uri);
                Cursor cursor = doQueryCityWithFilter(projection,
                        selection, selectionArgs, sortOrder, filter);
                cursor.setNotificationUri(getContext().getContentResolver(),
                        WeatherContract.City.CONTENT_URI);
                return cursor;
        }
        return null;
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
        if (weatherUriEnum.table != null) {
            mOpenHelper.getWritableDatabase().insertOrThrow(weatherUriEnum.table, null, values);
        }

        switch (weatherUriEnum) {
            case WEATHER:
                return null;
            case CITY:
                return WeatherContract.City.buildCityUri(values.getAsString(WeatherContract.City._ID));
            default:
                return null;
        }
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

    private Cursor doQueryCityWithFilter(String[] projection, String selection,
                                         String[] selectionArgs, String sortOrder, String filter) {
        if (DEBUG) Log.d(TAG, "doQueryCityWithFilter() called with: projection = ["
                + projection + "], selection = [" + selection + "], selectionArgs = ["
                + selectionArgs + "], sortOrder = [" + sortOrder + "], filter = [" + filter + "]");

        Log.v(TAG, String.format("doQueryCityWithFilter pid:%d tid:%d pname:%s", Process.myPid(), Process.myTid(), Thread.currentThread().getName()));

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
            return new MatrixCursor(null == projection ? new String[]{} : projection);

        String queryStr = String.format("SELECT * FROM %s WHERE %S IN ('%S')",
                WeatherUriEnum.CITY.table, WeatherContract.City._ID, TextUtils.join("','", result));
        return mOpenHelper.getReadableDatabase().rawQuery(queryStr, null);
    }
}
