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
import cc.cubone.turbo.ui.demo.snake.engine.LifeCircle;
import cc.cubone.turbo.ui.demo.snake.engine.Scene;
import cc.cubone.turbo.ui.demo.snake.engine.Status;
import cc.cubone.turbo.ui.demo.snake.game.GameLayer;
import cc.cubone.turbo.ui.demo.snake.game.TipLayer;

public class SnakeSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {

    private Scene mScene;
    private boolean mResumeNeeded = false;

    private GameLayer mGameLayer;
    private TipLayer mTipLayer;

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

        Status.DEBUG = BuildConfig.DEBUG;

        Scene scene = new Scene(this);
        scene.addCallback(mSceneCallback);

        GameLayer gameLayer = new GameLayer(scene);
        gameLayer.addCallback(mGameCallback);
        mGameLayer = gameLayer;

        TipLayer tipLayer = new TipLayer(scene);
        tipLayer.setBackgroundColor(0x33ffffff);
        tipLayer.setTextColor(Color.WHITE);
        tipLayer.setTextSize(scene.getPainter().dp2px(60));
        tipLayer.setText("Click\nTo\nStart");
        mTipLayer = tipLayer;

        scene.addLayers(gameLayer, tipLayer);
        mScene = scene;
    }

    public void onResume() {
        if (mResumeNeeded) {
            mScene.resume();
        }
    }

    public void onPause() {
        // the surface view will not be destroyed if turn screen off using power button directly
        mScene.pause();
        mResumeNeeded = true;
    }

    private LifeCircle.Callback mSceneCallback = new LifeCircle.Callback() {
        @Override
        public void onLifeStart() {
        }
        @Override
        public void onLifeResume() {
        }
        @Override
        public void onLifePause() {
            mGameLayer.pause();
        }
        @Override
        public void onLifeStop() {
        }
    };

    private LifeCircle.Callback mGameCallback = new LifeCircle.Callback() {
        @Override
        public void onLifeStart() {
            mTipLayer.setVisible(false);
        }
        @Override
        public void onLifeResume() {
            mTipLayer.setVisible(false);
        }
        @Override
        public void onLifePause() {
            mTipLayer.setText("Click\nTo\nResume");
            mTipLayer.setVisible(true);
        }
        @Override
        public void onLifeStop() {
            mTipLayer.setText("Click\nTo\nStart");
            mTipLayer.setVisible(true);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mScene.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mScene.resume();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // will resume on surface created
        mResumeNeeded = false;
    }

}
