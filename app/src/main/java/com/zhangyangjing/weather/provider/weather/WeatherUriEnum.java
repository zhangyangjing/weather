package com.zhangyangjing.weather.provider.weather;

/**
 * Created by zhangyangjing on 5/13/16.
 */
public enum WeatherUriEnum {
    CITY(100, "cities", WeatherContract.City.CONTENT_TYPE_ID, WeatherDatabase.Tables.CITY, false, true, true),
    WEATHER(101, "weathers", WeatherContract.Weather.CONTENT_TYPE_ID, WeatherDatabase.Tables.WEATHER, true, false, true),
    WEATHER_NOW(102, "cities/*/weathers/now", WeatherContract.WeatherNow.CONTENT_TYPE_ID, null, true, true, false),
    WEATHER_HOURLY(103, "cities/*/weathers/hourly", WeatherContract.WeatherHourly.CONTENT_TYPE_ID, null, false, true, false),
    WEATHER_DAILY(104, "cities/*/weathers/daily", WeatherContract.WeatherDaily.CONTENT_TYPE_ID, null, false, true, false);

    public int code;
    public String matchPath;
    public String table;
    public String contentType;
    public boolean canRead;
    public boolean canWrite;

    WeatherUriEnum(int code, String matchPath, String contentTypeId, String table, boolean item, boolean canRead, boolean canWrite) {
        this.code = code;
        this.matchPath= matchPath;
        this.table = table;
        this.contentType = item ? WeatherContract.makeContentItemType(contentTypeId) :
                WeatherContract.makeContentType(contentTypeId);
        this.canRead = canRead;
        this.canWrite = canWrite;
    }
}
