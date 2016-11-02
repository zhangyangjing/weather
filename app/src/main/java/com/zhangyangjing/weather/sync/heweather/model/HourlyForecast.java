
package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HourlyForecast {

    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("hum")
    @Expose
    public String hum;
    @SerializedName("pop")
    @Expose
    public String pop;
    @SerializedName("pres")
    @Expose
    public String pres;
    @SerializedName("tmp")
    @Expose
    public String tmp;
    @SerializedName("wind")
    @Expose
    public Wind wind;

}
