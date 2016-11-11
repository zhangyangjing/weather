package com.zhangyangjing.weather.ui.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.provider.weather.WeatherContract.WeatherDaily;
import com.zhangyangjing.weather.ui.widget.LineChartView;
import com.zhangyangjing.weather.ui.widget.StyleTextView;
import com.zhangyangjing.weather.util.CursorUtil;
import com.zhangyangjing.weather.util.WeatherUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyangjing on 11/11/2016.
 */
public class AdapterDaily extends RecyclerView.Adapter<AdapterDaily.MyViewHolder> {

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
        mCursor.moveToPosition(position);
        String date = CursorUtil.getString(mCursor, WeatherDaily.DATE);
        holder.mTvWeek.setText(date2week(date));
        holder.mTvDate.setText(date.split("-")[2] + "/" + date.split("-")[1]);
        holder.mTvIcon.setText(WeatherUtil.cond2icon(
                CursorUtil.getInt(mCursor, WeatherDaily.CONDD)));

        int temp[] = getTemp(position);
        int nextTemp[] = mCursor.getCount() - 1 == position ? temp : getTemp(position + 1);
        int previousTemp[] = 0 == position ? temp : getTemp(position - 1);

        holder.mVvDottedLine.setVisibility(0 == position ? View.GONE : View.VISIBLE);
        holder.mLcChart.setData(
                mMaxTemp,
                mMinTemp,
                new int[]{previousTemp[0], temp[0], nextTemp[0]},
                new int[]{previousTemp[1], temp[1], nextTemp[1]});
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
        return new int[]{
                CursorUtil.getInt(mCursor, WeatherDaily.TMPL),
                CursorUtil.getInt(mCursor, WeatherDaily.TMPH)};
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

    private String date2week(String date) {
        final String[] WEEKS = new String[]{"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        final String TODAY = "TODAY";

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.setTime(dateFormat.parse(date));
            if (today == calendar.get(Calendar.DAY_OF_MONTH))
                return TODAY;
            return WEEKS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
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
