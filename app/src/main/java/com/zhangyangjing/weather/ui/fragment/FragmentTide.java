package com.zhangyangjing.weather.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.settings.SettingsUtil;
import com.zhangyangjing.weather.util.DbUtil;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.value;
import static com.zhangyangjing.weather.provider.weather.WeatherContract.Tide;

/**
 * Created by zhangyangjing on 05/11/2016.
 */

public class FragmentTide extends Fragment implements OnChartValueSelectedListener {
    private static final String TAG = FragmentTide.class.getSimpleName();

    private static final String LOADER_PARAM_CITY = "city";
    private static final int LOADER_ID = 0;

    @BindView(R.id.chart) LineChart mChart;

    private MyLoaderCallback mLoaderCallback;
    private DateTimeFormatter mFormatter = DateTimeFormat.forPattern("MM/dd HH:mm");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoaderCallback = new MyLoaderCallback();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tide, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putString(LOADER_PARAM_CITY, SettingsUtil.getCurrentCity(getContext()));
        getLoaderManager().initLoader(LOADER_ID, bundle, mLoaderCallback);
        initChart();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        mChart.centerViewToAnimated(e.getX(), e.getY(),
                mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                        .getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() {

    }

    private void initChart() {
        ArrayList<Entry> values = new ArrayList<>();
        LineDataSet set1 = new LineDataSet(values, "Tide");
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(2f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setFormLineWidth(100f);
        set1.setDrawValues(false);
        set1.disableDashedLine();
        set1.setHighlightEnabled(true);
        set1.disableDashedHighlightLine();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);

        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(mChart);
        mChart.setMarker(mv);
        mChart.setData(data);
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(false);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setOnChartValueSelectedListener(this);
        mChart.getAxisLeft().setEnabled(false);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getLegend().setEnabled(false);
        mChart.getXAxis().setLabelCount(2);
        mChart.getXAxis().disableGridDashedLine();
        mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return getTimeDesc((long) value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
    }

    private String getTimeDesc(long time) {
        DateTime now = DateTime.now();
        DateTime today = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
        Interval todayInterval = new Interval(today, today.withFieldAdded(DurationFieldType.days(), 1));
        Interval yesterdayInterval = new Interval(today.withFieldAdded(DurationFieldType.days(), -1), today);
        Interval tomorrowInterval = new Interval(today.withFieldAdded(DurationFieldType.days(), 1), today.withFieldAdded(DurationFieldType.days(), 2));

        if (todayInterval.contains(time)) {
            return "Tod " + mFormatter.print(time).split(" ")[1];
        } else if (yesterdayInterval.contains(time)) {
            return "Yes " + mFormatter.print(time).split(" ")[1];
        } else if (tomorrowInterval.contains(time)) {
            return "Tom " + mFormatter.print(time).split(" ")[1];
        } else {
            return mFormatter.print((long) value);
        }
    }

    class MyLoaderCallback implements LoaderManager.LoaderCallbacks {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            String city = args.getString(LOADER_PARAM_CITY);
            DateTime today = DateTime.now();
            today = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 0, 0);
            long start = today.withFieldAdded(DurationFieldType.days(), -1).toDate().getTime();
            long stop = today.withFieldAdded(DurationFieldType.days(), 3).toDate().getTime();
            return new CursorLoader(
                    getContext(),
                    Tide.buildQueryUri(city),
                    new String[]{Tide.DATE, Tide.HEIGHT},
                    String.format("%s=? and %s >= ? and %s <= ?", Tide._ID, Tide.DATE, Tide.DATE),
                    new String[]{city, String.valueOf(start), String.valueOf(stop)},
                    null);
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            Cursor cursor = (Cursor) data;

            ArrayList<Entry> values = new ArrayList<>();
            cursor.moveToFirst();
            cursor.moveToPrevious();
            while (cursor.moveToNext()) {
                values.add(new Entry(DbUtil.getLong(cursor, Tide.DATE), DbUtil.getInt(cursor, Tide.HEIGHT)));
            }

            LineDataSet set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    }

    class MyMarkerView extends MarkerView {
        private TextView tvContent;

        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            if (e instanceof CandleEntry) {
                CandleEntry ce = (CandleEntry) e;
                tvContent.setText(getTimeDesc((long) ce.getX()) + "  " + Utils.formatNumber(ce.getHigh(), 0, true) + "cm");
            } else {
                tvContent.setText(getTimeDesc((long) e.getX()) + "  " + Utils.formatNumber(e.getY(), 0, true) + "cm");
            }
            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }
    }
}
