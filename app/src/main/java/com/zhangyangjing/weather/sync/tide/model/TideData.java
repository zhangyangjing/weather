package com.zhangyangjing.weather.sync.tide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyangjing on 15/11/2016.
 */

public class TideData {
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("data")
    @Expose
    public List<List<Long>> data = new ArrayList<>();
}
