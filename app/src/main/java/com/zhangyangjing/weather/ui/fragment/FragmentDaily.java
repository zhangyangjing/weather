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
import com.zhangyangjing.weather.ui.widget.LineChartView;
import com.zhangyangjing.weather.ui.widget.StyleTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyangjing on 05/11/2016.
 */

public class FragmentDaily extends Fragment {
    private static final String TAG = FragmentDaily.class.getSimpleName();

    private static final int LOADER_ID = 0;

    private MyAdapter mAdapter;
    private MyLoaderCallback mLoaderCallback;

    @BindView(R.id.rv_daily) RecyclerView mRvDaily;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyAdapter();
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

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private Cursor mCursor;
        private int mMaxTemp;
        private int mMinTemp;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_daily, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            int temp[] = getTemp(position);
            int nextTemp[] = mCursor.getCount() - 1 == position ? temp : getTemp(position + 1);
            int previousTemp[] = 0 == position ? temp : getTemp(position - 1);

            holder.mVvDottedLine.setVisibility(0 == position ? View.GONE : View.VISIBLE);
            holder.mLcChart.setData(
                    mMaxTemp,
                    mMinTemp,
                    new int[] {previousTemp[0], temp[0], nextTemp[0]},
                    new int[] {previousTemp[1], temp[1], nextTemp[1]});
        }

        @Override
        public int getItemCount() {
            return null == mCursor ? 0 : mCursor.getCount();
        }

        public void swapCursor(Cursor cursor) {
            mCursor = cursor;
            updateLimit();
            notifyDataSetChanged();
        }

        private int[] getTemp(int index) {
            mCursor.moveToPosition(index);
            return new int[] {
                    mCursor.getInt(mCursor.getColumnIndex("low")),
                    mCursor.getInt(mCursor.getColumnIndex("high"))};
        }

        private void updateLimit() {
            mMinTemp = Integer.MAX_VALUE;
            mMaxTemp = Integer.MIN_VALUE;

            for (int i = 0; i < mCursor.getCount(); i++) {
                int[] temp = getTemp(i);
                if (temp[0] < mMinTemp)
                    mMinTemp = temp[0];
                if (temp[1] > mMaxTemp)
                    mMaxTemp = temp[1];
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_week) StyleTextView mTvWeek;
        @BindView(R.id.tv_date) StyleTextView mTvDate;
        @BindView(R.id.tv_icon) StyleTextView mTvIcon;
        @BindView(R.id.dotted_line) View mVvDottedLine;
        @BindView(R.id.lc_chart) LineChartView mLcChart;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
