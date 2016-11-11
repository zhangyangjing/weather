package com.zhangyangjing.weather.ui.fragment;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhangyangjing.weather.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyangjing on 05/11/2016.
 */

public class FragmentHourly extends Fragment {
    private static final String TAG = FragmentHourly.class.getSimpleName();

    private static final int OADER_ID = 0;

    @BindView(R.id.rv_hourly) RecyclerView mRvHourly;

    private MyAdapter mAdapter;
    private MyLoaderCallback mLoaderCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyAdapter();
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
//        getLoaderManager().initLoader(LOADER_ID, null, mLoaderCallback);

        MatrixCursor cursor = new MatrixCursor(new String[]{"temp", "wind"}); // TODO: JUST FOR TEST
        cursor.addRow(new Integer[]{0, 3});
        cursor.addRow(new Integer[]{3, 6});
        cursor.addRow(new Integer[]{4, 6});
        cursor.addRow(new Integer[]{6, 9});
        cursor.addRow(new Integer[]{5, 9});
        cursor.addRow(new Integer[]{2, 4});
        cursor.addRow(new Integer[]{4, 7});
        cursor.addRow(new Integer[]{9, 12});
        mAdapter.swapCursor(cursor);
    }

    class MyLoaderCallback implements LoaderManager.LoaderCallbacks {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            return null;
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {

        }

        @Override
        public void onLoaderReset(Loader loader) {
            mAdapter.swapCursor(null);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private Cursor mCursor;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =  View.inflate(parent.getContext(), R.layout.item_hourly, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return null == mCursor ? 0 : mCursor.getCount();
        }

        public void swapCursor(Cursor cursor) {
            mCursor = cursor;
            notifyDataSetChanged();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
