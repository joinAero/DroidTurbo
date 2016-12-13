package cc.eevee.turbo.ui.demo.snake.engine.view;

import android.graphics.Canvas;
import android.view.MotionEvent;

import cc.eevee.turbo.ui.demo.snake.engine.Scene;
import cc.eevee.turbo.ui.demo.snake.engine.feature.Drawable;
import cc.eevee.turbo.ui.demo.snake.engine.feature.Touchable;

public abstract class Layer implements Drawable, Touchable {

    private Scene mScene;

    private boolean mVisible = true;
    private boolean mTouchable = false;

    public Layer(Scene scene) {
        this(scene, false);
    }

    public Layer(Scene scene, boolean touchable) {
        mScene = scene;
        setTouchable(touchable);
    }

    public Scene getScene() {
        return mScene;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public boolean isTouchable() {
        return mTouchable;
    }

    public void setTouchable(boolean touchable) {
        mTouchable = touchable;
    }

    @Override
    public void draw(Canvas c) {
        onDraw(c, mScene);
    }

    protected abstract void onDraw(Canvas canvas, Scene scene);

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }
}
