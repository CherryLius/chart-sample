package cherry.android.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;

/**
 * 图例
 * Created by ROOT on 2017/8/23.
 */

public class Legend {

    private int mTextSize;
    @ColorInt
    private int mTextColor = Color.BLACK;

    private TextPaint mTextPaint;

    private PieChart mPieChart;
    private int mPadding;
    private int mIndicatorSize;
    private String mLabel;

    private Rect mLegendRect;

    public Legend(Context context, PieChart pieChart) {
        this.mPieChart = pieChart;
        mPadding = (int) DensityUtils.dp2px(context, 10);
        mTextSize = (int) DensityUtils.sp2px(context, 12);
        mIndicatorSize = (int) DensityUtils.dp2px(context, 12);
        mLegendRect = new Rect();
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }

    public void setLabel(@Nullable String label) {
        this.mLabel = label;
    }

    public float measureWidth() {
        if (mPieChart.isEmpty())
            return 0;
        float max = 0;
        for (int i = 0; i < mPieChart.getPies().length; i++) {
            String label = mPieChart.getPies()[i].title;
            if (TextUtils.isEmpty(label))
                continue;
            final float textWidth = mTextPaint.measureText(label);
            if (textWidth > max) {
                max = textWidth;
            }
        }
        return max;
    }

    public void renderer(Canvas canvas, int width, int height) {
        if (mPieChart.isEmpty())
            return;
        final float measuredWidth = measureWidth();
        final int legendWidth = (int) (measuredWidth + mPadding * 2 + mIndicatorSize);
        final float textHeight = mTextPaint.descent() + mTextPaint.ascent();
        for (int i = 0; i < mPieChart.getPies().length; i++) {
            PieChart.Pie pie = mPieChart.getPies()[i];
            final int baseX = width - legendWidth;
            final int baseY = mPadding * (i + 1) + mIndicatorSize * i;
            mLegendRect.set(baseX,
                    baseY,
                    baseX + mIndicatorSize,
                    baseY + mIndicatorSize);
            mTextPaint.setColor(pie.color);
            canvas.drawRect(mLegendRect, mTextPaint);
            mTextPaint.setColor(mTextColor);

            final int baseTextX = baseX + mIndicatorSize + mPadding;
            final int baseTextY = (int) (baseY + mIndicatorSize / 2 - textHeight / 2);
            canvas.drawText(pie.title, baseTextX, baseTextY, mTextPaint);
        }

        if (TextUtils.isEmpty(mLabel))
            return;
        final int length = mPieChart.getPies().length;
        final int baseX = width - legendWidth;
        final int pointY = mPadding * (length + 1) + mIndicatorSize * length;
        final int baseY = (int) (pointY - textHeight);
        canvas.drawText(mLabel, baseX, baseY, mTextPaint);

    }

    private float getLabelWidth() {
        if (!TextUtils.isEmpty(mLabel)) {
            return mTextPaint.measureText(mLabel);
        }
        return 0;
    }
}
