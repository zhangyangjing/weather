package com.zhangyangjing.weather;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zhangyangjing.weather.provider.weather.WeatherContract;
import com.zhangyangjing.weather.sync.heweather.Heweather;
import com.zhangyangjing.weather.sync.heweather.model.HeWeather;
import com.zhangyangjing.weather.util.AnimUtils;
import com.zhangyangjing.weather.util.ImeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.schedulers.Schedulers;

public class ActivityMain extends AppCompatActivity {
    private static final String TAG = ActivityMain.class.getSimpleName();

    private static final int ANIM_DURATION_SHORT = 195;
    private static final int ANIM_DURATION_MEDIUM = 225;
    private static final int ANIM_DURATION_LONG = 375;

    private static final boolean DEBUG = false;
    private static final String KEY_FILTER = "filter";

    private AnimatedVectorDrawable mAnimateAddToBack;
    private AnimatedVectorDrawable mAnimateBackToAdd;

    private int mBtnSearchBackOffsetLeft;
    private int mBtnSearchBackOffsetRight;

    @BindView(R.id.btnSearchback)
    ImageButton mBtnSearchback;

    @BindView(R.id.search_view)
    SearchView mSearchView;

    @BindView(R.id.scrim)
    View mScrim;

    private SearchStatus mSearchStatus;
    private MyLoaderManagerCallback mLoaderManagerCallback;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLoaderManagerCallback = new MyLoaderManagerCallback();
        getSupportLoaderManager().initLoader(0, null, mLoaderManagerCallback);

        mAnimateAddToBack = (AnimatedVectorDrawable) getDrawable(R.drawable.animate_add_to_back);
        mAnimateBackToAdd = (AnimatedVectorDrawable) getDrawable(R.drawable.animate_back_to_add);

