package cc.cubone.turbo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.util.AttributeSet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cc.cubone.turbo.R;

public class ArrowView extends BaseView {

    public static final int DIRECTION_UP = 0;
    public static final int DIRECTION_DOWN = 1;
    public static final int DIRECTION_LEFT = 2;
    public static final int DIRECTION_RIGHT = 3;

    @IntDef({DIRECTION_UP, DIRECTION_DOWN, DIRECTION_LEFT, DIRECTION_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {}

    private int mArrowColor;
    private int mArrowDirection;
    private float mArrowRotation;

    private Paint mPaint;
    private Path mPath;

    public ArrowView(Context context) {
        super(context);
        initArrowView();
    }

    public ArrowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initArrowView();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArrowView, defStyleAttr, 0);
        setArrowColor(a.getColor(R.styleable.ArrowView_arrowColor, Color.WHITE));
        //noinspection WrongConstant
        setArrowDirection(a.getInteger(R.styleable.ArrowView_arrowDirection, DIRECTION_UP));
        setArrowRotation(a.getFloat(R.styleable.ArrowView_arrowRotation, 0));
        a.recycle();
    }

    private void initArrowView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
    }

    @ColorInt
    public int getArrowColor() {
        return mArrowColor;
    }

    public void setArrowColor(@ColorInt int arrowColor) {
        mArrowColor = arrowColor;
    }

    @Direction
    public int getArrowDirection() {
        return mArrowDirection;
    }

    public void setArrowDirection(@Direction int arrowDirection) {
        mArrowDirection = arrowDirection;
    }

    public float getArrowRotation() {
        return mArrowRotation;
    }

    public void setArrowRotation(float arrowRotation) {
        mArrowRotation = arrowRotation;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        final int w = c.getWidth();
        final int h = c.getHeight();
        final float cx = w * 0.5f;
        final float cy = h * 0.5f;

        mPaint.setColor(mArrowColor);
        if (mArrowRotation % 360 != 0) {
            c.rotate(mArrowRotation, cx, cy);
        }
        mPath.reset();
        switch (mArrowDirection) {
            case DIRECTION_UP:
                mPath.moveTo(0, h);
                mPath.lineTo(w, h);
                mPath.lineTo(cx, 0);
                break;
            case DIRECTION_DOWN:
                mPath.moveTo(0, 0);
                mPath.lineTo(w, 0);
                mPath.lineTo(cx, h);
                break;
            case DIRECTION_LEFT:
                mPath.moveTo(w, 0);
                mPath.lineTo(w, h);
                mPath.lineTo(0, cy);
                break;
            case DIRECTION_RIGHT:
                mPath.moveTo(0, 0);
                mPath.lineTo(0, h);
                mPath.lineTo(w, cy);
                break;
        }
        mPath.close();
        c.drawPath(mPath, mPaint);
    }
}
