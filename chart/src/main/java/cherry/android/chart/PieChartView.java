package cherry.android.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ROOT on 2017/8/24.
 */

public class PieChartView extends View {
    private Paint mChartPaint;
    private PieChart mPieChart;
    private Path mPiePath;
    private RectF mShiftRectF;

    private Paint mTextPaint;
    private RectF mTextRectF;

    private GestureDetector mGestureDetector;
    private OnPieSelectedListener mPieSelectedListener;

    private Legend mLegend;

    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, mGestureListener);
        mChartPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mShiftRectF = new RectF();
        mPieChart = new PieChart(context);
        mPiePath = new Path();
        mTextRectF = new RectF();
        mLegend = new Legend(context, mPieChart);
    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mPieChart.isEmpty())
                return super.onSingleTapUp(e);
            mPieChart.handleTouch(e, mPieSelectedListener);
            postInvalidate();
            return super.onSingleTapUp(e);
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTextPaint.setTextSize(mPieChart.getTextSize());
        final float textHeight = mTextPaint.descent() + mTextPaint.ascent();
        final float size = (mPieChart.getRadius() + mPieChart.getSelectionShift() + mPieChart.getFoldLineWith() - 1.5f * textHeight) * 2;
        final int width = (int) measureSpec(widthMeasureSpec, size + getPaddingLeft() + getPaddingRight());
        final int height = (int) measureSpec(heightMeasureSpec, size + getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(width, height);
    }

    private static float measureSpec(int measureSpec, float defVal) {
        float ret = 0;
        final int mode = MeasureSpec.getMode(measureSpec);
        final int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                ret = Math.min(size, defVal);
                break;
            case MeasureSpec.EXACTLY:
                ret = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                ret = defVal;
                break;
        }
        return ret;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPieChart.isEmpty())
            return;
        mPieChart.calculate();
        mLegend.renderer(canvas, getMeasuredWidth(), getMeasuredHeight());
        drawPathPie(canvas);
        if (mPieChart.isShowValues())
            drawValues(canvas);
        drawContent(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPieChart.adjustPiePorts(w, h);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private void drawPathPie(Canvas canvas) {
        final float centerX = mPieChart.getViewRectF().centerX();
        final float centerY = mPieChart.getViewRectF().centerY();

        /* pie */
        final float radius = mPieChart.getRadius();
        final float innerRadius = mPieChart.getInnerRadius();

        final float shift = mPieChart.getSelectionShift();
        mShiftRectF.set(mPieChart.getPieRectF());
        mShiftRectF.inset(-shift, -shift);
        for (int i = 0; i < mPieChart.getPies().length; i++) {
            PieChart.Pie pie = mPieChart.getPies()[i];
            mPiePath.reset();
            mChartPaint.setColor(pie.color);
            float sliceAngle = pie.sliceAngle;
            final float actualStartAngle = pie.actualStartAngle;
            final float actualSweepAngle = pie.actualSweepAngle;
            final float startPointX = (float) (centerX + radius * Math.cos(actualStartAngle * Math.PI / 180));
            final float startPointY = (float) (centerY + radius * Math.sin(actualStartAngle * Math.PI / 180));

            mPiePath.moveTo(startPointX, startPointY);
            if (mPieChart.isPieSelected(pie)) {
                mPiePath.arcTo(mShiftRectF, actualStartAngle, actualSweepAngle);
            } else {
                mPiePath.arcTo(mPieChart.getPieRectF(), actualStartAngle, actualSweepAngle);
            }

            if (innerRadius > 0) {
                float actualEndAngle = actualStartAngle + actualSweepAngle;
                float innerEndPointX = (float) (centerX + innerRadius * Math.cos(actualEndAngle * Math.PI / 180));
                float innerEndPointY = (float) (centerY + innerRadius * Math.sin(actualEndAngle * Math.PI / 180));
                mPiePath.lineTo(innerEndPointX, innerEndPointY);
                mPiePath.arcTo(mPieChart.getInnerRectF(), actualEndAngle, -actualSweepAngle);
            } else {
                float spaceOffset = PieChart.calculateMinimumRadiusForSpacedSlice(radius, sliceAngle,
                        actualStartAngle, actualSweepAngle,
                        centerX, centerY);
                float middleAngle = actualStartAngle + actualSweepAngle / 2;
                float endPointX = (float) (centerX + spaceOffset * Math.cos(middleAngle * Math.PI / 180));
                float endPointY = (float) (centerY + spaceOffset * Math.sin(middleAngle * Math.PI / 180));
                mPiePath.lineTo(endPointX, endPointY);
            }

            mPiePath.close();
            canvas.drawPath(mPiePath, mChartPaint);
        }
    }

    private void drawValues(Canvas canvas) {
        final float centerX = mPieChart.getViewRectF().centerX();
        final float centerY = mPieChart.getViewRectF().centerY();

        final float radius = mPieChart.getRadius();
        final float innerRadius = mPieChart.getInnerRadius();
        final float deltaRadius = (radius - innerRadius) * 0.75f;
        mTextPaint.setTextSize(mPieChart.getTextSize());
        mTextPaint.setColor(mPieChart.getTextColor());
        for (int i = 0; i < mPieChart.getPies().length; i++) {
            PieChart.Pie pie = mPieChart.getPies()[i];
            String value = pie.percent;
            if (TextUtils.isEmpty(value))
                continue;
            // 文字宽度高度
            final float textWidth = mTextPaint.measureText(value);
            final float textHeight = mTextPaint.descent() + mTextPaint.ascent();

            final float actualStartAngle = pie.actualStartAngle;
            final float actualSweepAngle = pie.actualSweepAngle;
            float middleAngle = actualStartAngle + actualSweepAngle / 2;

            float pointX = (float) (centerX + deltaRadius * Math.cos(Math.toRadians(middleAngle)));
            float pointY = (float) (centerY + deltaRadius * Math.sin(Math.toRadians(middleAngle)));

            float baseX = pointX - textWidth / 2;
            float baseY = pointY - textHeight / 2;

            mTextPaint.setAlpha(126);
            float padding = 5;
            mTextRectF.set(pointX - textWidth / 2 - padding,
                    pointY - Math.abs(textHeight) / 2 - padding,
                    pointX + textWidth / 2 + padding,
                    pointY + Math.abs(textHeight) / 2 + padding);
            canvas.drawRect(mTextRectF, mTextPaint);
            mTextPaint.setAlpha(255);
            canvas.drawText(value, baseX, baseY, mTextPaint);
        }

    }

    private void drawContent(Canvas canvas) {
        final float centerX = mPieChart.getViewRectF().centerX();
        final float centerY = mPieChart.getViewRectF().centerY();

        mTextPaint.setTextSize(mPieChart.getTextSize());
        mTextPaint.setColor(mPieChart.getTextColor());
        final float textHeight = mTextPaint.descent() + mTextPaint.ascent();
        float radius = mPieChart.getRadius();
        for (int i = 0; i < mPieChart.getPies().length; i++) {
            PieChart.Pie pie = mPieChart.getPies()[i];
            String label = pie.title;
            String percent = pie.percent;
            if (TextUtils.isEmpty(label) || !mPieChart.isPieSelected(pie))
                continue;
            // 文字宽度高度
            final float textLabelWidth = mTextPaint.measureText(label);
            final float textValueWidth = mTextPaint.measureText(percent);

            final float actualStartAngle = pie.actualStartAngle;
            final float actualSweepAngle = pie.actualSweepAngle;
            float middleAngle = actualStartAngle + actualSweepAngle / 2;

            if (mPieChart.isPieSelected(pie)) {
                mShiftRectF.centerX();
                float val = Math.min(mShiftRectF.width(), mShiftRectF.height());
                radius = val / 2;
            }

            float pointStartX = (float) (centerX + radius * Math.cos(Math.toRadians(middleAngle)));
            float pointStartY = (float) (centerY + radius * Math.sin(Math.toRadians(middleAngle)));

            final float foldLine = mPieChart.getFoldLineWith();
            float pointEndX = (float) (centerX + (radius + foldLine) * Math.cos(Math.toRadians(middleAngle)));
            float pointEndY = (float) (centerY + (radius + foldLine) * Math.sin(Math.toRadians(middleAngle)));

            float width = Math.max(textLabelWidth, textValueWidth);

            mChartPaint.setColor(Color.BLACK);
            mTextPaint.setColor(Color.BLACK);
            canvas.drawLine(pointStartX, pointStartY, pointEndX, pointEndY, mChartPaint);
            if (middleAngle <= 90 || middleAngle >= 270) {
                float baseX = pointEndX + width / 2 - textLabelWidth / 2;
                float baseY = pointEndY + textHeight / 2;
                canvas.drawText(label, baseX, baseY, mTextPaint);
                float baseX2 = pointEndX + width / 2 - textValueWidth / 2;
                float baseY2 = pointEndY - textHeight * 3 / 2;
                canvas.drawText(percent, baseX2, baseY2, mTextPaint);
                canvas.drawLine(pointEndX, pointEndY, pointEndX + width, pointEndY, mChartPaint);
            } else {
                float baseX = pointEndX - width / 2 - textLabelWidth / 2;
                float baseY = pointEndY + textHeight / 2;
                canvas.drawText(label, baseX, baseY, mTextPaint);
                float baseX2 = pointEndX - width / 2 - textValueWidth / 2;
                float baseY2 = pointEndY - textHeight * 3 / 2;
                canvas.drawText(percent, baseX2, baseY2, mTextPaint);
                canvas.drawLine(pointEndX, pointEndY, pointEndX - width, pointEndY, mChartPaint);
            }
        }
    }

    public void setOnPieSelectedListener(OnPieSelectedListener listener) {
        this.mPieSelectedListener = listener;
    }

    public interface OnPieSelectedListener {
        void onPieSelected(int position, float value, String title);
    }

    public PieChart getPieChart() {
        return this.mPieChart;
    }

    public Legend getLegend() {
        return this.mLegend;
    }


    public void random() {
        Random random = new Random();
        List<PieChart.Pie> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PieChart.Pie data = new PieChart.Pie((float) random.nextInt(50 + 1),
                    Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)), "" + i);
            data.value = (float) random.nextInt(50 + 1);
            data.color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
            list.add(data);
        }
        mPieChart.setPies(list);
        postInvalidate();
    }
}
