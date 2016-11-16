package com.zhangyangjing.weather.util;

import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by zhangyangjing on 11/11/2016.
 */

public class DbUtil {
    private static SimpleDateFormat sIso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getString(Cursor cursor, String keyName) {
        return cursor.getString(cursor.getColumnIndex(keyName));
    }

    public static int getInt(Cursor cursor, String keyName) {
        return cursor.getInt(cursor.getColumnIndex(keyName));
    }

    public static long getLong(Cursor cursor, String keyName) {
        return cursor.getLong(cursor.getColumnIndex(keyName));
    }

    public static String date2str(Calendar calendar) {
        return sIso8601Format.format(calendar.getTime());
    }

    public static Calendar str2date(String str) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sIso8601Format.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  calendar;
    }
}