        int autoCompleteTextViewID = getResources()
                .getIdentifier("search_src_text", "id", getPackageName());
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)
                mSearchView.findViewById(autoCompleteTextViewID);
        searchAutoComplete.setThreshold(1);

        mSearchView.setSuggestionsAdapter(new MyAdapter(this));
        mSearchView.setOnQueryTextListener(new MyQueryTextListener());

        caculateSearchbackCoord();
        mSearchStatus = SearchStatus.NORMAL;
        mBtnSearchback.setTranslationX(mBtnSearchBackOffsetRight);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && SearchStatus.SEARCH == mSearchStatus) {
            exitSearchMode();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.btnSearchback)
    public void onClick(View v) {
        Heweather.getApi().getCityWeather("CN101010100")
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<HeWeather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: e = [" + e + "]");
                    }

                    @Override
                    public void onNext(HeWeather heWeather) {
                        Log.d(TAG, "onNext() called with: heWeather = [" + heWeather + "]");
                        Log.v(TAG, "" + heWeather.heWeatherDataService30.get(0).status);
                        Log.v(TAG, "" + heWeather.heWeatherDataService30.get(0).aqi.city.pm25);
                        Log.v(TAG, "" + heWeather.heWeatherDataService30.get(0).now.tmp);
                    }
                });

        if (SearchStatus.NORMAL == mSearchStatus) {
            enterSearchMode();
        } else {
            exitSearchMode();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void enterSearchMode() {
        mBtnSearchback.setImageDrawable(mAnimateAddToBack);
        mAnimateAddToBack.reset();
        mAnimateAddToBack.start();

        mScrim.setVisibility(View.VISIBLE);
        mScrim.setAlpha(0.7f);
        float endRadius = (float) Math.hypot(mBtnSearchBackOffsetRight,
                mScrim.getHeight() - mSearchView.getBottom());
        Animator animator = ViewAnimationUtils.createCircularReveal(
                mScrim,
                mBtnSearchBackOffsetRight,
                mSearchView.getBottom(),
                0.0f,
                endRadius)
                .setDuration(ANIM_DURATION_MEDIUM);
        animator.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this));
        animator.start();

        mBtnSearchback.animate()
                .translationX(mBtnSearchBackOffsetLeft)
                .setDuration(ANIM_DURATION_LONG)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ImeUtils.showIme(mSearchView);
                    }
                })
                .start();

        mSearchView.setQuery("", false);
        mSearchView.setVisibility(View.VISIBLE);
        mSearchView.setAlpha(0.0f);
        mSearchView.animate()
                .alpha(1.0f)
                .setStartDelay(100)
                .setDuration(ANIM_DURATION_MEDIUM)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSearchView.setVisibility(View.VISIBLE);
                        mSearchView.setAlpha(1.0f);
                        mSearchView.requestFocus();
                    }
                })
                .start();

        mSearchStatus = SearchStatus.SEARCH;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void exitSearchMode() {
        mBtnSearchback.setImageDrawable(mAnimateBackToAdd);
        mAnimateBackToAdd.reset();
        mAnimateBackToAdd.start();

        mScrim.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mScrim.setVisibility(View.GONE);
                    }
                })
                .setDuration(ANIM_DURATION_MEDIUM)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this))
                .start();

        mBtnSearchback.animate()
                .translationX(mBtnSearchBackOffsetRight)
                .setDuration(ANIM_DURATION_SHORT)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this))
                .setStartDelay(100)
                .start();

        ImeUtils.hideIme(mSearchView);
        mSearchView.animate()
                .alpha(0.0f)
                .setDuration(ANIM_DURATION_SHORT)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSearchView.setVisibility(View.GONE);
                        mSearchView.clearFocus();
                    }
                })
                .start();
        mSearchStatus = SearchStatus.NORMAL;
    }

    private void caculateSearchbackCoord() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        mBtnSearchBackOffsetLeft = margin;
        mBtnSearchBackOffsetRight = screenWidth
                - margin - mBtnSearchback.getDrawable().getIntrinsicWidth();
    }

    class MyQueryTextListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (DEBUG) Log.d(TAG, "onQueryTextChange() called with: newText = [" + newText + "]");
            Bundle data = new Bundle();
            data.putString(KEY_FILTER, newText);
            getSupportLoaderManager().restartLoader(0, data, mLoaderManagerCallback);
            return false;
        }
    }

    class MyLoaderManagerCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String filter = null == args ? "" : args.getString(KEY_FILTER, "");
            Uri uri = TextUtils.isEmpty(filter) ?
                    WeatherContract.City.CONTENT_URI : WeatherContract.City.buildSearchUri(filter);
            return new CursorLoader(getBaseContext(), uri, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (DEBUG) Log.d(TAG, "onLoadFinished() called with: loader = ["
                    + loader + "], cursor = [" + cursor + "]");
            mSearchView.getSuggestionsAdapter().swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader loader) {
            if (DEBUG) Log.d(TAG, "onLoaderReset() called with: loader = [" + loader + "]");
            mSearchView.getSuggestionsAdapter().swapCursor(null);
        }
    }

    class MyAdapter extends CursorAdapter {
        public MyAdapter(Context context) {
            super(context, null, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = View.inflate(context, android.R.layout.simple_list_item_2, null);
            view.setTag(new ViewHolder(view));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String district = cursor.getString(cursor.getColumnIndex(WeatherContract.City.DISTRICT));
            String city = cursor.getString(cursor.getColumnIndex(WeatherContract.City.CITY));
            String province = cursor.getString(cursor.getColumnIndex(WeatherContract.City.PROVINCE));

            ViewHolder vh = (ViewHolder) view.getTag();
            vh.tvDistrict.setText(district);
            vh.tvCity.setText(String.format("%s %s", province, city));
        }

        class ViewHolder {
            @BindView(android.R.id.text1)
            TextView tvDistrict;
            @BindView(android.R.id.text2)
            TextView tvCity;

            ViewHolder(View v) {
                ButterKnife.bind(this, v);
            }
        }
    }

    enum SearchStatus {
        NORMAL, SEARCH
    }
}
