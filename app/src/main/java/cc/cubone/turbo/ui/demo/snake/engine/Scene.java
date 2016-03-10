package cc.cubone.turbo.ui.demo.snake.engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;

import cc.cubone.turbo.util.TimeUtils;

public class Scene {

    private SurfaceHolder mHolder;
    private DrawThread mDrawThread;

    private final Painter mPainter;
    private final Status mStatus;

    private ArrayList<Layer> mLayers;

    private long mTimeElapsedBase;

    private FPS mFPS = new FPS();

    public Scene(SurfaceView surfaceView) {
        mHolder = surfaceView.getHolder();
        mPainter = new Painter(surfaceView.getContext());
        mStatus = new Status();
        mLayers = new ArrayList<>();
    }

    public Painter getPainter() {
        return mPainter;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void addLayers(Layer... layers) {
        if (layers == null) return;
        mLayers.addAll(Arrays.asList(layers));
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public synchronized void start() {
        if (mStatus.started) return;
        mStatus.started = true;
        mStatus.pausing = false;
        mStatus.timeStart = System.currentTimeMillis();
        startDrawThread();
    }

    public synchronized void resume() {
        if (mStatus.pausing) {
            // resume if pausing
            mStatus.pausing = false;
            startDrawThread();
        } else if (!mStatus.started) {
            // start if not running
            start();
        }
    }

    public synchronized void pause() {
        if (mStatus.isRunning()) {
            mStatus.pausing = true;
            stopDrawThread();
        }
    }

    public synchronized void stop() {
        if (!mStatus.started) return;
        mStatus.started = false;
        mStatus.pausing = false;
        stopDrawThread();
    }

    public void draw() {
        if (mHolder.getSurface().isValid()) {
            Canvas canvas = mHolder.lockCanvas();
            draw(canvas);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void draw(Canvas canvas) {
        //canvas.drawColor(Color.WHITE);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // java.util.stream: https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
        for (Layer layer : mLayers) {
            if (layer.isVisible()) {
                //mPainter.reset();
                layer.draw(canvas, mPainter, mStatus);
            }
        }

        drawDebugInfo(canvas);
    }

    private void drawDebugInfo(Canvas canvas) {
        if (!mStatus.debug) return;

        String info = String.format("Time: %s, FPS: %.1f",
                TimeUtils.readableSeconds(mStatus.timeElapsed / 1000), mFPS.get());

        mPainter.reset();
        TextPaint pencil = mPainter.pencil;
        Paint.FontMetrics fontMetrics = pencil.getFontMetrics();

        /*Rect rect = mPainter.rect;
        pencil.getTextBounds(info, 0, info.length(), rect);
        float x = canvas.getWidth() - rect.width();*/
        float x = 0;
        float y = 0 - fontMetrics.top;

        pencil.setColor(Color.GREEN);
        canvas.drawText(info, x, y, pencil);
    }

    private void onDraw(Canvas canvas, long elapsed) {
        mStatus.timeElapsed = mTimeElapsedBase + elapsed;
        mFPS.update();
        draw(canvas);
    }

    private void startDrawThread() {
        if (mDrawThread != null) return;
        mTimeElapsedBase = mStatus.timeElapsed;
        mFPS.reset();
        mDrawThread = new DrawThread();
        mDrawThread.start();
    }

    private void stopDrawThread() {
        try {
            mDrawThread.cancel();
            mDrawThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mDrawThread = null;
    }

    private final class DrawThread extends Thread {

        private volatile Thread mBlinker;

        private long mTimeStart;

        public DrawThread() {
        }

        @Override
        public synchronized void start() {
            mBlinker = this;
            mTimeStart = System.currentTimeMillis();
            super.start();
        }

        public void cancel() {
            mBlinker = null;
        }

        @Override
        public void run() {
            Thread thisThread = Thread.currentThread();
            while (mBlinker == thisThread) {
                if (!mHolder.getSurface().isValid()) {
                    continue;
                }
                Canvas canvas = mHolder.lockCanvas();
                onDraw(canvas, System.currentTimeMillis() - mTimeStart);
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

}
