package com.zhangyangjing.weather.util;

import android.content.Context;
import android.util.TypedValue;

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
}
