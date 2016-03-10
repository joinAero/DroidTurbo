package cc.cubone.turbo.ui.demo.snake.engine;

import android.graphics.Canvas;

public abstract class Layer {

    private boolean mVisible = true;

    public boolean isVisible() {
        return mVisible;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public abstract void draw(Canvas canvas, Painter painter, Status status);

}
