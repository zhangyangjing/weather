package com.zhangyangjing.weather.sync.tide;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by zhangyangjing on 15/11/2016.
 */

public class AuthIntercept implements Interceptor {
    private String mReferer;

    public AuthIntercept(String referer) {
        mReferer = referer;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Headers headers = chain.request().headers().newBuilder()
                .add("Referer", mReferer).build();
        return chain.proceed(chain.request().newBuilder().headers(headers).build());
    }
}
