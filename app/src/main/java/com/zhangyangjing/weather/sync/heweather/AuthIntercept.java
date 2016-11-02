package com.zhangyangjing.weather.sync.heweather;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by zhangyangjing on 02/11/2016.
 */

public class AuthIntercept implements Interceptor {
    private String mAccessToken;

    public AuthIntercept(String accessToken) {
        mAccessToken = accessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = chain.request().url().newBuilder()
                .addQueryParameter("key", mAccessToken)
                .build();
        return chain.proceed(chain.request().newBuilder().url(url).build());
    }
}
