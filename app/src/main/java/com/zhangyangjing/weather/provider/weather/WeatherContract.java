package com.zhangyangjing.weather.provider.weather;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by zhangyangjing on 5/13/16.
 */
public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.zhangyangjing.weather";

    public static final String CONTENT_TYPE_APP_BASE = "weather.";

    public static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd."
            + CONTENT_TYPE_APP_BASE;

    public static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd."
            + CONTENT_TYPE_APP_BASE;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static String makeContentType(String id) {
        if (id != null) {
            return CONTENT_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static String makeContentItemType(String id) {
        if (id != null) {
            return CONTENT_ITEM_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    interface CityColumns extends BaseColumns {
        String DISTRICT = "district";
        String CITY = "city";
        String PROVINCE = "province";
        String FILTERS = "filters";
    }

    interface WeatherColumns extends BaseColumns {
        String DATA = "data";
    }

    interface WeatherNowColumns extends BaseColumns {
        String AQI = "aqi";
        String CO = "co";
        String NO2 = "no2";
        String O3 = "o3";
        String PM10 = "pm10";
        String PM25 = "pm25";
        String SO2 = "so2";
        String TMP = "tmp"; // 当前温度(摄氏度)
        String FL = "fl"; // 体感温度
        String HUM = "hum"; // 湿度(%)
        String PRES = "pres"; // 气压
        String VIS = "vis"; // 能见度(km)
        String UV = "uv"; // 紫外线
        String COND = "cond_d"; // 日间天气
        String WSPD = "wind_spd"; // 风速(Kmph)
        String WSCD = "wind_scd"; // 风力等级
        String WDIR = "wind_dir"; // 风向(方向)
        String WDEG = "wind_deg"; // 风向(角度)
    }

    interface WeatherDailyColumns extends BaseColumns {
        String DATE = "date"; // 日期
        String TMPH = "tmph"; // 最高温度
        String TMPL = "tmpl"; // 最低温度
        String CONDD = "cond_d"; // 日间天气
        String CONDN = "cond_n"; // 晚间天气
        String WSPD = "wind_spd"; // 风速(Kmph)
        String WSCD = "wind_scd"; // 风力等级
        String WDIR = "wind_dir"; // 风向(方向)
        String WDEG = "wind_deg"; // 风向(角度)
    }

    interface WeatherHourlyColumns extends BaseColumns {
        String DATE = "date"; // 日期
        String TMP = "tmp"; // 当前温度(摄氏度)
        String WSPD = "wind_spd"; // 风速(Kmph)
        String WSCD = "wind_scd"; // 风力等级
        String WDIR = "wind_dir"; // 风向(方向)
        String WDEG = "wind_deg"; // 风向(角度)
    }

    public static class City implements CityColumns {
        public static final String PATH = "cities";
        public static final String CONTENT_TYPE_ID = "city";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        private static final String SEARCH_FILTER = "filter";

        public static Uri buildSearchUri(String query) {
            return CONTENT_URI.buildUpon().appendQueryParameter(SEARCH_FILTER, query).build();
        }

        public static Uri buildCityUri(String cityId) {
            return CONTENT_URI.buildUpon().appendPath(cityId).build();
        }

        public static String getSearchFilter(Uri uri) {
            return uri.getQueryParameter(SEARCH_FILTER);
        }
    }

    public static abstract class WeatherBase {
        public static Uri buildObserveUri(String city) {
            return City.CONTENT_URI.buildUpon()
                    .appendEncodedPath(city)
                    .appendEncodedPath(Weather.PATH)
                    .build();
        }
    }

    public static abstract class WeatherQueryBase extends WeatherBase {
        public static String getCityId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class Weather extends WeatherBase implements WeatherColumns {
        public static final String PATH = "weathers";
        public static final String CONTENT_TYPE_ID = "weather";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
    }

    public static class WeatherNow extends WeatherQueryBase implements WeatherNowColumns {
        public static final String CONTENT_TYPE_ID = "weather_now";

        public static Uri buildQueryUri(String city) {
            return City.CONTENT_URI.buildUpon()
                    .appendEncodedPath(city)
                    .appendEncodedPath(Weather.PATH)
                    .appendPath("now")
                    .build();
        }

        public static String[] getColumns() {
            return new String[]{
                    _ID,
                    AQI,
                    CO,
                    NO2,
                    O3,
                    PM10,
                    PM25,
                    SO2,
                    TMP,
                    FL,
                    HUM,
                    PRES,
                    VIS,
                    UV,
                    COND,
                    WSPD,
                    WSCD,
                    WDIR,
                    WDEG};
        }
    }

    public static class WeatherDaily extends WeatherQueryBase implements WeatherDailyColumns {
        public static final String CONTENT_TYPE_ID = "weather_daily";

        public static Uri buildQueryUri(String city) {
            return City.CONTENT_URI.buildUpon()
                    .appendEncodedPath(city)
                    .appendEncodedPath(Weather.PATH)
                    .appendPath("daily")
                    .build();
        }

        public static String[] getColumns() {
            return new String[]{
                    _ID,
                    DATE,
                    TMPH,
                    TMPL,
                    CONDD,
                    CONDN,
                    WSPD,
                    WSCD,
                    WDIR,
                    WDEG};
        }
    }

    public static class WeatherHourly extends WeatherQueryBase implements WeatherHourlyColumns {
        public static final String CONTENT_TYPE_ID = "city";

        public static Uri buildQueryUri(String city) {
            return City.CONTENT_URI.buildUpon()
                    .appendEncodedPath(city)
                    .appendEncodedPath(Weather.PATH)
                    .appendPath("hourly")
                    .build();
        }

        public static String[] getColumns() {
            return new String[]{
                    _ID,
                    DATE,
                    TMP,
                    WSPD,
                    WSCD,
                    WDIR,
                    WDEG};
        }
    }

    public enum Uv {
        Low, Med, Hig;
    }

    public enum WindDirect {
        N, NE, E, SE, S, SW, W, NW;
    }
}
