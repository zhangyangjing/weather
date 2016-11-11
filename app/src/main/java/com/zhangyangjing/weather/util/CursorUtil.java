package com.zhangyangjing.weather.util;

import android.database.Cursor;

/**
 * Created by zhangyangjing on 11/11/2016.
 */

public class CursorUtil {
    public static String getString(Cursor cursor, String keyName) {
        return cursor.getString(cursor.getColumnIndex(keyName));
    }

    public static int getInt(Cursor cursor, String keyName) {
        return cursor.getInt(cursor.getColumnIndex(keyName));
    }
}
