package com.zhangyangjing.weather.util;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

/**
 * Created by zhangyangjing on 01/11/2016.
 */
public class AnimUtils {
    public static final int ANIM_DURATION_SHORT = 195;
    public static final int ANIM_DURATION_MEDIUM = 225;
    public static final int ANIM_DURATION_LONG = 375;

    private AnimUtils() {
    }

    private static Interpolator fastOutSlowIn;
    private static Interpolator fastOutLinearIn;
    private static Interpolator linearOutSlowIn;

    public static Interpolator getFastOutSlowInInterpolator(Context context) {
        if (fastOutSlowIn == null) {
            fastOutSlowIn = AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.fast_out_slow_in);
        }
        return fastOutSlowIn;
    }

    public static Interpolator getFastOutLinearInInterpolator(Context context) {
        if (fastOutLinearIn == null) {
            fastOutLinearIn = AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.fast_out_linear_in);
        }
        return fastOutLinearIn;
    }

    public static Interpolator getLinearOutSlowInInterpolator(Context context) {
        if (linearOutSlowIn == null) {
            linearOutSlowIn = AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.linear_out_slow_in);
        }
        return linearOutSlowIn;
    }
}
