package com.zhangyangjing.weather.ui.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.provider.weather.WeatherContract;
import com.zhangyangjing.weather.ui.fragment.FragmentHourly;
import com.zhangyangjing.weather.ui.widget.StyleTextView;
import com.zhangyangjing.weather.util.CursorUtil;

import butterknife.BindView;
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
        mCursor.moveToPosition(position);

        String date = CursorUtil.getString(mCursor, WeatherContract.WeatherHourly.DATE);
        int temp = CursorUtil.getInt(mCursor, WeatherContract.WeatherHourly.TMP);
        String wdir = CursorUtil.getString(mCursor, WeatherContract.WeatherHourly.WDIR);
        int wspd = CursorUtil.getInt(mCursor, WeatherContract.WeatherHourly.WSPD);

        holder.tvTime.setText(date.split(" ")[1]);
        holder.tvTemp.setText(temp + "°");
        holder.tvWindDir.setText(wdir);
        holder.tvWindSpd.setText(generateSpannableString(wspd, "m/s"));
    }

    @Override
    public int getItemCount() {
        return null == mCursor ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    private Spannable generateWindInfo(String direct, int speed) {
        return new SpannableStringBuilder(direct).append(generateSpannableString(speed, "m/s"));
    }

    private Spannable generateSpannableString(int value, String SizeStr) {
        Spannable span = new SpannableString(value + SizeStr);
        span.setSpan(
                new RelativeSizeSpan(0.5f),
                span.length() - SizeStr.length(),
                span.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_time) StyleTextView tvTime;
//        @BindView(R.id.tv_icon) StyleTextView tvIcon;
        @BindView(R.id.tv_temp) StyleTextView tvTemp;
        @BindView(R.id.tv_wind_dir) StyleTextView tvWindDir;
        @BindView(R.id.tv_wind_spd) StyleTextView tvWindSpd;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
