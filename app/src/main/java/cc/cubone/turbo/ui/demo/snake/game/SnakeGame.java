package cc.cubone.turbo.ui.demo.snake.game;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import cc.cubone.turbo.ui.demo.snake.engine.LifeCircle;
import cc.cubone.turbo.ui.demo.snake.engine.Scene;
import cc.cubone.turbo.ui.demo.snake.engine.Status;
import cc.cubone.turbo.ui.demo.snake.game.layer.GameLayer;
import cc.cubone.turbo.ui.demo.snake.game.layer.StatLayer;
import cc.cubone.turbo.ui.demo.snake.game.layer.TipLayer;
import cc.cubone.turbo.ui.demo.snake.game.status.Level;
import cc.cubone.turbo.ui.demo.snake.game.status.Score;

public class SnakeGame {

    private Scene mScene;
    private boolean mResumeNeeded = false;

    private GameLayer mGameLayer;
    //private StatLayer mStatLayer;
    private TipLayer mTipLayer;

    private final int LEVEL_TICK_INTERVAL[] = {
            500, 400, 300, 200, 100,
    };

    private Score mScore;
    private Level mLevel;

    public SnakeGame(SurfaceView surfaceView) {
        init(surfaceView);
    }

    private void init(SurfaceView surfaceView) {
        mScore = new Score();
        mLevel = new Level(LEVEL_TICK_INTERVAL.length);

        Scene scene = new Scene(surfaceView);
        scene.addCallback(mSceneCallback);

        GameLayer gameLayer = new GameLayer(scene);
        gameLayer.addCallback(mGameCallback);
        mGameLayer = gameLayer;

        StatLayer statLayer = new StatLayer(scene, mScore, mLevel);
        //mStatLayer = statLayer;

        TipLayer tipLayer = new TipLayer(scene);
        tipLayer.setBackgroundColor(0x33ffffff);
        tipLayer.setTextColor(Color.WHITE);
        tipLayer.setTextSize(scene.getPainter().dp2px(60));
        tipLayer.setText("Click\nTo\nStart");
        mTipLayer = tipLayer;

        scene.addLayers(gameLayer, statLayer, tipLayer);
        mScene = scene;
    }

    public void setDebug(boolean debug) {
        Status.DEBUG = debug;
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

    public void onSurfaceCreated(SurfaceHolder holder) {
        mScene.resume();
    }

    public void onSurfaceDestroyed(SurfaceHolder holder) {
        // will resume on surface created
        mResumeNeeded = false;
    }

    public boolean onTouchEvent(MotionEvent e) {
        return mScene.onTouchEvent(e);
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

}
