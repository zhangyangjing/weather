package com.zhangyangjing.weather.ui.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.provider.weather.WeatherContract.WeatherDaily;
import com.zhangyangjing.weather.ui.widget.LineChartView;
import com.zhangyangjing.weather.ui.widget.StyleTextView;
import com.zhangyangjing.weather.util.CursorUtil;

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

    private static String WEATHER_ICONS_UNKNOW = "\uf07b";
    private static SparseArray<String> WEATHER_ICONS = new SparseArray();
    static {
        WEATHER_ICONS.put(100, "\uf00d");
        WEATHER_ICONS.put(101, "\uf013");
        WEATHER_ICONS.put(102, "\uf041");
        WEATHER_ICONS.put(103, "\uf002");
        WEATHER_ICONS.put(104, "\uf013");

        WEATHER_ICONS.put(200, "\uf021");
        WEATHER_ICONS.put(201, "\uf021");
        WEATHER_ICONS.put(202, "\uf021");
        WEATHER_ICONS.put(203, "\uf021");
        WEATHER_ICONS.put(204, "\uf021");
        WEATHER_ICONS.put(205, "\uf050");
        WEATHER_ICONS.put(206, "\uf050");
        WEATHER_ICONS.put(207, "\uf0cc");
        WEATHER_ICONS.put(208, "\uf0cd");
        WEATHER_ICONS.put(209, "\uf0cd");
        WEATHER_ICONS.put(210, "\uf073");
        WEATHER_ICONS.put(211, "\uf073");
        WEATHER_ICONS.put(212, "\uf056");
        WEATHER_ICONS.put(213, "\uf050");

        WEATHER_ICONS.put(300, "\uf009");
        WEATHER_ICONS.put(301, "\uf008");
        WEATHER_ICONS.put(302, "\uf00e");
        WEATHER_ICONS.put(303, "\uf008");
        WEATHER_ICONS.put(304, "\uf068");
        WEATHER_ICONS.put(305, "\uf017");
        WEATHER_ICONS.put(306, "\uf015");
        WEATHER_ICONS.put(307, "\uf019");
        WEATHER_ICONS.put(308, "\uf018");
        WEATHER_ICONS.put(309, "\uf01c");
        WEATHER_ICONS.put(310, "\uf01d");
        WEATHER_ICONS.put(311, "\uf01e");
        WEATHER_ICONS.put(312, "\uf01e");
        WEATHER_ICONS.put(313, "\uf019");

        WEATHER_ICONS.put(400, "\uf00a");
        WEATHER_ICONS.put(401, "\uf01b");
        WEATHER_ICONS.put(402, "\uf064");
        WEATHER_ICONS.put(403, "\uf06b");
        WEATHER_ICONS.put(404, "\uf017");
        WEATHER_ICONS.put(405, "\uf017");
        WEATHER_ICONS.put(406, "\uf017");
        WEATHER_ICONS.put(407, "\uf017");


        WEATHER_ICONS.put(500, "\uf001");
        WEATHER_ICONS.put(501, "\uf003");
        WEATHER_ICONS.put(502, "\uf0b6");
        WEATHER_ICONS.put(503, "\uf063");
        WEATHER_ICONS.put(504, "\uf063");
        WEATHER_ICONS.put(507, "\uf082");
        WEATHER_ICONS.put(508, "\uf082");

        WEATHER_ICONS.put(900, "\uf072");
        WEATHER_ICONS.put(901, "\uf076");
    }

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
        holder.mTvIcon.setText(cond2icon(CursorUtil.getInt(mCursor, WeatherDaily.CONDD)));

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

    private String cond2icon(int condCode) {
        String icon = WEATHER_ICONS.get(condCode);
        return null == icon ? WEATHER_ICONS_UNKNOW : icon;
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
