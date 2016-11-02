
package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Aqi {

    @SerializedName("city")
    @Expose
    public City city;

}
