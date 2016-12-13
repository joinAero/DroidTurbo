package cc.eevee.turbo.ui.demo.snake.game.sprite;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import cc.eevee.turbo.ui.demo.snake.engine.Painter;
import cc.eevee.turbo.ui.demo.snake.engine.Scene;
import cc.eevee.turbo.ui.demo.snake.engine.util.Rand;
import cc.eevee.turbo.ui.demo.snake.engine.view.Sprite;
import cc.eevee.turbo.ui.demo.snake.game.base.Cell;

public class Fruit extends Sprite {

    private Rand.Color mRandColor;

    private Cell mCell;
    private int mColor;

    public Fruit(Scene scene) {
        super(scene);
        mRandColor = new Rand.Color();
    }

    public Cell get() {
        return mCell;
    }

    public int getColor() {
        return mColor;
    }

    public void grow(Cell emptyCell) {
        if (!Cell.isEmpty(emptyCell)) {
            throw new IllegalStateException("cell is not empty");
        }
        mCell = emptyCell;
        mCell.setStyle(Cell.Style.FRUIT);
        mColor = mRandColor.get();
    }

    public void reset() {
        mCell = null;
    }

    @Override
    protected void onDraw(Canvas canvas, Scene scene) {
        if (mCell == null) return;

        final Painter painter = scene.getPainter();
        final Paint brush = painter.brush;
        final RectF rectF = painter.rectF;

        mCell.getBounds(rectF);
        brush.setColor(mColor);
        canvas.drawRect(rectF, brush);
    }
}
