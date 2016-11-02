
package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DailyForecast {

    @SerializedName("astro")
    @Expose
    public Astro astro;
    @SerializedName("cond")
    @Expose
    public Cond cond;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("hum")
    @Expose
    public String hum;
    @SerializedName("pcpn")
    @Expose
    public String pcpn;
    @SerializedName("pop")
    @Expose
    public String pop;
    @SerializedName("pres")
    @Expose
    public String pres;
    @SerializedName("tmp")
    @Expose
    public Tmp tmp;
    @SerializedName("vis")
    @Expose
    public String vis;
    @SerializedName("wind")
    @Expose
    public Wind wind;

}
