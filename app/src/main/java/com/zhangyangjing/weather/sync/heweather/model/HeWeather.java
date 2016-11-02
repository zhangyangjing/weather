
package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate from http://www.jsonschema2pojo.org/
 */
public class HeWeather {
    @SerializedName("HeWeather data service 3.0")
    @Expose
    public List<HeWeatherDataService30> heWeatherDataService30 = new ArrayList<HeWeatherDataService30>();
}
