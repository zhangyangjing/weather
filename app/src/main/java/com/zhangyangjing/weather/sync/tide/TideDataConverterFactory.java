package com.zhangyangjing.weather.sync.tide;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangyangjing.weather.sync.tide.model.TideData;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by zhangyangjing on 15/11/2016.
 */

public class TideDataConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                            Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new TideDataConverter(type, annotations, retrofit);
    }

    class TideDataConverter<T> implements Converter<ResponseBody, T> {
        private Gson mGson;

        TideDataConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            mGson = new Gson();
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            List<TideData> data = mGson.fromJson(mGson.newJsonReader(value.charStream()),
                    new TypeToken<List<TideData>>(){}.getType());
            return (T) data.get(0);
        }
    }
}
