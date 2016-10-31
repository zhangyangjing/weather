package com.zhangyangjing.weather.provider.weather;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.TimingLogger;

import com.zhangyangjing.weather.util.TrieTree;

import java.util.Set;

/**
 * Created by zhangyangjing on 5/14/16.
 */
public class CityFilter extends ContentObserver implements Runnable {
    private static final String TAG = CityFilter.class.getSimpleName();
    private static final boolean DEBUG = false;

    private Handler mHandler;
    private Context mContext;
    private TrieTree<String> mTrieTree;

    public CityFilter(Context context) {
        super(null);
        mContext = context;
        mTrieTree = new TrieTree<>();

        HandlerThread thread = new HandlerThread("city_filter");
        thread.start();
        mHandler = new Handler(thread.getLooper());
        mHandler.post(this);

        mContext.getContentResolver().registerContentObserver(
                WeatherContract.City.CONTENT_URI, false, this);
    }

    public synchronized Set<String> filter(String filter) {
        return mTrieTree.get(filter);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        mHandler.removeCallbacks(this);
        mHandler.post(this);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }

    @Override
    public void run() {
        TimingLogger timings;
        if (DEBUG) timings = new TimingLogger(TAG, "build trie tree");

        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.City.CONTENT_URI,
                new String[]{WeatherContract.City._ID, WeatherContract.City.FILTERS},
                null, null, null);

        if (DEBUG) timings.addSplit("query");

        TrieTree<String> newTrieTree = new TrieTree<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(WeatherContract.City._ID));
            String filters = cursor.getString(cursor.getColumnIndex(WeatherContract.City.FILTERS));
            for (String filter : filters.split(","))
                newTrieTree.add(filter, id);
        }
        cursor.close();
        synchronized (this) {
            mTrieTree = newTrieTree;
        }

        if (DEBUG) timings.addSplit("build");
        if (DEBUG) timings.dumpToLog();

        mContext.getContentResolver().notifyChange(WeatherContract.City.CONTENT_URI, this);
    }
}
