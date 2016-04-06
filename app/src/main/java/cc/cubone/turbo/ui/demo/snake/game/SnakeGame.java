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

public class SnakeGame implements GameLayer.Callback {

    private Scene mScene;
    private boolean mResumeNeeded = false;

    private GameLayer mGameLayer;
    //private StatLayer mStatLayer;
    private TipLayer mTipLayer;

    private final int LEVEL_TICK_INTERVAL[] = {
            250, 200, 150, 100, 50,
    };

    private Score mScore;
    private Level mLevel;

    public SnakeGame(SurfaceView surfaceView) {
        init(surfaceView);
    }

    private void init(SurfaceView surfaceView) {
        mScore = new Score();
        mLevel = new Level(1, LEVEL_TICK_INTERVAL.length);

        Scene scene = new Scene(surfaceView);
        scene.addCallback(mSceneCallback);

        GameLayer gameLayer = new GameLayer(scene);
        gameLayer.addCallback(mGameCallback);
        gameLayer.setCallback(this);
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

        initGame();
    }

    private void initGame() {
        mScore.set(0);
        mLevel.set(1);
        updateLevelStatus();
    }

    private void updateLevelStatus() {
        mGameLayer.setTickInterval(LEVEL_TICK_INTERVAL[mLevel.value() - 1]);
    }

    public Scene getScene() {
        return mScene;
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

    @Override
    public void onGameFail() {
    }

    @Override
    public void onGameGrow(int size) {
        mScore.add(mLevel);
        if (!mLevel.isMax()) {
            int levelNew = (size - 3) / 5 + 1;
            if (mLevel.value() < levelNew) {
                mLevel.up();
                updateLevelStatus();
                mScene.toast("Level up " + levelNew);
            }
        }
    }

    @Override
    public void onGameOver(boolean perfect) {
    }

    private void showTip(String text) {
        mTipLayer.setText(text);
        mTipLayer.setVisible(true);
    }

    private void hideTip() {
        mTipLayer.setVisible(false);
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
            hideTip();
            initGame();
        }
        @Override
        public void onLifeResume() {
            hideTip();
        }
        @Override
        public void onLifePause() {
            showTip("Click\nTo\nResume");
        }
        @Override
        public void onLifeStop() {
            if (mGameLayer.isGameOver()) {
                if (mGameLayer.isGameOverPerfect()) {
                    showTip("Fantastic");
                } else {
                    showTip("Game Over");
                }
            } else {
                showTip("Click\nTo\nStart");
            }
        }
    };

}
