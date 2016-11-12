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
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.provider.weather.WeatherContract;
import com.zhangyangjing.weather.provider.weather.WeatherContract.WeatherNow;
import com.zhangyangjing.weather.settings.SettingsUtil;
import com.zhangyangjing.weather.util.DbUtil;
import com.zhangyangjing.weather.util.Utils;
import com.zhangyangjing.weather.util.WeatherUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyangjing on 05/11/2016.
 */

public class FragmentNow extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FragmentNow.class.getSimpleName();

    private static final int LOADER_ID = 0;
    private static final String LOADER_PARAM_CITY = "city";

    private static final int TARGET_CURRENT_TEMP_TEXT_SIZE = 16; // dp
    private static final int TARGET_WEATHER_ICON_TEXT_SIZE = 16; // dp
    private static final int TARGET_WEATHER_INFO_MARGIN_TOP = 18; // dp

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

    @BindView(R.id.vg_weather_info) ViewGroup mVgWeatherInfo;

    @BindView(R.id.detail_line1) View mDetailLine1;
    @BindView(R.id.detail_line2) View mDetailLine2;

    private Interpolator mInterpolator = new FastOutSlowInInterpolator();

    private int mActionBarHeight;
    private int mStatusBarHeight;

    private int mWeatherInfoBottom;
    private int mWeatherInfoMarginTop;
    private float mCurrentTempTextSize;
    private float mWeatherIconTextSize;

    private float mTargetCurrentTempTextSize;
    private float mTargetWeatherIconTextSize;
    private float mTargetWeatherInfoMarginTop;
    private float mTargetWeatherInfoTranslationX = 0;

    private int mTargetWeatherX = 0;
    private int mDistrictRight = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBarHeight = getActionBarHeight();
        mStatusBarHeight = getStatusBarHeight();
        mTargetCurrentTempTextSize = Utils.dp2px(getContext(), TARGET_CURRENT_TEMP_TEXT_SIZE);
        mTargetWeatherIconTextSize = Utils.dp2px(getContext(), TARGET_WEATHER_ICON_TEXT_SIZE);
        mTargetWeatherInfoMarginTop = Utils.dp2px(getContext(), TARGET_WEATHER_INFO_MARGIN_TOP);
    }

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setZ(15.0f);
        view.setPadding(0, mStatusBarHeight, 0, 0);
        mCurrentTempTextSize = mTvCurrentTemp.getTextSize();
        mWeatherIconTextSize = mTvWeatherIcon.getTextSize();
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

