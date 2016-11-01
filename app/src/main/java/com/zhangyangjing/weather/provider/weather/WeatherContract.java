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

    interface CityColumns {

    }

    interface WeatherColumns {

    }

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

    public static class City implements CityColumns, BaseColumns {
        public static final String PATH = "cities";
        public static final String CONTENT_TYPE_ID = "city";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        private static final String SEARCH_FILTER = "filter";

        public static final String FILTERS = "filters";
        public static final String DISTRICT = "district";
        public static final String CITY = "city";
        public static final String PROVINCE = "province";

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

    public static class Weather implements WeatherColumns, BaseColumns {
        public static final String CONTENT_TYPE_ID = "weather";

        public static final String WEATHER = "weather";
        public static final String TIME = "time";

        public static String getCityId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
