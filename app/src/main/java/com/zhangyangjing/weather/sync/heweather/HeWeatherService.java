package com.zhangyangjing.weather.sync.heweather;

import com.zhangyangjing.weather.sync.heweather.model.HeWeatherData;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by zhangyangjing on 02/11/2016.
 */
public interface HeWeatherService {
    String ENDPOINT = "http://api.heweather.com/x3/";

    @GET("weather")
    Observable<HeWeatherData> getCityWeather(@Query("cityid") String cityid);
}
