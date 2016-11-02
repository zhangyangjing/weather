
package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate from http://www.jsonschema2pojo.org/
 */
public class HeWeatherDataWrapper {
    @SerializedName("HeWeather data service 3.0")
    @Expose
    public List<HeWeatherData> heWeatherDataService30 = new ArrayList<HeWeatherData>();
}
