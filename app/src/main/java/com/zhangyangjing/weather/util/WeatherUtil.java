package com.zhangyangjing.weather.util;

import android.util.SparseArray;

/**
 * Created by zhangyangjing on 11/11/2016.
 */

public class WeatherUtil {
    private static String WEATHER_ICONS_UNKNOW = "\uf07b";
    private static SparseArray<String> WEATHER_ICONS = new SparseArray();
    static {
        WEATHER_ICONS.put(100, "\uf00d");
        WEATHER_ICONS.put(101, "\uf013");
        WEATHER_ICONS.put(102, "\uf041");
        WEATHER_ICONS.put(103, "\uf002");
        WEATHER_ICONS.put(104, "\uf013");

        WEATHER_ICONS.put(200, "\uf021");
        WEATHER_ICONS.put(201, "\uf021");
        WEATHER_ICONS.put(202, "\uf021");
        WEATHER_ICONS.put(203, "\uf021");
        WEATHER_ICONS.put(204, "\uf021");
        WEATHER_ICONS.put(205, "\uf050");
        WEATHER_ICONS.put(206, "\uf050");
        WEATHER_ICONS.put(207, "\uf0cc");
        WEATHER_ICONS.put(208, "\uf0cd");
        WEATHER_ICONS.put(209, "\uf0cd");
        WEATHER_ICONS.put(210, "\uf073");
        WEATHER_ICONS.put(211, "\uf073");
        WEATHER_ICONS.put(212, "\uf056");
        WEATHER_ICONS.put(213, "\uf050");

        WEATHER_ICONS.put(300, "\uf009");
        WEATHER_ICONS.put(301, "\uf008");
        WEATHER_ICONS.put(302, "\uf00e");
        WEATHER_ICONS.put(303, "\uf008");
        WEATHER_ICONS.put(304, "\uf068");
        WEATHER_ICONS.put(305, "\uf017");
        WEATHER_ICONS.put(306, "\uf015");
        WEATHER_ICONS.put(307, "\uf019");
        WEATHER_ICONS.put(308, "\uf018");
        WEATHER_ICONS.put(309, "\uf01c");
        WEATHER_ICONS.put(310, "\uf01d");
        WEATHER_ICONS.put(311, "\uf01e");
        WEATHER_ICONS.put(312, "\uf01e");
        WEATHER_ICONS.put(313, "\uf019");

        WEATHER_ICONS.put(400, "\uf00a");
        WEATHER_ICONS.put(401, "\uf01b");
        WEATHER_ICONS.put(402, "\uf064");
        WEATHER_ICONS.put(403, "\uf06b");
        WEATHER_ICONS.put(404, "\uf017");
        WEATHER_ICONS.put(405, "\uf017");
        WEATHER_ICONS.put(406, "\uf017");
        WEATHER_ICONS.put(407, "\uf017");


        WEATHER_ICONS.put(500, "\uf001");
        WEATHER_ICONS.put(501, "\uf003");
        WEATHER_ICONS.put(502, "\uf0b6");
        WEATHER_ICONS.put(503, "\uf063");
        WEATHER_ICONS.put(504, "\uf063");
        WEATHER_ICONS.put(507, "\uf082");
        WEATHER_ICONS.put(508, "\uf082");

        WEATHER_ICONS.put(900, "\uf072");
        WEATHER_ICONS.put(901, "\uf076");
    }

    public static String cond2icon(int condCode) {
        String icon = WEATHER_ICONS.get(condCode);
        return null == icon ? WEATHER_ICONS_UNKNOW : icon;
    }
}
