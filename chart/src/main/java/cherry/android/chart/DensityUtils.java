package cherry.android.chart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;

/**
 * 单位转换
 */
/*public */final class DensityUtils {

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static float dp2px(@NonNull Context context, float dpVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static float sp2px(@NonNull Context context, float spVal) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return spVal * fontScale + 0.5f;
    }

    public static float sp2px2(@NonNull Context context, float spVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }

    public static float px2dp(@NonNull Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxVal / scale;
    }

    public static float px2sp(@NonNull Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return pxVal / scale;
    }


}
