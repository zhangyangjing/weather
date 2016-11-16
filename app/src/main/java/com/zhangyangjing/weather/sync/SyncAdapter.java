package com.zhangyangjing.weather.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.zhangyangjing.weather.settings.SettingsUtil;
import com.zhangyangjing.weather.sync.heweather.Heweather;
import com.zhangyangjing.weather.sync.tide.Tide;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;

/**
 * Created by zhangyangjing on 03/11/2016.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();

    private Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync() called with: account = ["
                + account + "], bundle = ["
                + bundle + "], authority = ["
                + authority + "], contentProviderClient = ["
                + contentProviderClient + "], syncResult = ["
                + syncResult + "]");

        String city = SettingsUtil.getCurrentCity(mContext);
        Heweather.sync(mContext.getContentResolver(), city);


        Interval interval = new Interval(
                DateTime.now().withFieldAdded(DurationFieldType.days(), -1),
                DateTime.now().withFieldAdded(DurationFieldType.days(), 3));
        try {
            Tide.sync(mContext.getContentResolver(), city, interval);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
