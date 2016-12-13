package cc.eevee.turbo.widget;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public abstract class BaseView extends View {

    public static final int DEF_VALUE = Integer.MIN_VALUE;

    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void doInvalidate() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public static float dip2px(Context c, float dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, c.getResources().getDisplayMetrics());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable> T ceil(T value, T max) {
        if (value.compareTo(max) > 0) {
            return max;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable> T floor(T value, T min) {
        if (value.compareTo(min) < 0) {
            return min;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable> T range(T value, T min, T max) {
        if (value.compareTo(min) < 0) {
            return min;
        } else if (value.compareTo(max) > 0) {
            return max;
        } else {
            return value;
        }
    }

    public static int getValue(int beg, int end, @FloatRange(from=0f,to=1f) float ratio) {
        return (int) (beg + (end - beg) * ratio);
    }

    public static float getValue(float beg, float end, @FloatRange(from=0f,to=1f) float ratio) {
        return beg + (end - beg) * ratio;
    }

    public static double getValue(double beg, double end, @FloatRange(from=0f,to=1f) double ratio) {
        return beg + (end - beg) * ratio;
    }

}
