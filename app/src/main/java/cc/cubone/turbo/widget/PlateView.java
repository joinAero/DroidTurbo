package cc.cubone.turbo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.util.AttributeSet;

import cc.cubone.turbo.R;

public class PlateView extends BaseView {

    private int mPlateRadius;
    private int mPlateColor;
    private int mPlateBorderColor;
    private int mPlateBorderWidth;
    private int mPlateOutsideColor;

    private int mPlateBorderForeColor;
    private float mPlateBorderForeRatio;
    private float mPlateBorderForeRotation;

    private int mPlateBorderBlankSize;

    private float mPlateOriginX;
    private float mPlateOriginY;
    private float mPlateOriginOffsetX;
    private float mPlateOriginOffsetY;

    private Paint mPaint;
    private Path mPath;
    private RectF mRectF;

    public PlateView(Context context) {
        super(context);
        initPlateView();
    }

    public PlateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPlateView();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PlateView, defStyleAttr, 0);
        mPlateRadius = a.getDimensionPixelSize(R.styleable.PlateView_plateRadius, DEF_VALUE);
        if (mPlateRadius != DEF_VALUE) setPlateRadius(mPlateRadius);
        setPlateColor(a.getColor(R.styleable.PlateView_plateColor, Color.TRANSPARENT));
        setPlateBorderColor(a.getColor(R.styleable.PlateView_plateBorderColor, Color.TRANSPARENT));
        setPlateBorderWidth(a.getDimensionPixelSize(R.styleable.PlateView_plateBorderWidth, (int) dip2px(context, 2)));
        setPlateOutsideColor(a.getColor(R.styleable.PlateView_plateOutsideColor, Color.TRANSPARENT));

        setPlateBorderForeColor(a.getColor(R.styleable.PlateView_plateBorderForeColor, Color.TRANSPARENT));
        setPlateBorderForeRatio(a.getFloat(R.styleable.PlateView_plateBorderForeRatio, 0));
        setPlateBorderForeRotation(a.getFloat(R.styleable.PlateView_plateBorderForeRotation, 0));

        setPlateBorderBlankSize(a.getDimensionPixelSize(R.styleable.PlateView_plateBorderBlankSize, 0));

        setPlateOriginX(a.getDimensionPixelSize(R.styleable.PlateView_plateOriginX, DEF_VALUE));
        setPlateOriginY(a.getDimensionPixelSize(R.styleable.PlateView_plateOriginY, DEF_VALUE));
        setPlateOriginOffsetX(a.getDimensionPixelSize(R.styleable.PlateView_plateOriginOffsetX, 0));
        setPlateOriginOffsetY(a.getDimensionPixelSize(R.styleable.PlateView_plateOriginOffsetY, 0));
        a.recycle();
    }

    private void initPlateView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();
    }

    @IntRange(from=0)
    public int getPlateRadius() {
        return mPlateRadius;
    }

    public void setPlateRadius(@IntRange(from=0) int plateRadius) {
        mPlateRadius = floor(plateRadius, 0);
    }

    @ColorInt
    public int getPlateColor() {
        return mPlateColor;
    }

    public void setPlateColor(@ColorInt int plateColor) {
        mPlateColor = plateColor;
    }

    @ColorInt
    public int getPlateBorderColor() {
        return mPlateBorderColor;
    }

    public void setPlateBorderColor(@ColorInt int plateBorderColor) {
        mPlateBorderColor = plateBorderColor;
    }

    @IntRange(from=0)
    public int getPlateBorderWidth() {
        return mPlateBorderWidth;
    }

    public void setPlateBorderWidth(@IntRange(from=0) int plateBorderWidth) {
        mPlateBorderWidth = floor(plateBorderWidth, 0);
    }

    @ColorInt
    public int getPlateOutsideColor() {
        return mPlateOutsideColor;
    }

    public void setPlateOutsideColor(@ColorInt int plateOutsideColor) {
        mPlateOutsideColor = plateOutsideColor;
    }

    @ColorInt
    public int getPlateBorderForeColor() {
        return mPlateBorderForeColor;
    }

    public void setPlateBorderForeColor(@ColorInt int plateBorderForeColor) {
        mPlateBorderForeColor = plateBorderForeColor;
    }

    public float getPlateBorderForeRotation() {
        return mPlateBorderForeRotation;
    }

    public void setPlateBorderForeRotation(float plateBorderForeRotation) {
        mPlateBorderForeRotation = plateBorderForeRotation;
    }

    @FloatRange(from=0f, to=1f)
    public float getPlateBorderForeRatio() {
        return mPlateBorderForeRatio;
    }

    public void setPlateBorderForeRatio(@FloatRange(from=0f, to=1f) float plateBorderForeRatio) {
        mPlateBorderForeRatio = range(plateBorderForeRatio, 0f, 1f);
    }

    @IntRange(from=0)
    public int getPlateBorderBlankSize() {
        return mPlateBorderBlankSize;
    }

    public void setPlateBorderBlankSize(@IntRange(from=0) int plateBorderBlankSize) {
        mPlateBorderBlankSize = floor(plateBorderBlankSize, 0);
    }

    public float getPlateOriginX() {
        return mPlateOriginX;
    }

    public void setPlateOriginX(float plateOriginX) {
        mPlateOriginX = plateOriginX;
    }

    public float getPlateOriginY() {
        return mPlateOriginY;
    }

    public void setPlateOriginY(float plateOriginY) {
        mPlateOriginY = plateOriginY;
    }

    public float getPlateOriginOffsetX() {
        return mPlateOriginOffsetX;
    }

    public void setPlateOriginOffsetX(float plateOriginOffsetX) {
        mPlateOriginOffsetX = plateOriginOffsetX;
    }

    public float getPlateOriginOffsetY() {
        return mPlateOriginOffsetY;
    }

    public void setPlateOriginOffsetY(float plateOriginOffsetY) {
        mPlateOriginOffsetY = plateOriginOffsetY;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        final int w = c.getWidth();
        final int h = c.getHeight();
        if (mPlateOriginX == DEF_VALUE) mPlateOriginX = w * 0.5f;
        if (mPlateOriginY == DEF_VALUE) mPlateOriginY = h * 0.5f;
        final float cx = mPlateOriginX + mPlateOriginOffsetX;
        final float cy = mPlateOriginY + mPlateOriginOffsetY;

        if (mPlateRadius == DEF_VALUE) {
            mPlateRadius = (Math.min(w, h) - mPlateBorderWidth) / 2;
        }

        float halfBorderWidth = 0f;
        // plate border
        if (drawBorder(c, mPlateBorderColor, cx, cy, 1)) {
            halfBorderWidth = mPlateBorderWidth * 0.5f;
        }
        // plate border fore
        if (drawBorder(c, mPlateBorderForeColor, cx, cy, mPlateBorderForeRatio)) {
            halfBorderWidth = mPlateBorderWidth * 0.5f;
        }

        // plate
        if (mPlateColor != Color.TRANSPARENT) {
            final float r = mPlateRadius - halfBorderWidth;
            if (r > 0) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mPlateColor);
                c.drawCircle(cx, cy, r, mPaint);
            }
        }

        // plate outside
        if (mPlateOutsideColor != Color.TRANSPARENT) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mPlateOutsideColor);
            mPath.reset();
            final float r = mPlateRadius + halfBorderWidth;
            if (r > halfBorderWidth) {
                mPath.addCircle(cx, cy, r, Path.Direction.CW);
                c.clipPath(mPath, Region.Op.DIFFERENCE);
            }
            mPath.addRect(0, 0, w, h, Path.Direction.CW);
            c.drawPath(mPath, mPaint);
        }
    }

    private boolean drawBorder(Canvas c, int color, float cx, float cy, float ratio) {
        if (color == Color.TRANSPARENT) return false;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPlateBorderWidth);
        mPaint.setColor(color);
        final int r = mPlateRadius;
        float theta = (float) getTheta(r, mPlateBorderBlankSize);
        if (theta <= 0 && ratio >= 1) {
            c.drawCircle(cx, cy, r, mPaint);
        } else if (ratio > 0) {
            if (mRectF == null) mRectF = new RectF();
            mRectF.set(cx-r, cy-r, cx+r, cy+r);
            float startAngle = mPlateBorderForeRotation - theta;
            float totalAngle = 360f - 2*theta;
            if (totalAngle > 0) {
                mPath.reset();
                mPath.arcTo(mRectF, startAngle, -(totalAngle * ratio));
                c.drawPath(mPath, mPaint);
            }
        }
        return true;
    }

    private double getTheta(double r, double a) {
        if (a <= 0) {
            return 0;
        } else if (a < 2*r) {
            return Math.toDegrees(Math.asin(a/2 / r));
        } else if (a == 2*r) {
            return 90;
        } else if (a < 4*r) {
            return 180 - Math.toDegrees(Math.acos((a/2 - r) / r));
        } else { // >= 4*r
            return 180;
        }
    }

}
