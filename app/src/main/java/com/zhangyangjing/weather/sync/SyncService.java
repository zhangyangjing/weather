package com.zhangyangjing.weather.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by zhangyangjing on 03/11/2016.
 */

public class SyncService extends Service {
    private static SyncAdapter sSyncAdapter;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
