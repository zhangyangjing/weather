package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangyangjing on 06/11/2016.
 */

public class Uv {
    @SerializedName("brf")
    @Expose
    public String brf;
    @SerializedName("txt")
    @Expose
    public String txt;
}
