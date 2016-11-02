
package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cond_ {

    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("txt")
    @Expose
    public String txt;

}
