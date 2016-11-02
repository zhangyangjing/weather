package com.zhangyangjing.weather.sync.heweather;

import com.zhangyangjing.weather.sync.heweather.model.HeWeatherDataWrapper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhangyangjing on 02/11/2016.
 */

public class WeatherDataConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                            Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new WeatherDataConverter(type, annotations, retrofit);
    }

    class WeatherDataConverter<T> implements Converter<ResponseBody, T> {
        Type mType;
        Retrofit mRetrofit;
        Annotation[] mAnnotations;

        WeatherDataConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            mType = type;
            mAnnotations = annotations;
            mRetrofit = retrofit;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            HeWeatherDataWrapper wrapper = (HeWeatherDataWrapper) GsonConverterFactory.create()
                    .responseBodyConverter(HeWeatherDataWrapper.class, mAnnotations, mRetrofit)
                    .convert(value);
            return (T) wrapper.heWeatherDataService30.get(0);
        }
    }
}
