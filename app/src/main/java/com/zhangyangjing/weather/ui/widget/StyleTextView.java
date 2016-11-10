package com.zhangyangjing.weather.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.util.FontUtil;
import com.zhangyangjing.weather.util.FontUtil.TypeFaceEnum;

/**
 * Created by zhangyangjing on 05/11/2016.
 */

public class StyleTextView extends TextView {
    private static final String TAG = StyleTextView.class.getSimpleName();

    public StyleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.TextFontStyle);
        String style = t.getString(R.styleable.TextFontStyle_style);
        t.recycle();

        applyFont(style);
    }

    private void applyFont(String style) {
        if (TextUtils.isEmpty(style))
            return;

        TypeFaceEnum typeFaceEnum;
        try {
            typeFaceEnum = TypeFaceEnum.valueOf(style);
        } catch (IllegalArgumentException e) {
            return;
        }

        Typeface typeface = FontUtil.getTypeface(getContext(), typeFaceEnum);
        setTypeface(typeface);
    }
}
