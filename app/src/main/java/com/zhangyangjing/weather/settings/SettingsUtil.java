package com.zhangyangjing.weather.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zhangyangjing on 5/14/16.
 */
public class SettingsUtil {
    private static final String TAG = SettingsUtil.class.getSimpleName();

    private static final String PREF_DATA_BOOTSTRAP_DONE = "data_bootstrap_done";


    public static void markDataBootstrapDone(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_DATA_BOOTSTRAP_DONE, true).commit();
    }

    public static boolean isDataBootstrapDone(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_DATA_BOOTSTRAP_DONE, false);
    }
}
