package cherry.android.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by ROOT on 2017/8/23.
 */

public class PathView extends View {

    private Paint pathPaint;
    private Path path;
    private RectF rectF;

    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.RED);
        pathPaint.setStrokeWidth(5);

        path = new Path();
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
        drawTwoArc(canvas);
    }

    void drawArc(Canvas canvas) {
        final int radius = 160;
        final int centerW = getWidth() / 2;
        final int centerH = getHeight() / 2;
        rectF.left = centerW - radius;
        rectF.top = centerH - radius;
        rectF.right = centerW + radius;
        rectF.bottom = centerH + radius;

        Log.e("Test", " cos(180)=" + Math.cos(180 * Math.PI / 180));
        float startAngle = 40;
        float endAngle = 160;
        float startX = (float) (centerW + radius * Math.cos(startAngle * Math.PI / 180));
        float startY = (float) (centerH + radius * Math.sin(startAngle * Math.PI / 180));

        float endX = (float) (centerW + radius * Math.cos(endAngle * Math.PI / 180));
        float endY = (float) (centerH + radius * Math.sin(endAngle * Math.PI / 180));
        path.moveTo(startX, startY);
        path.arcTo(rectF, startAngle, endAngle);
//        path.lineTo(endX, endY);
        path.lineTo(centerW, centerH);
        path.close();
        canvas.drawPath(path, pathPaint);
    }

    void drawTwoArc(Canvas canvas) {
        final int radius = 160;
        final int centerW = getWidth() / 2;
        final int centerH = getHeight() / 2;
//        path.moveTo(20, 20);
//        path.lineTo(200, 400);
//        path.lineTo(400, 600);
        rectF.left = centerW - radius;
        rectF.top = centerH - radius;
        rectF.right = centerW + radius;
        rectF.bottom = centerH + radius;
//        path.arcTo(rectF, 0, 160);
////        path.close();

        Log.e("Test", " cos(180)=" + Math.cos(180 * Math.PI / 180));
        float startAngle = 240;
        float sweepAngle = 100;
        float startX = (float) (centerW + radius * Math.cos(startAngle * Math.PI / 180));
        float startY = (float) (centerH + radius * Math.sin(startAngle * Math.PI / 180));

        path.moveTo(startX, startY);
        path.arcTo(rectF, startAngle, sweepAngle);

        float endAngle = startAngle + sweepAngle;
        float endX = (float) (centerW + radius * Math.cos(endAngle * Math.PI / 180));
        float endY = (float) (centerH + radius * Math.sin(endAngle * Math.PI / 180));

        float radius2 = 100;
        float startX2 = (float) (centerW + radius2 * Math.cos(startAngle * Math.PI / 180));
        float startY2 = (float) (centerH + radius2 * Math.sin(startAngle * Math.PI / 180));

        float endX2 = (float) (centerW + radius2 * Math.cos(endAngle * Math.PI / 180));
        float endY2 = (float) (centerH + radius2 * Math.sin(endAngle * Math.PI / 180));

        path.lineTo(endX2, endY2);
        rectF.left = centerW - radius2;
        rectF.top = centerH - radius2;
        rectF.right = centerW + radius2;
        rectF.bottom = centerH + radius2;
        path.arcTo(rectF, endAngle, -sweepAngle);
        canvas.drawPath(path, pathPaint);
    }
}
