
package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

    @SerializedName("deg")
    @Expose
    public String deg;
    @SerializedName("dir")
    @Expose
    public String dir;
    @SerializedName("sc")
    @Expose
    public String sc;
    @SerializedName("spd")
    @Expose
    public String spd;

}
