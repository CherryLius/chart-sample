package cherry.android.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by ROOT on 2017/8/24.
 */

public class PieChart {
    private RectF mViewRectF;
    private RectF mPieRectF;
    private RectF mInnerRectF;

    private float mRadius;
    private float mInnerRadius;

    private float mSliceGap;
    private float mSelectionShift;

    private float mFoldLineWith;

    private boolean mShowValues;

    private Pie[] mPies;

    private int mTextSize;
    @ColorInt
    private int mTextColor;

    public PieChart(@NonNull Context context) {
        mRadius = DensityUtils.dp2px(context, 90);
        mInnerRadius = DensityUtils.dp2px(context, 0);
        mSliceGap = DensityUtils.dp2px(context, 10);
        mSelectionShift = DensityUtils.dp2px(context, 5);
        mTextSize = (int) DensityUtils.sp2px(context, 12);
        mFoldLineWith = DensityUtils.dp2px(context, 15);
        mTextColor = Color.WHITE;

        mViewRectF = new RectF();
        mPieRectF = new RectF();
        mInnerRectF = new RectF();
    }

    public void adjustPiePorts(int width, int height) {
        mViewRectF.set(0, 0, width, height);

        final float centerX = mViewRectF.centerX();
        final float centerY = mViewRectF.centerY();
        mPieRectF.set(centerX - mRadius, centerY - mRadius, centerX + mRadius, centerY + mRadius);
        mInnerRectF.set(centerX - mInnerRadius, centerY - mInnerRadius, centerX + mInnerRadius, centerY + mInnerRadius);
    }

    public RectF getViewRectF() {
        return this.mViewRectF;
    }

    public RectF getPieRectF() {
        return this.mPieRectF;
    }

    public RectF getInnerRectF() {
        return this.mInnerRectF;
    }

    public void calculate() {
        if (isEmpty())
            return;

        final float sliceGap = getSliceGap();
        final float radius = getRadius();

        float gapAngle = (float) (sliceGap / (Math.PI / 180.f * radius));
        // calculate angle;
        float total = calculateTotalValue();
        float startAngle = 0;
        for (int i = 0; i < mPies.length; i++) {
            Pie pie = mPies[i];
            pie.sliceAngle = pie.value / total * 360;
            pie.percent = NumberFormat.getPercentInstance().format(pie.value / total);

            final float actualStartAngle = startAngle + gapAngle / 2.0f;
            final float actualSweepAngle = pie.sliceAngle - gapAngle / 2.0f;
            pie.actualStartAngle = actualStartAngle;
            pie.actualSweepAngle = actualSweepAngle;
            startAngle += pie.sliceAngle;
        }

    }

    public boolean isEmpty() {
        return mPies == null || mPies.length == 0;
    }

    public float getRadius() {
        return this.mRadius;
    }

    public float getInnerRadius() {
        return this.mInnerRadius;
    }

    public float getSelectionShift() {
        return this.mSelectionShift;
    }

    public float getSliceGap() {
        return this.mSliceGap;
    }

    public float getFoldLineWith() {
        return this.mFoldLineWith;
    }

    public void showValues(boolean show) {
        this.mShowValues = show;
    }

    public boolean isShowValues() {
        return this.mShowValues;
    }

    public Pie[] getPies() {
        return this.mPies;
    }

    public void setPies(Pie[] pies) {
        this.mPies = pies;
    }

    public void setPies(List<Pie> pieList) {
        if (pieList == null) {
            this.mPies = null;
        } else {
            this.mPies = pieList.toArray(new Pie[]{});
        }
    }

    public int getTextSize() {
        return this.mTextSize;
    }

    public int getTextColor() {
        return this.mTextColor;
    }

    private float mTouchRadius;
    private double mTouchDegrees;

