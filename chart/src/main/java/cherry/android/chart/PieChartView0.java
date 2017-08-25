//package cherry.android.chart;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.RectF;
//import android.support.annotation.ColorInt;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.util.TypedValue;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
///**
// * Created by ROOT on 2017/8/23.
// */
//
//public class PieChartView0 extends View {
//
//    private Paint mChartPaint;
//    private PieData[] mPieData;
//    private float mRadius;
//    private float mInnerRadius;
//
//    private Path mPiePath;
//    private RectF mCenterRect;
//    private RectF mPieRect;
//    private RectF mInnerRect;
//    private int mSliceGap;
//
//    private double mTouchDegrees;
//    private float mTouchRadius;
//
//    private GestureDetector mGestureDetector;
//
//    class PieData {
//        float value;
//        String title;
//        @ColorInt
//        int color;
//    }
//
//    public PieChartView0(Context context) {
//        this(context, null);
//    }
//
//    public PieChartView0(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public PieChartView0(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        mGestureDetector = new GestureDetector(context, mGestureListener);
//        mChartPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//        mRadius = 180;
//        mInnerRadius = 0;
//        mSliceGap = 10;
//        mPiePath = new Path();
//        mPieRect = new RectF();
//        mInnerRect = new RectF();
//        mCenterRect = new RectF();
//        random();
//    }
//
//    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
//
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            float eventX = e.getX();
//            float eventY = e.getY();
//            mTouchDegrees = getTouchDegrees(eventX, eventY);
//            postInvalidate();
//            return super.onSingleTapUp(e);
//        }
//    };
//
//    private double getTouchDegrees(float eventX, float eventY) {
//        final float centerX = mCenterRect.centerX();
//        final float centerY = mCenterRect.centerY();
//
//        final float tx = eventX - centerX;
//        final float ty = eventY - centerY;
//        mTouchRadius = (float) Math.sqrt(tx * tx + ty * ty);
//        float radians = (float) Math.atan(ty / tx);
//        double degrees = Math.toDegrees(radians);
//        if (tx >= 0 && ty >= 0) {
//            return degrees;
//        } else if (tx < 0 && ty > 0) {
//            return 180 + degrees;
//        } else if (tx < 0 && ty < 0) {
//            return 180 + degrees;
//        } else {
//            return 360 + degrees;
//        }
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (isEmpty())
//            return;
//        calculate();
////        drawPie(canvas);
//        drawPathPie(canvas);
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mCenterRect.set(0, 0, w, h);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        mGestureDetector.onTouchEvent(event);
//        return true;
//    }
//
//    private void calculate() {
//        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
//        final float centerX = mCenterRect.centerX();
//        final float centerY = mCenterRect.centerY();
//        mPieRect.set(centerX - mRadius, centerY - mRadius, centerX + mRadius, centerY + mRadius);
//        mInnerRect.set(centerX - mInnerRadius, centerY - mInnerRadius, centerX + mInnerRadius, centerY + mInnerRadius);
//    }
//
//    private boolean isEmpty() {
//        return mPieData == null || mPieData.length == 0;
//    }
//
//    private void drawPie(Canvas canvas) {
//        int centerW = getWidth() / 2;
//        int centerH = getHeight() / 2;
//        float total = calculateTotalValue();
//        float startAngle = 0;
//        for (int i = 0; i < mPieData.length; i++) {
//            PieData pie = mPieData[i];
//            float angle = pie.value / total * 360;
//            RectF rectF = new RectF(centerW - mRadius, centerH - mRadius, centerW + mRadius, centerH + mRadius);
//            mChartPaint.setColor(pie.color);
//            canvas.drawArc(rectF, startAngle, angle, true, mChartPaint);
//            startAngle += angle;
//        }
//    }
//
//    private void drawPathPie(Canvas canvas) {
//        final float centerX = mCenterRect.centerX();
//        final float centerY = mCenterRect.centerY();
//
//        float total = calculateTotalValue();
//        float startAngle = 0;
//        float gapAngle = (float) (mSliceGap / (Math.PI / 180.f * mRadius));
//        for (int i = 0; i < mPieData.length; i++) {
//            mPiePath.reset();
//            PieData pie = mPieData[i];
//            mChartPaint.setColor(pie.color);
//            float sliceAngle = pie.value / total * 360;
//            final float actualStartAngle = startAngle + gapAngle / 2.0f;
//            final float actualSweepAngle = sliceAngle - gapAngle / 2.0f;
//            final float startPointX = (float) (centerX + mRadius * Math.cos(actualStartAngle * Math.PI / 180));
//            final float startPointY = (float) (centerY + mRadius * Math.sin(actualStartAngle * Math.PI / 180));
//            mPiePath.moveTo(startPointX, startPointY);
//            if (mTouchDegrees >= startAngle && mTouchDegrees <= (startAngle + sliceAngle)
//                    && mTouchRadius < mRadius && mTouchRadius > 0) {
//                RectF rectF = new RectF();
//                rectF.set(mPieRect);
//                rectF.inset(-20, -20);
//                mPiePath.arcTo(rectF, actualStartAngle, actualSweepAngle);
//            } else {
//                mPiePath.arcTo(mPieRect, actualStartAngle, actualSweepAngle);
//            }
//
//            if (mInnerRadius > 0) {
//                float actualEndAngle = actualStartAngle + actualSweepAngle;
//                float innerEndPointX = (float) (centerX + mInnerRadius * Math.cos(actualEndAngle * Math.PI / 180));
//                float innerEndPointY = (float) (centerY + mInnerRadius * Math.sin(actualEndAngle * Math.PI / 180));
//                mPiePath.lineTo(innerEndPointX, innerEndPointY);
//                mPiePath.arcTo(mInnerRect, actualEndAngle, -actualSweepAngle);
//            } else {
//                float spaceOffset = calculateMinimumRadiusForSpacedSlice(sliceAngle, actualStartAngle, actualSweepAngle);
//                float middleAngle = actualStartAngle + actualSweepAngle / 2;
//                float endPointX = (float) (centerX + spaceOffset * Math.cos(middleAngle * Math.PI / 180));
//                float endPointY = (float) (centerY + spaceOffset * Math.sin(middleAngle * Math.PI / 180));
//                mPiePath.lineTo(endPointX, endPointY);
//            }
//
//            mPiePath.close();
//            canvas.drawPath(mPiePath, mChartPaint);
//            startAngle += sliceAngle;
//        }
//    }
//
//    private float calculateTotalValue() {
//        float ret = 0;
//        for (int i = 0; i < mPieData.length; i++) {
//            ret += mPieData[i].value;
//        }
//        return ret;
//    }
//
//    /**
//     * 扇形间隔位移半径计算
//     * 原扇形沿圆半径方向位移，求出位移后三角形的被半径圆截取后的高
//     * 利用半径-位移后三角形的高-圆弧的高度=位移的半径
//     *
//     * @param sliceAngle
//     * @param startAngle
//     * @param sweepAngle
//     * @return
//     */
//    protected float calculateMinimumRadiusForSpacedSlice(float sliceAngle, float startAngle, float sweepAngle) {
//        final float centerX = mCenterRect.centerX();
//        final float centerY = mCenterRect.centerY();
//
//        final float middleAngle = startAngle + sweepAngle / 2;
//        final float arcEndPointX = (float) (centerX + mRadius * Math.cos((startAngle + sweepAngle) * Math.PI / 180));
//        final float arcEndPointY = (float) (centerY + mRadius * Math.sin((startAngle + sweepAngle) * Math.PI / 180));
//
//        final float arcStartPointX = (float) (centerX + mRadius * Math.cos(startAngle * Math.PI / 180));
//        final float arcStartPointY = (float) (centerY + mRadius * Math.sin(startAngle * Math.PI / 180));
//
//        final float arcMiddlePointX = (float) (centerX + mRadius * Math.cos(middleAngle * Math.PI / 180));
//        final float arcMiddlePointY = (float) (centerY + mRadius * Math.sin(middleAngle * Math.PI / 180));
//
//        // 位移后三角形底边长度
//        double basePointDistance = Math.sqrt(Math.pow(arcEndPointX - arcStartPointX, 2)
//                + Math.pow(arcEndPointY - arcStartPointY, 2));
//
//        // 位移，扇形的角度不变，求三角形高度
//        float angle = (180 - sliceAngle) / 2;
//        float containedTriangleHeight = (float) (basePointDistance / 2 * Math.tan(angle * Math.PI / 180));
//        // 圆半径-三角形高度=位移半径+圆弧高度
//        float spaceRadius = mRadius - containedTriangleHeight;
//        // 减去圆弧高度
//        spaceRadius -= Math.sqrt(Math.pow(arcMiddlePointX - (arcStartPointX + arcEndPointX) / 2, 2)
//                + Math.pow(arcMiddlePointY - (arcStartPointY + arcEndPointY) / 2, 2));
//        return spaceRadius;
//    }
//
//    public void random() {
//        Random random = new Random();
//        List<PieData> list = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            PieData data = new PieData();
//            data.value = (float) random.nextInt(50 + 1);
//            data.color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
//            list.add(data);
//        }
//        mPieData = list.toArray(new PieData[]{});
//        postInvalidate();
//    }
//}
