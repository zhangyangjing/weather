package com.zhangyangjing.weather.sync.heweather;

import com.zhangyangjing.weather.sync.heweather.model.HeWeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by zhangyangjing on 02/11/2016.
 */
interface HeWeatherService {
    String ENDPOINT = "http://api.heweather.com/x3/";

    @GET("weather")
    Call<HeWeatherData> getCityWeather(@Query("cityid") String cityid);
}
