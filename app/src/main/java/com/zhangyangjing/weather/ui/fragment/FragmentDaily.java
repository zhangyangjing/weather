package com.zhangyangjing.weather.ui.fragment;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.provider.weather.WeatherContract.WeatherDaily;
import com.zhangyangjing.weather.settings.SettingsUtil;
import com.zhangyangjing.weather.ui.adapter.AdapterDaily;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyangjing on 05/11/2016.
 */

public class FragmentDaily extends Fragment {
    private static final String TAG = FragmentDaily.class.getSimpleName();

    private static final int LOADER_ID = 0;

    private AdapterDaily mAdapter;
    private MyLoaderCallback mLoaderCallback;

    @BindView(R.id.rv_daily) RecyclerView mRvDaily;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new AdapterDaily();
        mLoaderCallback = new MyLoaderCallback();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        Log.d(TAG, "onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getLoaderManager().initLoader(LOADER_ID, null, mLoaderCallback);
        MatrixCursor cursor = new MatrixCursor(new String[]{"low", "high"});
        cursor.addRow(new Integer[]{0, 3});
        cursor.addRow(new Integer[]{3, 6});
        cursor.addRow(new Integer[]{4, 6});
        cursor.addRow(new Integer[]{6, 9});
        cursor.addRow(new Integer[]{5, 9});
        cursor.addRow(new Integer[]{2, 4});
        cursor.addRow(new Integer[]{4, 7});
        cursor.addRow(new Integer[]{9, 12});
        mRvDaily.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRvDaily.setAdapter(mAdapter);
        mAdapter.swapCursor(cursor);
    }

    private class MyLoaderCallback implements LoaderManager.LoaderCallbacks {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getContext(),
                    WeatherDaily.buildObserveUri(SettingsUtil.getCurrentCity(getContext())),
                    null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            mAdapter.swapCursor((Cursor) data);
        }

        @Override
        public void onLoaderReset(Loader loader) {
            mAdapter.swapCursor(null);
        }
    }
}
