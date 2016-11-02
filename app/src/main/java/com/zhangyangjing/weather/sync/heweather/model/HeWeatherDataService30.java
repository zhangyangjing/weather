
package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class HeWeatherDataService30 {

    @SerializedName("aqi")
    @Expose
    public Aqi aqi;
    @SerializedName("daily_forecast")
    @Expose
    public List<DailyForecast> dailyForecast = new ArrayList<DailyForecast>();
    @SerializedName("hourly_forecast")
    @Expose
    public List<HourlyForecast> hourlyForecast = new ArrayList<HourlyForecast>();
    @SerializedName("now")
    @Expose
    public Now now;
    @SerializedName("status")
    @Expose
    public String status;
}
