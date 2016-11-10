package com.zhangyangjing.weather.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.zhangyangjing.weather.util.FontUtil;
import com.zhangyangjing.weather.util.FontUtil.TypeFaceEnum;
import com.zhangyangjing.weather.util.Utils;

/**
 * Created by zhangyangjing on 10/11/2016.
 */

public class LineChartView extends View {
    private static final String FORMAT_STRING = "%d°";
    private static final int STROKE_WIDTH = 1;
    private static final int TEXT_MARGIN = 5;
    private static final int TEXT_SIZE = 13;

    private int[] mLowData, mHighData;
    private int mMax, mMin;
    private int mStrokeWidth;
    private int mTextMargin;

    Paint mLinePaint;
    Paint mTextPaint;
    Paint mShadePaint;

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mStrokeWidth = Utils.dp2px(context, STROKE_WIDTH);
        mTextMargin = Utils.dp2px(context, TEXT_MARGIN);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(mStrokeWidth);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(FontUtil.getTypeface(context, TypeFaceEnum.OswaldRegular));
        mTextPaint.setTextSize(Utils.dp2px(context, TEXT_SIZE));

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

        int rangeTop = textHeight + mStrokeWidth + mTextMargin;
        int rangeBottom = getHeight() - textHeight - mStrokeWidth - mTextMargin;

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
        canvas.drawCircle(current.x, current.y, mStrokeWidth, mLinePaint);

        int textY = textUpward ?
                current.y - mStrokeWidth - mTextMargin :
                current.y + mStrokeWidth + mTextMargin + textHeight;
        canvas.drawText(info, current.x - textWidth / 2, textY, mTextPaint);
    }
}
