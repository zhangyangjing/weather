
package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Astro {

    @SerializedName("sr")
    @Expose
    public String sr;
    @SerializedName("ss")
    @Expose
    public String ss;

}
