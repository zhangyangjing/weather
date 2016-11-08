package com.zhangyangjing.weather.ui.fragment;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
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

    @BindView(R.id.detail_line1) View mDetailLine1;
    @BindView(R.id.detail_line2) View mDetailLine2;

    Interpolator mInterpolator = new FastOutSlowInInterpolator();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = new Bundle();
        bundle.putString(LOADER_PARAM_CITY, SettingsUtil.getCurrentCity(getContext()));
        getLoaderManager().initLoader(LOADER_ID, bundle, this);

        LayoutParams params = (LayoutParams) getView().getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (null == behavior)
            params.setBehavior(new FragmentNow.Behavior(this));
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
        cursor.moveToFirst();
        int humidity = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.HUM));
        int windSpeed = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.WSPD));
        String windDir = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherNow.WDIR));
        int temp = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.TMP));
        int feelLike = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.FL));
        int pm25 = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.PM25));
        int pm10 = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.PM10));
        int aqi = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.AQI));
        int visibility = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.VIS));
        String uv = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherNow.UV));

        mTvWind.setText(generateWindInfo(windDir, windSpeed));
        mTvHumidity.setText(generateSpannableString(humidity, "%"));
        mTvVisibility.setText(generateSpannableString(visibility, "KM"));
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

    void updateBottom(int bottom) {
        updateDetailLine(mDetailLine1, bottom);
        updateDetailLine(mDetailLine2, bottom);
    }

    private void updateDetailLine(View line, int bottom) {
        int height = line.getHeight();

        line.setPivotY(0);

        int invisibleHeight = line.getBottom() - height/2;
        if (bottom <= invisibleHeight) {
            line.setVisibility(View.GONE);
        } else {
            line.setVisibility(View.VISIBLE);
            float rate = Math.min((bottom-invisibleHeight)/(height/2f), 1.0f);
            rate = 1 - mInterpolator.getInterpolation(1 - rate); // reverse interpolator
            float scale = 0.5f + rate/2f;
            line.setAlpha(rate);
            line.setScaleY(scale);
            line.setScaleX(scale);
        }
    }

    private Spannable generateSpannableString(int value, String SizeStr) {
        Spannable span = new SpannableString(value + SizeStr);
        span.setSpan(
                new RelativeSizeSpan(0.5f),
                span.length() - SizeStr.length(),
                span.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    private Spannable generateWindInfo(String direct, int speed) {
        return generateSpannableString(speed, "m/s");
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static class Behavior<V extends View> extends CoordinatorLayout.Behavior<V> {
        private static final String TAG = Behavior.class.getSimpleName();

        private FragmentNow mFragmentNow;

        public Behavior(FragmentNow fragmentNow) {
            mFragmentNow = fragmentNow;
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
            if (dependency.getClass() == AppBarLayout.class)
                return true;
            return false;
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
            child.setBottom(dependency.getBottom());
            mFragmentNow.updateBottom(dependency.getBottom());
            return super.onDependentViewChanged(parent, child, dependency);
        }
    }
}
