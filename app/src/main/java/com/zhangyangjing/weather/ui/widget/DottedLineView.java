package com.zhangyangjing.weather.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by zhangyangjing on 10/11/2016.
 */

public class DottedLineView extends View {
    private Paint mPaint;
    private Path mPath;

    public DottedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.d(TAG, "DottedLineView() called with: context = [" + context + "], attrs = [" + attrs + "]");

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            mPaint.setColor(getResources().getColor(android.R.color.darker_gray, null));
        else
            mPaint.setColor(getResources().getColor(android.R.color.darker_gray));
        mPaint.setStrokeWidth(20);

        mPath = new Path();
        mPaint.setPathEffect(new DashPathEffect(new float[]{15, 6}, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw() called with: canvas = [" + canvas + "]");
        mPath.reset();
        mPath.moveTo(2, 0);
        mPath.lineTo(2, getHeight());
        canvas.drawPath(mPath, mPaint);
    }
}
