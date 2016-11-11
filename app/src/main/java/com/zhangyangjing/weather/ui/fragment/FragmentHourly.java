package com.zhangyangjing.weather.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.provider.weather.WeatherContract.WeatherHourly;
import com.zhangyangjing.weather.settings.SettingsUtil;
import com.zhangyangjing.weather.ui.adapter.AdapterHourly;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyangjing on 05/11/2016.
 */

public class FragmentHourly extends Fragment {
    private static final String TAG = FragmentHourly.class.getSimpleName();

    private static final String LOADER_PARAM_CITY = "city";
    private static final int LOADER_ID = 0;

    @BindView(R.id.rv_hourly) RecyclerView mRvHourly;

    private AdapterHourly mAdapter;
    private MyLoaderCallback mLoaderCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new AdapterHourly(this);
        mLoaderCallback = new MyLoaderCallback();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hourly, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvHourly.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRvHourly.setAdapter(mAdapter);

        Bundle bundle = new Bundle();
        bundle.putString(LOADER_PARAM_CITY, SettingsUtil.getCurrentCity(getContext()));
        getLoaderManager().initLoader(LOADER_ID, bundle, mLoaderCallback);
    }

    class MyLoaderCallback implements LoaderManager.LoaderCallbacks {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            String city = args.getString(LOADER_PARAM_CITY);
            return new CursorLoader(
                    getContext(),
                    WeatherHourly.buildQueryUri(city),
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
