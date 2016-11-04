package com.zhangyangjing.weather.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.provider.weather.WeatherContract;
import com.zhangyangjing.weather.settings.SettingsUtil;
import com.zhangyangjing.weather.ui.fragment.FragmentSearch;
import com.zhangyangjing.weather.util.AccountUtil;
import com.zhangyangjing.weather.util.AnimUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMain extends AppCompatActivity implements FragmentSearch.SearchListener {
    private static final String TAG = ActivityMain.class.getSimpleName();
    private static final boolean DEBUG = true;

    private MyLoaderManagerCallback mLoaderManagerCallback;
    private FragmentSearch mFrgSearch;

    @BindView(R.id.scrim)
    View mScrim;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLoaderManagerCallback = new MyLoaderManagerCallback(this);
        mFrgSearch = (FragmentSearch) getSupportFragmentManager().findFragmentById(R.id.frg_search);

        getSupportLoaderManager().initLoader(0, null, mLoaderManagerCallback);
    }

    @OnClick({R.id.btn_test_sync, R.id.btn_test_query})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test_sync:
                Bundle extras = new Bundle();
                extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                getContentResolver().requestSync(
                        AccountUtil.getSyncAccount(this),
                        WeatherContract.CONTENT_AUTHORITY,
                        extras);
                break;
            case R.id.btn_test_query:
                getSupportLoaderManager().restartLoader(0, null, mLoaderManagerCallback);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (true == mFrgSearch.onKeyDown(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
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

    class MyLoaderManagerCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        private Context mCtx;

        public MyLoaderManagerCallback(Context context) {
            mCtx = context;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri uri = WeatherContract.WeatherNow.buildQueryUri(SettingsUtil.getCurrentCity(mCtx));
            return new CursorLoader(mCtx, uri, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (DEBUG) Log.d(TAG, "onLoadFinished() called with: loader = ["
                    + loader + "], cursor = [" + cursor + "]");

            cursor.moveToFirst();
            Log.v(TAG, "cursor count:" + cursor.getCount());
            int tmp = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.TMP));
            int pm25 = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherNow.PM25));
            Log.v(TAG, String.format("tmp:%d pm2.5:%d", tmp, pm25));
        }

        @Override
        public void onLoaderReset(Loader loader) {
            if (DEBUG) Log.d(TAG, "onLoaderReset() called with: loader = [" + loader + "]");
        }
    }
}
