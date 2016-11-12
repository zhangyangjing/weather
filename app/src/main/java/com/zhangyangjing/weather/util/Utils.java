package com.zhangyangjing.weather.util;

import android.content.Context;
import android.util.TypedValue;

import java.util.Calendar;


/**
 * Created by zhangyangjing on 10/11/2016.
 */

public class Utils {

    public static int dp2px(Context context, int dpValue) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue,
                context.getResources().getDisplayMetrics());
    }

    public static String formateUpdateTime(String timeStr) {
        final long DURATION_SECOND = 1000;
        final long DURATION_MINUTE = 60 * DURATION_SECOND;
        final long DURATION_HOUR = 60 * DURATION_MINUTE;
        final long DURATION_DAY = 24 * DURATION_HOUR;

        Calendar calendar = Calendar.getInstance();
        long duration = calendar.getTimeInMillis() -DbUtil.str2date(timeStr).getTimeInMillis();

        long days = duration / DURATION_DAY;
        if (0 < days)
            return String.format("update: %d days ago", days);

        long hours = duration / DURATION_HOUR;
        if (0 < hours)
            return String.format("update: %d hours ago", hours);

        long minutes = duration / DURATION_MINUTE;
        if (0 < minutes)
            return String.format("update: %d minutes ago", minutes);

        long seconds = duration / DURATION_SECOND;
        if (5 < seconds)
            return String.format("update: %d seconds ago", seconds);
        else
            return "update: just now";
    }
}
