package cc.eevee.turbo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import cc.eevee.turbo.R;

public class CircleView extends BaseView {

    private int mCircleColor;
    /**
     * @see Color#parseColor(String)
     */
    private int[] mCircleColorGradient;
    private int mCircleRadius;
    private int mCircleWidth;
    private float mCircleRatio;
    private float mCircleRotation;
    private boolean mCircleCounter;
    private boolean mCircleCapRound;
    private boolean mCircleAnimated;
    private int mCircleAnimDuration;

    private Paint mPaint;
    private Path mPath;
    private RectF mRectF;

    private boolean mShaderReady = false;
    private Animator mAnim;
    private float mAnimRotation;

    public CircleView(Context context) {
        super(context);
        initCircleView();
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCircleView();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleView, defStyleAttr, 0);
        setCircleColor(a.getColor(R.styleable.CircleView_circleColor, Color.WHITE));
        String gradientString = a.getString(R.styleable.CircleView_circleColorGradient);
        if (gradientString != null) setCircleColorGradient(parseColors(gradientString));
        mCircleRadius = a.getDimensionPixelSize(R.styleable.CircleView_circleRadius, DEF_VALUE);
        if (mCircleRadius != DEF_VALUE) setCircleRadius(mCircleRadius);
        setCircleWidth(a.getDimensionPixelSize(R.styleable.CircleView_circleWidth, (int) dip2px(context, 6)));
        setCircleRatio(a.getFloat(R.styleable.CircleView_circleRatio, 0.25f));
        setCircleRotation(a.getFloat(R.styleable.CircleView_circleRotation, 0));
        setCircleCounter(a.getBoolean(R.styleable.CircleView_circleCounter, false));
        setCircleCapRound(a.getBoolean(R.styleable.CircleView_circleCapRound, true));
        setCircleAnimated(a.getBoolean(R.styleable.CircleView_circleAnimated, true));
        setCircleAnimDuration(a.getInteger(R.styleable.CircleView_circleAnimDuration, 1000));
        a.recycle();
    }

    private void initCircleView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath = new Path();
        mRectF = new RectF();
    }

    private int[] parseColors(@NonNull String s) {
        final String[] parts = s.split(",");
        final int len = parts.length;
        if (len <= 0) return null;
        int[] colors = new int[len];
        for (int i = 0; i < len; ++i) {
            String part = parts[i].trim();
            if (part.isEmpty()) {
                colors[i] = Color.TRANSPARENT;
            } else {
                try {
                    colors[i] = Color.parseColor(parts[i]);
                } catch (IllegalArgumentException unknownColor) {
                    try {
                        colors[i] = Integer.parseInt(parts[i]);
                    } catch (NumberFormatException e) {
                        throw unknownColor;
                    }
                }
            }
        }
        return colors;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mCircleAnimated) startAnim();
    }

    @ColorInt
    public int getCircleColor() {
        return mCircleColor;
    }

    public void setCircleColor(@ColorInt int circleColor) {
        mCircleColor = circleColor;
    }

    public int[] getCircleColorGradient() {
        return mCircleColorGradient;
    }

    public void setCircleColorGradient(@Size(min=2) int[] circleColorGradient) {
        mCircleColorGradient = circleColorGradient;
        mShaderReady = false;
    }

    @IntRange(from=0)
    public int getCircleRadius() {
        return mCircleRadius;
    }

    public void setCircleRadius(@IntRange(from=0) int circleRadius) {
        mCircleRadius = floor(circleRadius, 0);
    }

    @IntRange(from=0)
    public int getCircleWidth() {
        return mCircleWidth;
    }

    public void setCircleWidth(@IntRange(from=0) int circleWidth) {
        mCircleWidth = floor(circleWidth, 0);
    }

    @FloatRange(from=0,to=1)
    public float getCircleRatio() {
        return mCircleRatio;
    }

    public void setCircleRatio(@FloatRange(from=0,to=1) float circleRatio) {
        float ratio = range(circleRatio, 0f, 1f);
        if (mCircleRatio != ratio) {
            mShaderReady = false;
        }
        mCircleRatio = ratio;
    }

    public float getCircleRotation() {
        return mCircleRotation;
    }

    public void setCircleRotation(float circleRotation) {
        mCircleRotation = circleRotation;
    }

    public boolean isCircleCounter() {
        return mCircleCounter;
    }

    public void setCircleCounter(boolean circleCounter) {
        mCircleCounter = circleCounter;
    }

    public boolean isCircleCapRound() {
        return mCircleCapRound;
    }

    public void setCircleCapRound(boolean circleCapRound) {
        mCircleCapRound = circleCapRound;
    }

    public boolean isCircleAnimated() {
        return mCircleAnimated;
    }

    public void setCircleAnimated(boolean circleAnimated) {
        mCircleAnimated = circleAnimated;
    }

    @IntRange(from=0)
    public int getCircleAnimDuration() {
        return mCircleAnimDuration;
    }

    public void setCircleAnimDuration(@IntRange(from=0) int circleAnimDuration) {
        mCircleAnimDuration = floor(circleAnimDuration, 0);
    }

    public boolean isAnimated() {
        return mAnim != null;
    }

    public void startAnim() {
        startAnim(mCircleAnimDuration);
    }

    public void startAnim(long duration) {
        stopAnim();
        ValueAnimator anim = ValueAnimator.ofFloat(mAnimRotation,
                mAnimRotation + (isCircleCounter() ? -360 : 360));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimRotation = (float) animation.getAnimatedValue();
                setRotation(mAnimRotation);
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnim = null;
            }
        });
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setDuration(duration);
        mAnim = anim;
        mAnim.start();
    }

    public void stopAnim() {
        if (mAnim != null) {
            mAnim.cancel();
            mAnim = null;
        }
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        final int w = c.getWidth();
        final int h = c.getHeight();
        final float cx = w * 0.5f;
        final float cy = h * 0.5f;
        if (mCircleRadius == DEF_VALUE) {
            mCircleRadius = (Math.min(w, h) - mCircleWidth) / 2;
        }
        final float r = mCircleRadius;
        if (r > 0 && mCircleWidth > 0) {
            if (shouldSetShader()) {
                @SuppressLint("DrawAllocation")
                Shader shader = new SweepGradient(cx, cy, mCircleColorGradient, getPositions());
                mPaint.setShader(shader);
                mPaint.setColor(Color.WHITE);
                mShaderReady = true;
            } else {
                mPaint.setColor(mCircleColor);
            }
            mPaint.setStrokeWidth(mCircleWidth);
            float capOffsetDegrees = 0;
            if (mCircleCapRound) {
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                capOffsetDegrees = (float) Math.toDegrees(Math.asin(0.5f*mCircleWidth/mCircleRadius));
            } else {
                mPaint.setStrokeCap(Paint.Cap.BUTT);
                mPaint.setStrokeJoin(Paint.Join.MITER);
            }
            mPath.reset();
            mRectF.set(cx-r, cy-r, cx+r, cy+r);
            mPath.addArc(mRectF, capOffsetDegrees, 360*mCircleRatio-capOffsetDegrees);

            //c.save();
            c.rotate(-90, cx, cy);
            if (!mCircleCounter) {
                c.scale(1, -1, cx, cy);
            }
            c.rotate(-mCircleRotation, cx, cy);
            c.drawPath(mPath, mPaint);
            //c.restore();
        }
    }

    private boolean shouldSetShader() {
        return !mShaderReady && mCircleColorGradient != null && mCircleColorGradient.length > 1;
    }

    private float[] getPositions() {
        int n = mCircleColorGradient.length;
        float[] result = new float[n];
        float a = mCircleRatio / (n - 1);
        for (int i = 0; i < n; ++i) {
            result[i] = i * a;
        }
        return result;
    }

}
