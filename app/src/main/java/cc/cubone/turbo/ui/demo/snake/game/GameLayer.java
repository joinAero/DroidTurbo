package cc.cubone.turbo.ui.demo.snake.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.MotionEvent;

import cc.cubone.turbo.ui.demo.snake.engine.Painter;
import cc.cubone.turbo.ui.demo.snake.engine.Scene;
import cc.cubone.turbo.ui.demo.snake.engine.Status;
import cc.cubone.turbo.ui.demo.snake.engine.view.Layer;
import cc.cubone.turbo.ui.demo.snake.engine.view.LifeLayer;
import cc.cubone.turbo.util.TimeUtils;

public class GameLayer extends LifeLayer implements Gesture.Callback {

    private Grid mGrid;

    private Snake mSnake;
    private Stat mStat;

    private Level mLevel;

    private long mTickTime;
    private long mTickInterval;

    private Gesture mGesture;

    public GameLayer(Scene scene) {
        super(scene, true);

        mSnake = new Snake(scene);
        mStat = new Stat(scene);

        mLevel = new Level();
        mTickTime = 0;
        mTickInterval = mLevel.tickInterval();

        mGesture = new Gesture(20, this);
    }

    @Override
    protected void onDraw(Canvas canvas, Scene scene) {
        final Painter painter = scene.getPainter();
        if (mGrid == null) {
            mGrid = new Grid(canvas, (int) painter.dp2px(16));
        }
        final Status status = getStatus();
        final long elapsed = status.getTimeElapsed();
        final long tickDiff = (elapsed - mTickTime) - mTickInterval;
        if (tickDiff >= 0) {
            mTickTime = elapsed - tickDiff;
            onTick();
        }

        painter.resetPencil();
        if (Status.DEBUG) {
            mGrid.draw(canvas, scene.getPainter());
        }
        mSnake.draw(canvas);
        mStat.draw(canvas);
        if (Status.DEBUG) drawInfo(canvas, scene);
    }

    private void drawInfo(Canvas canvas, Scene scene) {
        final Painter painter = scene.getPainter();
        final TextPaint pencil = painter.pencil;
        pencil.setTextAlign(Paint.Align.RIGHT);
        String info = String.format("%dx%d\nGame: %s",
                mGrid.row(), mGrid.column(),
                TimeUtils.readableSeconds(getStatus().getTimeElapsed() / 1000));
        painter.drawText(canvas, info, Gravity.END | Gravity.BOTTOM, true);
    }

    private void onTick() {
    }

    public void levelUp() {
        mLevel.up();
        mTickInterval = mLevel.tickInterval();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return mGesture.onTouchEvent(e);
    }

    @Override
    public boolean onGestureDown() {
        //getScene().toast("Down");
        return true;
    }

    @Override
    public void onGestureMove(Direction direction) {
        if (getStatus().isPausing()) return;
        getScene().toast("Move " + direction.name().toLowerCase());
    }

    @Override
    public void onGestureUp(boolean moved) {
        //getScene().toast("Up");
        final Status status = getStatus();
        if (status.isStopped()) {
            start();
        } else if (status.isRunning()) {
            if (!moved) pause();
        } else {
            resume();
        }
    }

    private static class Snake extends Layer {

        public Snake(Scene scene) {
            super(scene);
        }

        @Override
        protected void onDraw(Canvas canvas, Scene scene) {
        }
    }

    private static class Stat extends Layer {

        public int score = 0;
        public int level = 0;

        public Stat(Scene scene) {
            super(scene);
        }

        @Override
        protected void onDraw(Canvas canvas, Scene scene) {
            final Painter painter = scene.getPainter();
            painter.drawText(canvas, "Score: " + score, Gravity.START | Gravity.TOP);
            painter.drawText(canvas, "Level: " + level, Gravity.END | Gravity.TOP);
        }
    }

}
