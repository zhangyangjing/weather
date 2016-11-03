package com.zhangyangjing.weather.sync.heweather;

import android.content.ContentResolver;
import android.content.ContentValues;

import com.google.gson.Gson;
import com.zhangyangjing.weather.provider.weather.WeatherContract;
import com.zhangyangjing.weather.sync.heweather.model.HeWeatherData;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by zhangyangjing on 02/11/2016.
 */

public class Heweather {
    private static HeWeatherService sService;

    public static void sync(ContentResolver resolver, String city) {
        try {
            HeWeatherData data = getService().getCityWeather(city).execute().body();

            ContentValues contentValues = new ContentValues();
            contentValues.put(WeatherContract.Weather._ID, city);
            contentValues.put(WeatherContract.Weather.DATA, new Gson().toJson(data));
            resolver.insert(WeatherContract.Weather.CONTENT_URI, contentValues);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HeWeatherService getService() {
        if (null == sService) {
            synchronized (Heweather.class) {
                if (null == sService) {
                    OkHttpClient okHttpClient = new OkHttpClient
                            .Builder()
                            .addInterceptor(new AuthIntercept("67af075f28cb49829e6a2175dfaea5ff"))
                            .build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .addConverterFactory(new WeatherDataConverterFactory())
                            .client(okHttpClient)
                            .baseUrl(HeWeatherService.ENDPOINT)
                            .build();

                    sService = retrofit.create(HeWeatherService.class);
                }
            }
        }

        return sService;
    }
}
