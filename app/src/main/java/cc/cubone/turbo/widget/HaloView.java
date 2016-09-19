package cc.cubone.turbo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.util.AttributeSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import cc.cubone.turbo.R;

/**
 * The halo view。
 */
public class HaloView extends BaseView {

    //private static final String TAG = HaloView.class.getSimpleName();

    /** Halo color */
    private int mHaloColor;
    /** Halo alpha begin, [0, 255] */
    private int mHaloAlphaBeg;
    /** Halo alpha end, [0, 255] */
    private int mHaloAlphaEnd;
    /** Halo radius begin, px */
    private int mHaloRadiusBeg;
    /** Halo radius end, px */
    private int mHaloRadiusEnd;
    /** Halo duration, ms */
    private int mHaloDuration;
    /** Halo max amount */
    private int mHaloMaxAmount;
    /** Halo animated or not */
    private boolean mHaloAnimated;

    /** Halo origin x, px */
    private float mHaloOriginX;
    /** Halo origin y, px */
    private float mHaloOriginY;
    /** Halo origin offset x, px */
    private float mHaloOriginOffsetX;
    /** Halo origin offset y, px */
    private float mHaloOriginOffsetY;

    private Paint mPaint;

    /** Halo interval to emit, ms */
    private int mHaloInterval;
    private Deque<Halo> mHaloQueue = new LinkedList<>();

    public HaloView(Context context) {
        super(context);
        initHaloView();
    }