//                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//                        mVgWeatherInfo.getLayoutParams();
//                params.setMargins(0, mActionBarHeight, 0, 0);
                mWeatherInfoMarginTop = mActionBarHeight;
                mWeatherInfoBottom = mVgWeatherInfo.getBottom() + mActionBarHeight + mStatusBarHeight;
                v.removeOnLayoutChangeListener(this);

                mTargetWeatherX = calculateWeatherInfoTargetX();
                if (0 != mDistrictRight)
                    mTargetWeatherInfoTranslationX = mDistrictRight - mTargetWeatherX;
            }
        });
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
        String date = DbUtil.getString(cursor, WeatherNow.DATE);
        int humidity = DbUtil.getInt(cursor, WeatherNow.HUM);
        int windSpeed = DbUtil.getInt(cursor, WeatherNow.WSPD);
        String windDir = DbUtil.getString(cursor, WeatherNow.WDIR);
        int temp = DbUtil.getInt(cursor, WeatherNow.TMP);
        int feelLike = DbUtil.getInt(cursor, WeatherNow.FL);
        int pm25 = DbUtil.getInt(cursor, WeatherNow.PM25);
        int pm10 = DbUtil.getInt(cursor, WeatherNow.PM10);
        int aqi = DbUtil.getInt(cursor, WeatherNow.AQI);
        int visibility = DbUtil.getInt(cursor, WeatherNow.VIS);
        int cond = DbUtil.getInt(cursor, WeatherNow.COND);
        String uv = DbUtil.getString(cursor, WeatherNow.UV);

        Spannable wind = WeatherUtil.spannableMetricString(windSpeed, "m/s");
        mTvWind.setText(new SpannableStringBuilder(windDir).append(wind));
        mTvHumidity.setText(WeatherUtil.spannableMetricString(humidity, "%"));
        mTvVisibility.setText(WeatherUtil.spannableMetricString(visibility, "KM"));
        mTvCurrentTemp.setText(temp + "° ");
        mTvFellLike.setText(feelLike + "°");
        mTvPm10.setText(pm10 + "");
        mTvPm25.setText(pm25 + "");
        mTvAqi.setText(aqi + "");
        mTvUv.setText(uv);
        mTvUpdateTime.setText(Utils.formateUpdateTime(date));
        mTvWeatherIcon.setText(WeatherUtil.cond2icon(cond));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void setDistrictRect(int left, int top, int right, int bottom) {
        mDistrictRight = right;
        if (0 != mTargetWeatherX)
            mTargetWeatherInfoTranslationX = right - mTargetWeatherX;
        mTvUpdateTime.setTranslationX(right);
    }

    void updateBottom(int bottom) {
        updateDetailLine(mDetailLine1, bottom);
        updateDetailLine(mDetailLine2, bottom);
        updateWeatherInfo(bottom);
    }

    private void updateWeatherInfo(int bottom) {

        if (bottom <= mWeatherInfoBottom) {

            float rate = (mWeatherInfoBottom - bottom) /
                    (float) (mWeatherInfoBottom - mStatusBarHeight - mActionBarHeight);
            rate = 1 - mInterpolator.getInterpolation(rate);

            float dCurrentTempTextSize = mCurrentTempTextSize - mTargetCurrentTempTextSize;
            float dWeatherIconTextSize = mWeatherIconTextSize - mTargetWeatherIconTextSize;
            float dWeatherInfoMarginTop = mWeatherInfoMarginTop - mTargetWeatherInfoMarginTop;

            float currentTempTextSize = mTargetCurrentTempTextSize + dCurrentTempTextSize * rate;
            float weatherIconTextSize = mTargetWeatherIconTextSize + dWeatherIconTextSize * rate;
            float weatherInfoMarginTop = mTargetWeatherInfoMarginTop + dWeatherInfoMarginTop * rate;
            float weatherInfoTranslationX = mTargetWeatherInfoTranslationX * (1 - rate);

            mTvUpdateTime.setAlpha(Math.max(rate - 0.2f, 0));
            mTvCurrentTemp.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTempTextSize);
            mTvWeatherIcon.setTextSize(TypedValue.COMPLEX_UNIT_PX, weatherIconTextSize);
            mVgWeatherInfo.setTranslationX(weatherInfoTranslationX);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                    mVgWeatherInfo.getLayoutParams();
            params.setMargins(0, (int) weatherInfoMarginTop, 0, 0);
        } else {
            mTvUpdateTime.setAlpha(1.0f);
            mTvCurrentTemp.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCurrentTempTextSize);
            mTvWeatherIcon.setTextSize(TypedValue.COMPLEX_UNIT_PX, mWeatherIconTextSize);
            mVgWeatherInfo.setTranslationX(0);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                    mVgWeatherInfo.getLayoutParams();
            params.setMargins(0, mWeatherInfoMarginTop, 0, 0);
        }
    }

    private void updateDetailLine(View line, int bottom) {
        int height = line.getHeight();

        line.setPivotY(0);

        int invisibleHeight = line.getBottom() - height / 2;
        if (bottom <= invisibleHeight) {
            line.setVisibility(View.GONE);
        } else {
            line.setVisibility(View.VISIBLE);
            float rate = Math.min((bottom - invisibleHeight) / (height / 2f), 1.0f);
            rate = 1 - mInterpolator.getInterpolation(1 - rate); // reverse interpolator
            float scale = 0.5f + rate / 2f;
            line.setAlpha(rate);
            line.setScaleY(scale);
            line.setScaleX(scale);
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int calculateWeatherInfoTargetX() {
        float tempSize = mTvCurrentTemp.getTextSize();
        float iconSize = mTvWeatherIcon.getTextSize();

        mTvCurrentTemp.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTargetCurrentTempTextSize);
        mTvWeatherIcon.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTargetWeatherIconTextSize);
        mVgWeatherInfo.measure(
                View.MeasureSpec.makeMeasureSpec(getView().getWidth(), View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(getView().getHeight(), View.MeasureSpec.AT_MOST));
        int targetWidth = mVgWeatherInfo.getMeasuredWidth();
        int targetWeatherX = (getView().getWidth() - targetWidth) / 2;

        mTvCurrentTemp.setTextSize(TypedValue.COMPLEX_UNIT_PX, tempSize);
        mTvWeatherIcon.setTextSize(TypedValue.COMPLEX_UNIT_PX, iconSize);

        return targetWeatherX;
    }

    public int getActionBarHeight() {
        int result = 0;
        TypedValue typedValue = new TypedValue();
        if (getContext().getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
            result = TypedValue.complexToDimensionPixelSize(
                    typedValue.data,
                    getResources().getDisplayMetrics());
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
