package cc.cubone.turbo.ui.demo.snake.game.sprite;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cc.cubone.turbo.ui.demo.snake.engine.Painter;
import cc.cubone.turbo.ui.demo.snake.engine.Scene;
import cc.cubone.turbo.ui.demo.snake.engine.util.Rand;
import cc.cubone.turbo.ui.demo.snake.engine.view.Sprite;
import cc.cubone.turbo.ui.demo.snake.game.base.Cell;
import cc.cubone.turbo.ui.demo.snake.game.base.Grid;

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

    public boolean grow(Grid grid) {
        List<Cell> blankCells = new ArrayList<>();
        Cell[][] cells = grid.cells();
        int row = grid.row();
        int column = grid.column();
        Cell cell;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                cell = cells[i][j];
                if (cell.getStyle() == Cell.Style.BLANK) {
                    blankCells.add(cell);
                }
            }
        }
        return grow(blankCells);
    }

    public boolean grow(List<Cell> mBlankCells) {
        int n = mBlankCells.size();
        if (n <= 0) {
            mCell = null;
            return false;
        }
        int randLoc = new Random(System.currentTimeMillis()).nextInt(n);
        mCell = mBlankCells.get(randLoc);
        mCell.setStyle(Cell.Style.FILL);
        mColor = mRandColor.get();
        return true;
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
