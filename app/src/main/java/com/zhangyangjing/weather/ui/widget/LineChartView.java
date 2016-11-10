package com.zhangyangjing.weather.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhangyangjing on 10/11/2016.
 */

public class LineChartView extends View {
    private static final String FORMAT_STRING = "%d°";
    private static final int STROKE_WIDTH = 3;

    private int[] mLowData, mHighData;
    private int mMax, mMin;

    Paint mLinePaint;
    Paint mTextPaint;
    Paint mShadePaint;

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(STROKE_WIDTH);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                "Oswald-Regular.ttf")); // TODO: use util
        mTextPaint.setTextSize(40);

        mShadePaint = new Paint();
    }

    public void setData(int max, int min, int[] lowData, int[] highData) {
        mMax = max;
        mMin = min;
        mLowData = lowData;
        mHighData = highData;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mLinePaint.setColor(0xFFFE872F);
        mShadePaint.setShader(new LinearGradient(
                0, 0, 0, getHeight(),
                new int[]{0xFFFEE6CE, 0x00FFFFFF},
                null, Shader.TileMode.REPEAT));
        drawChart(canvas, mHighData, true);

        mLinePaint.setColor(0xFF2EB0E9);
        mShadePaint.setShader(new LinearGradient(
                0, 0, 0, getHeight(),
                new int[]{0xFFABDBEF, 0x00FFFFFF},
                null, Shader.TileMode.CLAMP));
        drawChart(canvas, mLowData, false);
    }

    private void drawChart(Canvas canvas, int[] data, boolean textUpward) {
        Rect rect = new Rect();
        String info = String.format(FORMAT_STRING, data[1]);
        mTextPaint.getTextBounds(info, 0, info.length(), rect);
        int textWidth = rect.right - rect.left;
        int textHeight = rect.bottom - rect.top;

        int rangeTop = textHeight + STROKE_WIDTH * 2;
        int rangeBottom = getHeight() - textHeight - STROKE_WIDTH * 2;

        int verticalRange = rangeBottom - rangeTop;
        float rate = verticalRange / (mMax - mMin);

        Point previous = new Point(-getWidth() / 2, (int) (rangeBottom - (data[0] - mMin) * rate));
        Point current = new Point(getWidth() / 2, (int) (rangeBottom - (data[1] - mMin) * rate));
        Point next = new Point(getWidth() * 3 / 2, (int) (rangeBottom - (data[2] - mMin) * rate));

        Path path = new Path();
        path.moveTo(previous.x, previous.y);
        path.cubicTo(0, previous.y, 0, current.y, current.x, current.y);
        path.cubicTo(getWidth(), current.y, getWidth(), next.y, next.x, next.y);

        Path shadePath = new Path();
        shadePath.addPath(path);
        shadePath.lineTo(getWidth(), getHeight());
        shadePath.lineTo(0, getHeight());

        canvas.drawPath(shadePath, mShadePaint);
        canvas.drawPath(path, mLinePaint);
        canvas.drawCircle(current.x, current.y, STROKE_WIDTH, mLinePaint);

        int textY = textUpward ?
                current.y - STROKE_WIDTH * 2 :
                current.y + STROKE_WIDTH * 2 + textHeight;
        canvas.drawText(info, current.x - textWidth / 2, textY, mTextPaint);
    }
}
