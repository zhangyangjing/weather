package com.zhangyangjing.weather.sync.tide;

import com.zhangyangjing.weather.sync.tide.model.TideData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by zhangyangjing on 15/11/2016.
 */

public interface TideService {
    String ENDPOINT = "http://ocean.cnss.com.cn/";

    @GET("index.php?m=resource&c=tide&a=get_tide_data")
    Call<TideData> getTide(@Query("portid") String portId, @Query("date") String date);
}
