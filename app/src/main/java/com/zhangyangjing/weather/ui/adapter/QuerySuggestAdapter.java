package com.zhangyangjing.weather.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhangyangjing.weather.provider.weather.WeatherContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyangjing on 04/11/2016.
 */
public class QuerySuggestAdapter extends CursorAdapter {
    public QuerySuggestAdapter(Context context) {
        super(context, null, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, android.R.layout.simple_list_item_2, null);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String district = cursor.getString(cursor.getColumnIndex(WeatherContract.City.DISTRICT));
        String city = cursor.getString(cursor.getColumnIndex(WeatherContract.City.CITY));
        String province = cursor.getString(cursor.getColumnIndex(WeatherContract.City.PROVINCE));

        ViewHolder vh = (ViewHolder) view.getTag();
        vh.tvDistrict.setText(district);
        vh.tvCity.setText(String.format("%s %s", province, city));
    }

    class ViewHolder {
        @BindView(android.R.id.text1)
        TextView tvDistrict;
        @BindView(android.R.id.text2)
        TextView tvCity;

        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
