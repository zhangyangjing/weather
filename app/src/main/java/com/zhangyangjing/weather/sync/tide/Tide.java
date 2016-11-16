package com.zhangyangjing.weather.sync.tide;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.google.common.primitives.Ints;
import com.zhangyangjing.weather.provider.weather.WeatherContract;
import com.zhangyangjing.weather.sync.tide.model.TideData;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import static android.content.ContentValues.TAG;

/**
 * Created by zhangyangjing on 15/11/2016.
 */

public class Tide {
    private static TideService sService;
    private static DateTimeFormatter sFormatter;

    public static void sync(ContentResolver resolver, String city, Interval interval) throws
            IOException, RemoteException, OperationApplicationException {
        String port = getPort(city);
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        for (String date : intervalToDays(interval)) {
            Log.v(TAG, "sync tide:" + date);
            TideData tideData = getService().getTide(port, date).execute().body();
            for (List<Long> data : tideData.data) {
                long time = data.get(0);
                int height = Ints.checkedCast(data.get(1));

                ops.add(ContentProviderOperation.newInsert(WeatherContract.Tide.CONTENT_URI)
                        .withValue(WeatherContract.Tide._ID, city)
                        .withValue(WeatherContract.Tide.DATE, time)
                        .withValue(WeatherContract.Tide.HEIGHT, height)
                        .build());
            }
        }
        resolver.applyBatch(WeatherContract.CONTENT_AUTHORITY, ops);
    }

    private static List<String> intervalToDays(Interval interval) {
        List<String> result = new LinkedList<>();
        DateTimeFormatter formatter = getDateTimeFormatter();
        int days = Days.standardDaysIn(interval.toPeriod()).getDays();
        for (int i = 0; i < days; i++) {
            DateTime date = interval.getStart().withFieldAdded(DurationFieldType.days(), i);
            result.add(formatter.print(date));
        }
        return result;
    }

    private static String getPort(String city) {
        return "38"; // TODO: 15/11/2016 query port id & SYNC MORE PERIOD
    }


    private static TideService getService() {
        if (null == sService) {
            synchronized (Tide.class) {
                if (null == sService) {
                    OkHttpClient okHttpClient = new OkHttpClient
                            .Builder()
                            .addInterceptor(new AuthIntercept("http://ocean.cnss.com.cn/"))
                            .build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .addConverterFactory(new TideDataConverterFactory())
                            .client(okHttpClient)
                            .baseUrl(TideService.ENDPOINT)
                            .build();

                    sService = retrofit.create(TideService.class);
                }
            }
        }

        return sService;
    }

    private static DateTimeFormatter getDateTimeFormatter() {
        if (null == sFormatter) {
            synchronized(Tide.class) {
                if (null == sFormatter) {
                    sFormatter = ISODateTimeFormat.forFields(
                            Arrays.asList(DateTimeFieldType.year(),
                                    DateTimeFieldType.monthOfYear(),
                                    DateTimeFieldType.dayOfMonth()),
                            true, true);
                }
            }
        }
        return sFormatter;
    }
}
