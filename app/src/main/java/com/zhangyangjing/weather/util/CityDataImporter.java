package com.zhangyangjing.weather.util;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.TimingLogger;

import com.google.common.collect.ObjectArrays;
import com.zhangyangjing.weather.provider.weather.WeatherContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by zhangyangjing on 5/14/16.
 */
public class CityDataImporter {
    private static final String TAG = CityDataImporter.class.getSimpleName();

    private static final boolean DEBUG = false;

    public static void importData(Context context, String assetFilePath) throws
            IOException, RemoteException, OperationApplicationException {
        TimingLogger timings;
        if (DEBUG) timings = new TimingLogger(TAG, "importCityData");

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        InputStream is = context.getAssets().open(assetFilePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while (null != (line = reader.readLine())) {
            if (line.startsWith("#"))
                continue;

            String[] segs = line.split(" ");
            String id = segs[0];
            String abbr = segs[1];
            String abbr2 = segs[2];
            String district = segs[3];
            String city = segs[4];
            String province = segs[5];

            String[] filters = ObjectArrays.concat(abbr.split(","), abbr2.split(","), String.class);
            filters = ObjectArrays.concat(filters, district);
            String filtersStr = TextUtils.join(",", filters);

            ops.add(ContentProviderOperation.newInsert(WeatherContract.City.CONTENT_URI)
                    .withValue(WeatherContract.City._ID, id)
                    .withValue(WeatherContract.City.FILTERS, filtersStr)
                    .withValue(WeatherContract.City.DISTRICT, district)
                    .withValue(WeatherContract.City.CITY, city)
                    .withValue(WeatherContract.City.PROVINCE, province)
                    .build());
        }
        is.close();

        if (DEBUG) timings.addSplit("build ops");

        context.getContentResolver().applyBatch(WeatherContract.CONTENT_AUTHORITY, ops);
        context.getContentResolver().notifyChange(WeatherContract.City.CONTENT_URI, null);

        if (DEBUG) timings.addSplit("apply ops");
        if (DEBUG) timings.dumpToLog();
    }
}
