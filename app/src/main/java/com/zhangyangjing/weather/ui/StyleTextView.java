package com.zhangyangjing.weather.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zhangyangjing.weather.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyangjing on 05/11/2016.
 */

public class StyleTextView extends TextView {
    private static final String TAG = StyleTextView.class.getSimpleName();

    private static Map<TypeFaceEnum, Typeface> sTypeFaces = new HashMap<>();


    public StyleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.TextFontStyle);
        String style = t.getString(R.styleable.TextFontStyle_style);
        t.recycle();

        applyFont(context, style);
    }

    private void applyFont(Context context, String style) {
        if (TextUtils.isEmpty(style))
            return;

        TypeFaceEnum typeFaceEnum = null;
        try {
            typeFaceEnum = TypeFaceEnum.valueOf(style);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        Typeface typeface = sTypeFaces.get(typeFaceEnum);
        if (null == typeface) {
            typeface = Typeface.createFromAsset(context.getAssets(), typeFaceEnum.path);
            sTypeFaces.put(typeFaceEnum, typeface);;
        }
        setTypeface(typeface);
    }


    enum TypeFaceEnum {
        OswaldDei_bold("Oswald-DemiBold.ttf"),
        OswaldMedium("Oswald-Medium.ttf"),
        OswaldLight("Oswald-Light.ttf"),
        OswaldRegular("Oswald-Regular.ttf");

        String path;

        TypeFaceEnum(String path) {
            this.path = path;
        }
    }
}
