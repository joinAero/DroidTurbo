package cc.cubone.turbo.ui.demo.snake;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import cc.cubone.turbo.BuildConfig;
import cc.cubone.turbo.ui.demo.snake.game.SnakeGame;

public class SnakeSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {

    private SnakeGame mSnakeGame;

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
        mSnakeGame = new SnakeGame(this);
        mSnakeGame.setDebug(BuildConfig.DEBUG);
    }

    public void onResume() {
        mSnakeGame.onResume();
    }

    public void onPause() {
        mSnakeGame.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return mSnakeGame.onTouchEvent(e) || super.onTouchEvent(e);
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSnakeGame.onSurfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSnakeGame.onSurfaceDestroyed(holder);
    }

}
