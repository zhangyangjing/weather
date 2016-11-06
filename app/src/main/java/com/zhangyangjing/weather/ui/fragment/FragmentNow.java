package com.zhangyangjing.weather.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.provider.weather.WeatherContract;
import com.zhangyangjing.weather.settings.SettingsUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyangjing on 05/11/2016.
 */

public class FragmentNow extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FragmentNow.class.getSimpleName();

    private static final int LOADER_ID = 0;
    private static final String LOADER_PARAM_CITY = "city";

    @BindView(R.id.tv_weather_icon) TextView mTvWeatherIcon;
    @BindView(R.id.tv_current_temp) TextView mTvCurrentTemp;
    @BindView(R.id.tv_update_time) TextView mTvUpdateTime;
//    @BindView(R.id.tv_temp_high) TextView mTvTempHight;
//    @BindView(R.id.tv_temp_low) TextView mTvTempLow;
    @BindView(R.id.tv_wind) TextView mTvWind;
    @BindView(R.id.tv_humidity) TextView mTvHumidity;
    @BindView(R.id.tv_feel_like) TextView mTvFellLike;
    @BindView(R.id.tv_uv) TextView mTvUv;
    @BindView(R.id.tv_visibility) TextView mTvVisibility;
    @BindView(R.id.tv_pm25) TextView mTvPm25;
    @BindView(R.id.tv_pm10) TextView mTvPm10;
    @BindView(R.id.tv_aqi) TextView mTvAqi;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = new Bundle();
        bundle.putString(LOADER_PARAM_CITY, SettingsUtil.getCurrentCity(getContext()));
        getLoaderManager().initLoader(LOADER_ID, bundle, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getLoaderManager().destroyLoader(LOADER_ID);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String city = args.getString(LOADER_PARAM_CITY);
        return new CursorLoader(
                getContext(),
                WeatherContract.WeatherNow.buildQueryUri(city),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//            String AQI = "aqi";
//            String CO = "co";
//            String NO2 = "no2";
//            String O3 = "o3";
//            String PM10 = "pm10";
//            String PM25 = "pm25";
//            String SO2 = "so2";
//            String TMP = "tmp"; // 当前温度(摄氏度)
//            String FL = "fl"; // 体感温度
//            String HUM = "hum"; // 湿度(%)
//            String PRES = "pres"; // 气压
//            String VIS = "vis"; // 能见度(km)
//            String WSPD = "wind_spd"; // 风速(Kmph)
//            String WSCD = "wind_scd"; // 风力等级
//            String WDIR = "wind_dir"; // 风向(方向)
//            String WDEG = "wind_deg"; // 风向(角度)

        cursor.moveToFirst();
//        cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.WDIR));
        int humidity = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.HUM));
        int windSpeed = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.WSPD));
        int temp = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.TMP));
        int feelLike = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.FL));
        int pm25 = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.PM25));
        int pm10 = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.PM10));
        int aqi = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.AQI));
        int visibility = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.VIS));
        String uv = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherNow.UV));

        mTvWind.setText(generateSpanableString(windSpeed, "m/s"));
        mTvHumidity.setText(generateSpanableString(humidity, "%"));
        mTvVisibility.setText(generateSpanableString(visibility, "KM"));
        mTvCurrentTemp.setText(temp + "");
        mTvFellLike.setText(feelLike + "°");
        mTvPm10.setText(pm10 + "");
        mTvPm25.setText(pm25 + "");
        mTvAqi.setText(aqi + "");
        mTvUv.setText(uv);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Spannable generateSpanableString(int value, String SizeStr) {
        Spannable span = new SpannableString(value + SizeStr);
        span.setSpan(
                new RelativeSizeSpan(0.5f),
                span.length() - SizeStr.length(),
                span.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }
}