    protected void handleTouch(MotionEvent event, PieChartView.OnPieSelectedListener listener) {
        final float centerX = getViewRectF().centerX();
        final float centerY = getViewRectF().centerY();
        float eventX = event.getX();
        float eventY = event.getY();

        final float tx = eventX - centerX;
        final float ty = eventY - centerY;

        mTouchRadius = (float) Math.sqrt(tx * tx + ty * ty);
        mTouchDegrees = calTouchDegrees(tx, ty);


        if (listener != null) {
            for (int i = 0; i < getPies().length; i++) {
                PieChart.Pie pie = getPies()[i];
                if (isPieSelected(pie)) {
                    listener.onPieSelected(i, pie.value, pie.title);
                    break;
                }
            }
        }
    }

    protected boolean isPieSelected(@NonNull Pie pie) {
        return (mTouchDegrees >= pie.actualStartAngle
                && mTouchDegrees <= (pie.actualStartAngle + pie.actualSweepAngle))
                && (mTouchRadius < getRadius() && mTouchRadius > 0);
    }

    private double calTouchDegrees(float tx, float ty) {
        float radians = (float) Math.atan(ty / tx);
        double degrees = Math.toDegrees(radians);
        if (tx >= 0 && ty >= 0) {
            return degrees;
        } else if (tx < 0 && ty > 0) {
            return 180 + degrees;
        } else if (tx < 0 && ty < 0) {
            return 180 + degrees;
        } else {
            return 360 + degrees;
        }
    }

    private float calculateTotalValue() {
        float ret = 0;
        for (int i = 0; i < mPies.length; i++) {
            ret += mPies[i].value;
        }
        return ret;
    }

    /**
     * 扇形间隔位移半径计算
     * 原扇形沿圆半径方向位移，求出位移后三角形的被半径圆截取后的高
     * 利用半径-位移后三角形的高-圆弧的高度=位移的半径
     *
     * @param radius
     * @param sliceAngle
     * @param startAngle
     * @param sweepAngle
     * @param centerX
     * @param centerY
     * @return
     */
    public static float calculateMinimumRadiusForSpacedSlice(final float radius,
                                                             final float sliceAngle,
                                                             final float startAngle,
                                                             final float sweepAngle,
                                                             final float centerX,
                                                             final float centerY) {
        final float middleAngle = startAngle + sweepAngle / 2;
        final float arcEndPointX = (float) (centerX + radius * Math.cos((startAngle + sweepAngle) * Math.PI / 180));
        final float arcEndPointY = (float) (centerY + radius * Math.sin((startAngle + sweepAngle) * Math.PI / 180));

        final float arcStartPointX = (float) (centerX + radius * Math.cos(startAngle * Math.PI / 180));
        final float arcStartPointY = (float) (centerY + radius * Math.sin(startAngle * Math.PI / 180));

        final float arcMiddlePointX = (float) (centerX + radius * Math.cos(middleAngle * Math.PI / 180));
        final float arcMiddlePointY = (float) (centerY + radius * Math.sin(middleAngle * Math.PI / 180));

        // 位移后三角形底边长度
        double basePointDistance = Math.sqrt(Math.pow(arcEndPointX - arcStartPointX, 2)
                + Math.pow(arcEndPointY - arcStartPointY, 2));

        // 位移，扇形的角度不变，求三角形高度
        float angle = (180 - sliceAngle) / 2;
        float containedTriangleHeight = (float) (basePointDistance / 2 * Math.tan(angle * Math.PI / 180));
        // 圆半径-三角形高度=位移半径+圆弧高度
        float spaceRadius = radius - containedTriangleHeight;
        // 减去圆弧高度
        spaceRadius -= Math.sqrt(Math.pow(arcMiddlePointX - (arcStartPointX + arcEndPointX) / 2, 2)
                + Math.pow(arcMiddlePointY - (arcStartPointY + arcEndPointY) / 2, 2));
        return spaceRadius;
    }

    public static class Pie {
        protected float value;
        @ColorInt
        protected int color;
        protected String title;

        protected String percent;
        protected float sliceAngle;
        protected float actualStartAngle;
        protected float actualSweepAngle;

        public Pie(float value, int color, String title) {
            this.value = value;
            this.color = color;
            this.title = title;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
