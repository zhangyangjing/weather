package com.zhangyangjing.weather.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyangjing on 10/11/2016.
 */

public class FontUtil {
    private static Map<TypeFaceEnum, Typeface> sTypeFaces = new HashMap<>();
    private static Object sLock = new Object();

    public static Typeface getTypeface(Context context, TypeFaceEnum typeFaceEnum) {
        if (false == sTypeFaces.containsKey(typeFaceEnum)) {
            synchronized (sLock) {
                if (false == sTypeFaces.containsKey(typeFaceEnum)) {
                    Typeface tf = Typeface.createFromAsset(context.getAssets(), typeFaceEnum.path);
                    sTypeFaces.put(typeFaceEnum, tf);
                }
            }
        }

        return sTypeFaces.get(typeFaceEnum);
    }

    public enum TypeFaceEnum {
        OswaldDei_bold("Oswald-DemiBold.ttf"),
        OswaldMedium("Oswald-Medium.ttf"),
        OswaldLight("Oswald-Light.ttf"),
        OswaldRegular("Oswald-Regular.ttf"),
        WeatherIcons("WeatherIcons.ttf");

        String path;
        TypeFaceEnum(String path) {
            this.path = path;
        }
    }
}

