package cc.cubone.turbo.ui.demo.snake;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import cc.cubone.turbo.BuildConfig;
import cc.cubone.turbo.ui.demo.snake.engine.Painter;
import cc.cubone.turbo.ui.demo.snake.engine.Scene;
import cc.cubone.turbo.ui.demo.snake.engine.Status;
import cc.cubone.turbo.ui.demo.snake.game.GameLayer;
import cc.cubone.turbo.ui.demo.snake.game.TipLayer;

public class SnakeSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {

    private Scene mScene;
    private Status mStatus;

    private TipLayer mTipLayer;

    private boolean mUpdateNeededOnResume = false;

    public SnakeSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public SnakeSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnakeSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SnakeSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        getHolder().addCallback(this);

        mScene = new Scene(this);
        mStatus = mScene.getStatus();
        mStatus.setDebug(BuildConfig.DEBUG);

        Painter painter = mScene.getPainter();

        GameLayer gameLayer = new GameLayer();

        TipLayer tipLayer = new TipLayer();
        tipLayer.setBackgroundColor(0x33ffffff);
        tipLayer.setTextColor(Color.WHITE);
        tipLayer.setTextSize(painter.dp2px(60));

        mTipLayer = tipLayer;
        mScene.addLayers(gameLayer, tipLayer);
    }

    public void start() {
        mScene.start();
        update(false);
    }

    public void resume() {
        mScene.resume();
        update(false);
    }

    public void pause() {
        pause(true);
    }

    private void pause(boolean update) {
        mScene.pause();
        if (update) update(true);
    }

    public void stop() {
        mScene.stop();
        update(true);
    }

    public void onResume() {
        if (mUpdateNeededOnResume) {
            update(true);
        }
    }

    public void onPause() {
        // not redraw immediately to update scene
        pause(false);
        // need update on resume if turn screen off using power button directly
        // because the surface view will not be destroyed
        mUpdateNeededOnResume = true;
    }

    private void update(boolean redraw) {
        updateTipLayer();
        if (redraw) {
            mScene.draw();
        }
    }

    private void updateTipLayer() {
        mTipLayer.setVisible(true);
        if (mStatus.isPausing()) {
            // click to resume
            mTipLayer.setText("Click\nTo\nResume");
        } else if (mStatus.isStopped()) {
            // click to start
            mTipLayer.setText("Click\nTo\nStart");
        } else {
            // running
            mTipLayer.setVisible(false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        boolean handled = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handled = true;
                break;
            case MotionEvent.ACTION_UP:
                onClick();
                handled = true;
                break;
        }
        return handled || super.onTouchEvent(ev);
        //return mScene.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    private void onClick() {
        if (mStatus.isRunning()) {
            pause();
        } else {
            // resume if pausing and start if stopped
            resume();
        }
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // will recreate every resume from background
        update(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mUpdateNeededOnResume = false;
    }

}
