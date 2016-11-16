package com.zhangyangjing.weather.provider.weather;


import static com.zhangyangjing.weather.provider.weather.WeatherContract.Weather;
import static com.zhangyangjing.weather.provider.weather.WeatherContract.WeatherDaily;
import static com.zhangyangjing.weather.provider.weather.WeatherContract.WeatherHourly;
import static com.zhangyangjing.weather.provider.weather.WeatherContract.WeatherNow;
import static com.zhangyangjing.weather.provider.weather.WeatherContract.Tide;
import static com.zhangyangjing.weather.provider.weather.WeatherDatabase.Tables;

/**
 * Created by zhangyangjing on 5/13/16.
 */
public enum WeatherUriEnum {
    CITY(100, "cities", WeatherContract.City.CONTENT_TYPE_ID, Tables.CITY, false, true, true),
    WEATHER(101, "weathers", Weather.CONTENT_TYPE_ID, Tables.WEATHER, true, false, true),
    WEATHER_NOW(102, "cities/*/weathers/now", WeatherNow.CONTENT_TYPE_ID, null, true, true, false),
    WEATHER_HOURLY(103, "cities/*/weathers/hourly", WeatherHourly.CONTENT_TYPE_ID, null, false, true, false),
    WEATHER_DAILY(104, "cities/*/weathers/daily", WeatherDaily.CONTENT_TYPE_ID, null, false, true, false),
    TIDE(105, "cities/*/weathers/tide", Tide.CONTENT_TYPE_ID, Tables.TIDE, false, true, false),
    TIDE_WRITE(106, "tides", Tide.CONTENT_TYPE_ID, Tables.TIDE, false, false, true);

    public int code;
    public String matchPath;
    public String table;
    public String contentType;
    public boolean canRead;
    public boolean canWrite;

    WeatherUriEnum(int code, String matchPath, String contentTypeId,
                   String table, boolean item, boolean canRead, boolean canWrite) {
        this.code = code;
        this.matchPath = matchPath;
        this.table = table;
        this.contentType = item ? WeatherContract.makeContentItemType(contentTypeId) :
                WeatherContract.makeContentType(contentTypeId);
        this.canRead = canRead;
        this.canWrite = canWrite;
    }
}
