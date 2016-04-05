package cc.cubone.turbo.ui.demo.snake.game.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.LinkedList;
import java.util.List;

import cc.cubone.turbo.ui.demo.snake.engine.Painter;
import cc.cubone.turbo.ui.demo.snake.engine.Scene;
import cc.cubone.turbo.ui.demo.snake.engine.view.Sprite;
import cc.cubone.turbo.ui.demo.snake.game.base.Cell;
import cc.cubone.turbo.ui.demo.snake.game.base.Grid;

public class Snake extends Sprite {

    private List<Cell> mCells = new LinkedList<>();
    private int mColor = Color.GREEN;

    public Snake(Scene scene) {
        super(scene);
    }

    public boolean init(Grid grid) {
        if (grid.row() < 1 || grid.column() < 6) {
            return false;
        }
        int row = grid.row() / 2;
        int col = grid.column() / 2 - 1;
        Cell cell;
        for (int i = 0; i < 3; i++) {
            cell = grid.cell(row, col-i);
            cell.setStyle(Cell.Style.FILL);
            mCells.add(cell);
        }
        return true;
    }

    public List<Cell> get() {
        return mCells;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas, Scene scene) {
        final Painter painter = scene.getPainter();
        final Paint brush = painter.brush;
        final RectF rectF = painter.rectF;
        brush.setColor(mColor);
        for (Cell cell : mCells) {
            cell.getBounds(rectF);
            canvas.drawRect(rectF, brush);
        }
    }
}
