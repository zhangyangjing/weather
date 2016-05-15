package com.zhangyangjing.weather.provider.weather;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

/**
 * Created by zhangyangjing on 5/13/16.
 */
public class WeatherProviderUriMatcher {
    private UriMatcher mUriMatcher;
    private SparseArray<WeatherUriEnum> mEnumsMap = new SparseArray<>();

    public WeatherProviderUriMatcher() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        buildUriMatcher();
    }

    public WeatherUriEnum matchUri(Uri uri) {
        final int code = mUriMatcher.match(uri);
        try {
            return matchCode(code);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Unknown uri " + uri);
        }
    }

    private void buildUriMatcher() {
        WeatherUriEnum[] uris = WeatherUriEnum.values();
        for (int i = 0; i < uris.length; i++) {
            mUriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, uris[i].path, uris[i].code);
            mEnumsMap.put(uris[i].code, uris[i]);
        }
    }

    private WeatherUriEnum matchCode(int code) {
        WeatherUriEnum weatherUriEnum = mEnumsMap.get(code);
        if (weatherUriEnum != null) {
            return weatherUriEnum;
        } else {
            throw new UnsupportedOperationException("Unknown uri with code " + code);
        }
    }
}
