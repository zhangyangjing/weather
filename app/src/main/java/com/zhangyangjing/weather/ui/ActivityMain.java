package com.zhangyangjing.weather.ui;

import android.accounts.Account;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.provider.weather.WeatherContract;
import com.zhangyangjing.weather.ui.fragment.FragmentDistricts;
import com.zhangyangjing.weather.ui.fragment.FragmentNow;
import com.zhangyangjing.weather.ui.fragment.FragmentSearch;
import com.zhangyangjing.weather.util.AccountUtil;
import com.zhangyangjing.weather.util.AnimUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityMain extends AppCompatActivity implements
        FragmentSearch.SearchListener, FragmentDistricts.FragmentDistricsListener,
        SwipeRefreshLayout.OnRefreshListener, SyncStatusObserver {
    private static final String TAG = ActivityMain.class.getSimpleName();
    private static final boolean DEBUG = true;

    private FragmentNow mFrgNow;
    private FragmentSearch mFrgSearch;
    private Object mSyncStatusHandler;

    @BindView(R.id.scrim) View mScrim;
    @BindView(R.id.sr_refresh) SwipeRefreshLayout mRefresh;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFrgSearch = (FragmentSearch) getSupportFragmentManager().findFragmentById(R.id.frg_search);
        mFrgNow = (FragmentNow) getSupportFragmentManager().findFragmentById(R.id.frg_now);
        mRefresh.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSyncStatusHandler = getContentResolver().addStatusChangeListener(
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mSyncStatusHandler) {
            getContentResolver().removeStatusChangeListener(mSyncStatusHandler);
            mSyncStatusHandler = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (true == mFrgSearch.onKeyDown(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRefresh() {
        Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        getContentResolver().requestSync(
                AccountUtil.getSyncAccount(this),
                WeatherContract.CONTENT_AUTHORITY,
                extras);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onEnterSearch(int x, int y) {
        mScrim.setVisibility(View.VISIBLE);
        mScrim.setAlpha(0.7f);
        float endRadius = (float) Math.hypot(x, mScrim.getHeight() - y);
        Animator animator = ViewAnimationUtils.createCircularReveal(
                mScrim,
                x,
                y,
                0.0f,
                endRadius)
                .setDuration(AnimUtils.ANIM_DURATION_MEDIUM);
        animator.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this));
        animator.start();
    }

    @Override
    public void onExitSearch() {
        mScrim.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mScrim.setVisibility(View.GONE);
                    }
                })
                .setDuration(AnimUtils.ANIM_DURATION_MEDIUM)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this))
                .start();
    }

    @Override
    public void onSearchResult() {

    }

    @Override
    public void onDistrictPositionChange(int left, int top, int right, int bottom) {
        mFrgNow.setDistrictRect(left, top, right, bottom);
    }

    @Override
    public void onStatusChanged(int which) {
        Account account = AccountUtil.getSyncAccount(getApplicationContext());
        final boolean refreshing = getContentResolver().isSyncActive(
                account, WeatherContract.CONTENT_AUTHORITY);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(refreshing);
            }
        });
    }
}
