package com.zhangyangjing.weather.provider.weather;

/**
 * Created by zhangyangjing on 5/13/16.
 */
public enum WeatherUriEnum {

    CITY(101, "cities", WeatherContract.City.CONTENT_TYPE_ID, false, WeatherDatabase.Tables.CITY),
    WEATHER(100, "cities/*/weather", WeatherContract.Weather.CONTENT_TYPE_ID, true, WeatherDatabase.Tables.WEATHER);

    public int code;
    public String path;
    public String table;
    public String contentType;

    WeatherUriEnum(int code, String path, String contentTypeId, boolean item, String table) {
        this.code = code;
        this.path = path;
        this.contentType = item ? WeatherContract.makeContentItemType(contentTypeId) :
                WeatherContract.makeContentType(contentTypeId);
        this.table = table;
    }
}
