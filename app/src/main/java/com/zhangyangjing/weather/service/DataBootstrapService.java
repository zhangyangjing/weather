package com.zhangyangjing.weather.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.zhangyangjing.weather.settings.SettingsUtil;
import com.zhangyangjing.weather.util.CityDataImporter;


public class DataBootstrapService extends IntentService {
    private static final String TAG = DataBootstrapService.class.getSimpleName();

    private static final Boolean DEBUG = false;
    public static final String ACTION_IMPORT_FINISH = "import_finish";

    public DataBootstrapService() {
        super(TAG);
    }

    public static boolean startDatastrapIfNecessary(Context context) {

        int currentVersion = SettingsUtil.getCitydataVersion(context);
        int newVersion = getNewVersion(context);
        if (newVersion <= currentVersion)
            return false;

        Intent intent = new Intent(context, DataBootstrapService.class);
        context.startService(intent);
        return true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (DEBUG) Log.d(TAG, "onHandleIntent() called with: intent = [" + intent + "]");

            CityDataImporter.importData(this, "city.data");
            SettingsUtil.setCitydataVersion(this, getNewVersion(this));

            Intent broadcastIntent = new Intent(ACTION_IMPORT_FINISH);
            sendBroadcast(broadcastIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getNewVersion(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            return ai.metaData.getInt("city_data_version", -1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
