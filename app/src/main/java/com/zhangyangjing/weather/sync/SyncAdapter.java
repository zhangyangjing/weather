package com.zhangyangjing.weather.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.zhangyangjing.weather.sync.heweather.Heweather;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

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
    public void onPerformSync(Account account, Bundle bundle, String authority, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync() called with: account = [" + account + "], bundle = [" + bundle + "], authority = [" + authority + "], contentProviderClient = [" + contentProviderClient + "], syncResult = [" + syncResult + "]");
        String city = getDefaultSharedPreferences(mContext).getString("current_city", "CN101010100");
        Heweather.sync(mContext.getContentResolver(), city);
    }
}
