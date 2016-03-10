package cc.cubone.turbo.ui.demo.snake.game;

import android.graphics.Canvas;
import android.text.TextPaint;
import android.view.Gravity;

import cc.cubone.turbo.ui.demo.snake.engine.Drawable;
import cc.cubone.turbo.ui.demo.snake.engine.Layer;
import cc.cubone.turbo.ui.demo.snake.engine.Painter;
import cc.cubone.turbo.ui.demo.snake.engine.Status;

public class GameLayer extends Layer {

    private Grid mGrid;
    private Snake mSnake;
    private Stat mStat;

    @Override
    public void draw(Canvas canvas, Painter painter, Status status) {
        if (mGrid == null) {
            mGrid = new Grid(canvas, (int) painter.dp2px(16));
            mSnake = new Snake();
            mStat = new Stat();
        }
        mGrid.draw(canvas, painter, status);
        mSnake.draw(canvas, painter, status);
        mStat.draw(canvas, painter, status);
    }

    private static class Snake implements Drawable {

        @Override
        public void draw(Canvas canvas, Painter painter, Status status) {
        }
    }

    private static class Stat implements Drawable {

        public int score = 0;
        public int level = 0;

        @Override
        public void draw(Canvas canvas, Painter painter, Status status) {
            TextPaint pencil = painter.resetPencil();
            painter.drawText(canvas, "Score: " + score, Gravity.START | Gravity.TOP, pencil);
            painter.drawText(canvas, "Level: " + level, Gravity.END | Gravity.TOP, pencil);
        }
    }
}
