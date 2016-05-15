package com.zhangyangjing.weather.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.zhangyangjing.weather.settings.SettingsUtil;
import com.zhangyangjing.weather.util.CityDataImporter;

import java.io.IOException;

public class DataBootstrapService extends IntentService {
    private static final String TAG = DataBootstrapService.class.getSimpleName();

    public DataBootstrapService() {
        super(TAG);
    }

    public static void startDatastrapIfNecessary(Context context) {
        if (false == SettingsUtil.isDataBootstrapDone(context)) {
            Intent intent = new Intent(context, DataBootstrapService.class);
            context.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.v(TAG, "start data bootstrap");
            long start = System.currentTimeMillis();

            CityDataImporter.importData(this, "city.data");
            SettingsUtil.markDataBootstrapDone(this);

            long duration = System.currentTimeMillis() - start;
            Log.v(TAG, "data bootstrap use time: " + duration);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
