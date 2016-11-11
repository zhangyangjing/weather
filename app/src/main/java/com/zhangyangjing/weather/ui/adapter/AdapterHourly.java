package com.zhangyangjing.weather.ui.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.ui.fragment.FragmentHourly;

import butterknife.ButterKnife;

/**
 * Created by zhangyangjing on 11/11/2016.
 */
public class AdapterHourly extends RecyclerView.Adapter<AdapterHourly.MyViewHolder> {
    private FragmentHourly fragmentHourly;
    private Cursor mCursor;

    public AdapterHourly(FragmentHourly fragmentHourly) {
        this.fragmentHourly = fragmentHourly;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_hourly, null);
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

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
