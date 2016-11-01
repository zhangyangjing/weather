package com.zhangyangjing.weather;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhangyangjing.weather.provider.weather.WeatherContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMain extends AppCompatActivity {
    private static final String TAG = ActivityMain.class.getSimpleName();

    private static final boolean DEBUG = false;
    private static final String KEY_FILTER = "filter";

    @BindView(R.id.recycler_list)
    RecyclerView mRecyclerView;

    private CursorRecyclerAdapter mAdapter;
    private MyLoaderManagerCallback mLoaderManagerCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdapter = new CursorRecyclerAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

        mLoaderManagerCallback = new MyLoaderManagerCallback();
        getSupportLoaderManager().initLoader(0, null, mLoaderManagerCallback);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnClick(R.id.fab)
    public void onClick(View v) {
        FloatingActionButton fab = (FloatingActionButton) v;
        AnimatedVectorDrawable d = (AnimatedVectorDrawable) fab.getDrawable();
        d.reset();
        d.start();
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
            if (DEBUG) Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], cursor = [" + cursor + "]");
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
}
