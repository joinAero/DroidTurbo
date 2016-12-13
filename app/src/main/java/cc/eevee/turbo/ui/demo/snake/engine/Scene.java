package cc.eevee.turbo.ui.demo.snake.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;

import cc.eevee.turbo.ui.demo.snake.engine.feature.Drawable;
import cc.eevee.turbo.ui.demo.snake.engine.feature.Touchable;
import cc.eevee.turbo.ui.demo.snake.engine.util.FPS;
import cc.eevee.turbo.ui.demo.snake.engine.view.Layer;
import cc.eevee.turbo.util.HumanReadable;

public class Scene extends LifeCircle implements Drawable, Touchable {

    private Context mContext;
    private SurfaceHolder mHolder;
    private DrawThread mDrawThread;

    private final Painter mPainter;

    private ArrayList<Layer> mLayers;
    private Toast mToast;

    private FPS mFPS = new FPS();

    private boolean mInfoVisible = false;
    private int mInfoGravity = Gravity.START | Gravity.BOTTOM;

    private boolean mChanged = false;

    public Scene(SurfaceView surfaceView) {
        mContext = surfaceView.getContext();
        mHolder = surfaceView.getHolder();
        mPainter = new Painter(mContext);
        mLayers = new ArrayList<>();
        mToast = new Toast(this, Toast.SHORT);
        mToast.setTextColor(Color.GREEN);
    }

    public Context getContext() {
        return mContext;
    }

    public Painter getPainter() {
        return mPainter;
    }

    public void addLayer(Layer layer) {
        if (layer == null) return;
        mLayers.add(layer);
    }

    public void addLayers(Layer... layers) {
        if (layers == null) return;
        mLayers.addAll(Arrays.asList(layers));
    }

    public void toast(String text) {
        mToast.setText(text);
        mToast.show();
    }

    public void setInfoVisible(boolean visible) {
        mInfoVisible = visible;
    }

    public void setInfoGravity(int gravity) {
        mInfoGravity = gravity;
    }

    public boolean isChanged() {
        return mChanged;
    }

    public void setChanged(boolean changed) {
        mChanged = changed;
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onResume() {
        if (mDrawThread != null) return;
        mFPS.reset();
        mDrawThread = new DrawThread();
        mDrawThread.start();
    }

    @Override
    protected void onPause() {
        if (mDrawThread == null) return;
        mDrawThread.cancel();
        mDrawThread = null;
    }

    @Override
    protected void onStop() {
    }

    public void draw() {
        if (mHolder.getSurface().isValid()) {
            Canvas canvas = mHolder.lockCanvas();
            draw(canvas);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        update();
        mFPS.update();
        onDraw(canvas);
        mChanged = false;
    }

    @SuppressLint("DefaultLocale")
    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.WHITE);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // java.util.stream: https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
        for (Layer layer : mLayers) {
            if (layer.isVisible()) {
                mPainter.reset();
                layer.draw(canvas);
            }
        }

        // draw toast
        mPainter.resetPencil();
        mToast.draw(canvas);

        if (mInfoVisible) {
            // draw fps and time
            mPainter.resetPencil();
            String info = String.format("FPS: %.1f\nTime: %s", mFPS.get(),
                    HumanReadable.seconds(mStatus.timeElapsed / 1000));
            mPainter.drawText(canvas, info, mInfoGravity, true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Layer layer;
        for (int i = mLayers.size() - 1; i >= 0; --i) {
            layer = mLayers.get(i);
            if (layer.isTouchable()) {
                if (layer.onTouchEvent(e)) {
                    return true;
                }
            }
        }
        return false;
    }

    private final class DrawThread extends Thread {

        private volatile Thread mBlinker;

        public DrawThread() {
        }

        @Override
        public synchronized void start() {
            mBlinker = this;
            super.start();
        }

        public void cancel() {
            mBlinker = null;
        }

        @Override
        public void run() {
            Thread thisThread = Thread.currentThread();
            while (mBlinker == thisThread) {
                draw();
            }
        }
    }
}
