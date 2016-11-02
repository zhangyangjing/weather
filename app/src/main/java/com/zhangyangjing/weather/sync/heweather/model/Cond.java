
package com.zhangyangjing.weather.sync.heweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cond {

    @SerializedName("code_d")
    @Expose
    public String codeD;
    @SerializedName("code_n")
    @Expose
    public String codeN;
    @SerializedName("txt_d")
    @Expose
    public String txtD;
    @SerializedName("txt_n")
    @Expose
    public String txtN;

}
