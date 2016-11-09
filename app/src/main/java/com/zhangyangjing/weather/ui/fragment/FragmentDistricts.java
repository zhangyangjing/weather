package com.zhangyangjing.weather.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.zhangyangjing.weather.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentDistricts extends Fragment implements View.OnLayoutChangeListener {
    private static final String TAG = FragmentDistricts.class.getSimpleName();

    @BindView(R.id.sp_districts) Spinner mSpDistricts;

    private FragmentDistricsListener mListener;

    // TODO: 使用自定义Spinner，自动缩放宽度。右侧放更新时间，collapse时可自动隐藏

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_districts, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Map<String, String>> data = new ArrayList<>();
        data.add(getDistrictItemData("烟台"));
        data.add(getDistrictItemData("济宁"));
        data.add(getDistrictItemData("北京"));
        mSpDistricts.setAdapter(new SimpleAdapter(getContext(), data, android.R.layout.simple_list_item_1, new String[]{"name"}, new int[]{android.R.id.text1}));

        view.addOnLayoutChangeListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FragmentDistricsListener) {
            mListener = (FragmentDistricsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DistrctPositionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private Map<String, String> getDistrictItemData(String name) {
        Map<String, String> itemData = new HashMap<>();
        itemData.put("name", name);
        return itemData;
    }


    public interface FragmentDistricsListener {
        void onDistrictPositionChange(int left, int top, int right, int bottom);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        mListener.onDistrictPositionChange(left, top, right, bottom);
    }
}
