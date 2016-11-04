package com.zhangyangjing.weather.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Collections;
import java.util.Set;

/**
 * Created by zhangyangjing on 5/14/16.
 */
public class SettingsUtil {
    private static final String TAG = SettingsUtil.class.getSimpleName();

    private static final String PREF_KEY_CITY_DATA_VERSION = "city_data_version";
    private static final String PREF_KEY_CURRENT_CITY = "current_city";
    private static final String PREF_KEY_CITY_LIST = "city_list";


    public static int getCitydataVersion(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PREF_KEY_CITY_DATA_VERSION, -1);
    }

    public static void setCitydataVersion(Context context, int version) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(PREF_KEY_CITY_DATA_VERSION, version).apply();
    }

    public static String getCurrentCity(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_KEY_CURRENT_CITY, null);
    }

    public static void setCurrentCity(Context context, String city) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_KEY_CURRENT_CITY, city).apply();
    }

    public static Set<String> getCityList(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getStringSet(PREF_KEY_CITY_LIST, Collections.EMPTY_SET);
    }

    public static void setCityList(Context context, Set<String> cities) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putStringSet(PREF_KEY_CITY_LIST, cities).apply();
    }
}
