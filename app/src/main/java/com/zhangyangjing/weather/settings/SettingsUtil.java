package com.zhangyangjing.weather.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zhangyangjing on 5/14/16.
 */
public class SettingsUtil {
    private static final String TAG = SettingsUtil.class.getSimpleName();

    private static final String PREF_DATA_BOOTSTRAP_DONE = "city_data_version";


    public static void getCitydataVersion(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_DATA_BOOTSTRAP_DONE, true).commit();
    }

    public static boolean setCitydataVersion(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_DATA_BOOTSTRAP_DONE, false);
    }
}
