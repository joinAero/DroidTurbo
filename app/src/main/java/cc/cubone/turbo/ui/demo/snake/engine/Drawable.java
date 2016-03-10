package cc.cubone.turbo.ui.demo.snake.engine;

import android.graphics.Canvas;

public interface Drawable {
    public void draw(Canvas canvas, Painter painter, Status status);
}
