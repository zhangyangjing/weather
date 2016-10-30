package com.zhangyangjing.weather.provider.weather;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

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
                WeatherContract.City.CONTENT_URI, false, CityFilter.this);
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
        long debugStart;
        if (DEBUG) {
            Log.v(TAG, "start build trie tree");
            debugStart = System.currentTimeMillis();
        }

        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.City.CONTENT_URI,
                new String[]{WeatherContract.City._ID, WeatherContract.City.FILTERS},
                null, null, null);

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

        if (DEBUG) {
            long duration = System.currentTimeMillis() - debugStart;
            Log.v(TAG, "build trie tree use time: " + duration);
        }

        mContext.getContentResolver().notifyChange(WeatherContract.City.CONTENT_URI, this);
    }
}
