package com.zhangyangjing.weather.sync.heweather;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by zhangyangjing on 02/11/2016.
 */

public class Heweather {
    public static HeWeatherService getApi() {
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(new AuthIntercept("67af075f28cb49829e6a2175dfaea5ff"))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(new WeatherDataConverterFactory())
                .client(okHttpClient)
                .baseUrl(HeWeatherService.ENDPOINT)
                .build();

        HeWeatherService service = retrofit.create(HeWeatherService.class);
        return service;
    }
}