    public HaloView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HaloView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHaloView();

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.HaloView, defStyleAttr, 0);
        setHaloColor(a.getColor(R.styleable.HaloView_haloColor, Color.WHITE));
        setHaloAlphaBeg(a.getInteger(R.styleable.HaloView_haloAlphaBeg, 77));
        setHaloAlphaEnd(a.getInteger(R.styleable.HaloView_haloAlphaEnd, 0));
        setHaloRadiusBeg(a.getDimensionPixelSize(R.styleable.HaloView_haloRadiusBeg, 0));
        mHaloRadiusEnd = a.getDimensionPixelSize(R.styleable.HaloView_haloRadiusEnd, DEF_VALUE);
        if (mHaloRadiusEnd != DEF_VALUE) setHaloRadiusEnd(mHaloRadiusEnd);
        setHaloDuration(a.getInteger(R.styleable.HaloView_haloDuration, 9));
        setHaloMaxAmount(a.getInteger(R.styleable.HaloView_haloMaxAmount, 3));
        mHaloAnimated = a.getBoolean(R.styleable.HaloView_haloAnimated, true);

        setHaloOriginX(a.getDimensionPixelSize(R.styleable.HaloView_haloOriginX, DEF_VALUE));
        setHaloOriginY(a.getDimensionPixelSize(R.styleable.HaloView_haloOriginY, DEF_VALUE));
        setHaloOriginOffsetX(a.getDimensionPixelSize(R.styleable.HaloView_haloOriginOffsetX, 0));
        setHaloOriginOffsetY(a.getDimensionPixelSize(R.styleable.HaloView_haloOriginOffsetY, 0));
        a.recycle();

        mHaloInterval = mHaloDuration / mHaloMaxAmount;
        mHaloQueue.clear();
    }

    private void initHaloView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @ColorInt
    public int getHaloColor() {
        return mHaloColor;
    }

    public void setHaloColor(@ColorInt int haloColor) {
        mHaloColor = haloColor;
    }

    @IntRange(from=0,to=255)
    public int getHaloAlphaBeg() {
        return mHaloAlphaBeg;
    }

    public void setHaloAlphaBeg(@IntRange(from=0,to=255) int haloAlphaBeg) {
        mHaloAlphaBeg = range(haloAlphaBeg, 0, 255);
    }

    @IntRange(from=0,to=255)
    public int getHaloAlphaEnd() {
        return mHaloAlphaEnd;
    }

    public void setHaloAlphaEnd(@IntRange(from=0,to=255) int haloAlphaEnd) {
        mHaloAlphaEnd = range(haloAlphaEnd, 0, 255);
    }

    @IntRange(from=0)
    public int getHaloRadiusBeg() {
        return mHaloRadiusBeg;
    }

    public void setHaloRadiusBeg(@IntRange(from=0) int haloRadiusBeg) {
        mHaloRadiusBeg = floor(haloRadiusBeg, 0);
    }

    @IntRange(from=0)
    public int getHaloRadiusEnd() {
        return mHaloRadiusEnd;
    }

    public void setHaloRadiusEnd(@IntRange(from=0) int haloRadiusEnd) {
        mHaloRadiusEnd = floor(haloRadiusEnd, 0);
    }

    @IntRange(from=0)
    public int getHaloDuration() {
        return mHaloDuration / 1000;
    }

    public void setHaloDuration(@IntRange(from=0) int haloDuration) {
        mHaloDuration = floor(haloDuration * 1000, 0);
    }

    @IntRange(from=1)
    public int getHaloMaxAmount() {
        return mHaloMaxAmount;
    }

    public void setHaloMaxAmount(@IntRange(from=1) int haloMaxAmount) {
        mHaloMaxAmount = floor(haloMaxAmount, 1);
    }

    public float getHaloOriginX() {
        return mHaloOriginX;
    }

    public void setHaloOriginX(float haloOriginX) {
        mHaloOriginX = haloOriginX;
    }

    public float getHaloOriginY() {
        return mHaloOriginY;
    }

    public void setHaloOriginY(float haloOriginY) {
        mHaloOriginY = haloOriginY;
    }

    public float getHaloOriginOffsetX() {
        return mHaloOriginOffsetX;
    }

    public void setHaloOriginOffsetX(float haloOriginOffsetX) {
        mHaloOriginOffsetX = haloOriginOffsetX;
    }

    public float getHaloOriginOffsetY() {
        return mHaloOriginOffsetY;
    }

    public void setHaloOriginOffsetY(float haloOriginOffsetY) {
        mHaloOriginOffsetY = haloOriginOffsetY;
    }

    public boolean isAnimated() {
        return mHaloAnimated;
    }

    public void startAnim() {
        mHaloAnimated = true;
        mHaloInterval = mHaloDuration / mHaloMaxAmount;
        mHaloQueue.clear();
        doInvalidate();
    }

    public void stopAnim() {
        mHaloAnimated = false;
        mHaloQueue.clear();
        doInvalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        if (mHaloDuration == 0 || !mHaloAnimated) {
            return;
        }

        final int w = c.getWidth();
        final int h = c.getHeight();
        if (mHaloOriginX == DEF_VALUE) mHaloOriginX = w * 0.5f;
        if (mHaloOriginY == DEF_VALUE) mHaloOriginY = h * 0.5f;
        final float cx = mHaloOriginX + mHaloOriginOffsetX;
        final float cy = mHaloOriginY + mHaloOriginOffsetY;

        if (mHaloRadiusEnd == DEF_VALUE) {
            List<Double> distances = Arrays.asList(
                    Math.hypot(cx, cy),
                    Math.hypot(cx, cy-h),
                    Math.hypot(cx-w, cy),
                    Math.hypot(cx-w, cy-h)
            );
            mHaloRadiusEnd = Collections.max(distances).intValue();
        }

        final long now = System.currentTimeMillis();
        if (mHaloQueue.size() == 0) {
            mHaloQueue.add(new Halo(now));
        }

        Halo mHaloFirst = mHaloQueue.getLast();
        Halo mHaloLast = mHaloQueue.getFirst();

        if (now - mHaloFirst.spawnTime >= mHaloInterval) {
            if (now - mHaloLast.spawnTime > mHaloDuration) {
                mHaloFirst = mHaloQueue.removeFirst();
                mHaloFirst.reset(now);
                mHaloQueue.add(mHaloFirst);
            } else {
                mHaloQueue.add(new Halo(now));
            }
        }

        mPaint.setColor(mHaloColor);
        for (Halo halo : mHaloQueue) {
            long elapsed = now - halo.spawnTime;
            if (elapsed > mHaloDuration) {
                // dead halo
                continue;
            }
            float ratio = 1f * elapsed / mHaloDuration;
            mPaint.setAlpha(getValue(mHaloAlphaBeg, mHaloAlphaEnd, ratio));
            c.drawCircle(cx, cy, getValue(mHaloRadiusBeg, mHaloRadiusEnd, ratio), mPaint);
        }

        invalidate();
    }

    private static class Halo {

        public long spawnTime;

        public Halo() {
            reset();
        }

        public Halo(long spawnTime) {
            reset(spawnTime);
        }

        public void reset() {
            reset(System.currentTimeMillis());
        }

        public void reset(long spawnTime) {
            this.spawnTime = spawnTime;
        }
    }

}
