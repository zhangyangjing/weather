package com.zhangyangjing.weather;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zhangyangjing.weather.provider.weather.WeatherContract;
import com.zhangyangjing.weather.util.ImeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMain extends AppCompatActivity {
    private static final String TAG = ActivityMain.class.getSimpleName();

    private static final boolean DEBUG = false;
    private static final String KEY_FILTER = "filter";

    private AnimatedVectorDrawable mAnimateAddToBack;
    private AnimatedVectorDrawable mAnimateBackToAdd;

    private int mBtnSearchBackOffsetLeft;
    private int mBtnSearchBackOffsetRight;

    @BindView(R.id.recycler_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.btnSearchback)
    ImageButton mBtnSearchback;

    @BindView(R.id.search_view)
    SearchView mSearchView;

    @BindView(R.id.scrim)
    View mScrim;

    private SearchStatus mSearchStatus;
    private CursorRecyclerAdapter mAdapter;
    private MyLoaderManagerCallback mLoaderManagerCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAdapter = new CursorRecyclerAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

        mLoaderManagerCallback = new MyLoaderManagerCallback();
        getSupportLoaderManager().initLoader(0, null, mLoaderManagerCallback);

        mAnimateAddToBack = (AnimatedVectorDrawable) getDrawable(R.drawable.animate_add_to_back);
        mAnimateBackToAdd = (AnimatedVectorDrawable) getDrawable(R.drawable.animate_back_to_add);

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
        Animator animator = ViewAnimationUtils
                .createCircularReveal(mScrim, mBtnSearchBackOffsetRight, (int) mSearchView.getBottom(), 0.0f, (float) Math.hypot(mBtnSearchBackOffsetRight, mScrim.getHeight() - mSearchView.getBottom()))
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        animator.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in));
        animator.start();

        mBtnSearchback.animate()
                .translationX(mBtnSearchBackOffsetLeft)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in))
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
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSearchView.setVisibility(View.VISIBLE);
                        mSearchView.setAlpha(1.0f);
                        mSearchView.requestFocus();
                    }
                })
                .setStartDelay(100)
                .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime))
                .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in))
                .start();

        mSearchStatus = SearchStatus.SEARCH;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void exitSearchMode() {
        mBtnSearchback.setImageDrawable(mAnimateBackToAdd);
        mAnimateBackToAdd.reset();
        mAnimateBackToAdd.start();

        mBtnSearchback.animate()
                .translationX(mBtnSearchBackOffsetRight)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in))
                .setStartDelay(100)
                .start();

        ImeUtils.hideIme(mSearchView);
        mSearchView.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSearchView.setVisibility(View.GONE);
                        mSearchView.clearFocus();
                    }
                })
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in))
                .start();

        mScrim.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mScrim.setVisibility(View.GONE);
                    }
                })
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in))
                .start();

        mSearchStatus = SearchStatus.NORMAL;
    }

    private void caculateSearchbackCoord() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mBtnSearchback.getLayoutParams();
        int margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        mBtnSearchBackOffsetLeft = margin;
        mBtnSearchBackOffsetRight = screenWidth - margin - mBtnSearchback.getDrawable().getIntrinsicWidth();
    }

    class MyQueryTextListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
//            searchView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
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
            if (DEBUG)
                Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], cursor = [" + cursor + "]");
            mAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader loader) {
            if (DEBUG) Log.d(TAG, "onLoaderReset() called with: loader = [" + loader + "]");
            mAdapter.swapCursor(null);
        }

    }

    class CursorRecyclerAdapter extends
            RecyclerView.Adapter<CursorRecyclerAdapter.CursorViewHolder> {
        private Cursor mCursor;

        public CursorRecyclerAdapter(Cursor cursor) {
            mCursor = cursor;
        }

        public void swapCursor(Cursor cursor) {
            mCursor = cursor;
            notifyItemInserted(2);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (mCursor != null) {
                return mCursor.getCount();
            } else {
                return 0;
            }
        }

        @Override
        public CursorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CursorViewHolder(View.inflate(parent.getContext(),
                    R.layout.item_city, null));
        }

        @Override
        public void onBindViewHolder(CursorViewHolder holder, int i) {
            if (!mCursor.moveToPosition(i)) {
                throw new IllegalStateException("couldn't move cursor to position " + i);
            }

            mCursor.moveToPosition(i);
            String county = mCursor.getString(mCursor.getColumnIndex(WeatherContract.City.COUNTY));
            String city = mCursor.getString(mCursor.getColumnIndex(WeatherContract.City.CITY));
            String province = mCursor.getString(mCursor.getColumnIndex(
                    WeatherContract.City.PROVINCE));
            holder.textView.setText(String.format("%s %s %s", province, city, county));
        }

        public class CursorViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.info_text)
            TextView textView;

            public CursorViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    enum SearchStatus {
        NORMAL, SEARCH
    }
}
