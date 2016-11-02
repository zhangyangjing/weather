
package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tmp {

    @SerializedName("max")
    @Expose
    public String max;
    @SerializedName("min")
    @Expose
    public String min;

}
