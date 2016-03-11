package cc.cubone.turbo.ui.demo.snake.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.Gravity;

import cc.cubone.turbo.ui.demo.snake.engine.Drawable;
import cc.cubone.turbo.ui.demo.snake.engine.Painter;
import cc.cubone.turbo.ui.demo.snake.engine.Status;
import cc.cubone.turbo.ui.demo.snake.engine.util.Rand;

public class Grid implements Drawable {

    private int mColumn;
    private int mRow;

    private float mLeft;
    private float mTop;

    private int mWidth;
    private int mHeight;
    private int mCellSize;

    private Cell[][] mCells;

    private Rand.Color mRandColor;
    private String mInfo;

    public Grid(Canvas canvas, int cellSize) {
        init(canvas, cellSize);
    }

    public void init(Canvas canvas, int cellSize) {
        mCellSize = cellSize;
        mRandColor = new Rand.Color();

        final int w = canvas.getWidth();
        final int h = canvas.getHeight();

        mColumn = w / cellSize;
        mRow = h / cellSize;

        mWidth = mColumn * cellSize;
        mHeight = mRow * cellSize;

        mLeft = (w - mWidth) * 0.5f;
        mTop = (h - mHeight) * 0.5f;

        mCells = new Cell[mRow][mColumn];

        float y;
        for (int i = 0; i < mRow; i++) {
            y = mTop + i * cellSize;
            for (int j = 0; j < mColumn; j++) {
                mCells[i][j] = new Cell(mLeft + j * cellSize, y, cellSize);
            }
        }

        mInfo = String.format("Gird: %dx%d", mRow, mColumn);
    }

    @Override
    public void draw(Canvas canvas, Painter painter, Status status) {
        if (status.isDebug()) {
            drawGird(canvas, painter);
            //drawCells(canvas, painter);
            TextPaint pencil = painter.resetPencil();
            painter.drawText(canvas, mInfo, Gravity.END | Gravity.BOTTOM, pencil);
        }
    }

    private void drawGird(Canvas canvas, Painter painter) {
        Paint brush = painter.brush;
        brush.setColor(Color.RED);

        float y;
        float right = mLeft + mWidth;
        for (int i = 0; i <= mRow; i++) {
            y = mTop + i * mCellSize;
            canvas.drawLine(mLeft, y, right, y, brush);
        }

        float x;
        float bottom = mTop + mHeight;
        for (int j = 0; j <= mColumn; j++) {
            x = mLeft + j * mCellSize;
            canvas.drawLine(x, mTop, x, bottom, brush);
        }
    }

    private void drawCells(Canvas canvas, Painter painter) {
        Paint brush = painter.brush;
        RectF rectF = painter.rectF;

        Cell cell;
        for (int i = 0; i < mRow; i++) {
            for (int j = 0; j < mColumn; j++) {
                cell = mCells[i][j];
                cell.getBounds(rectF);
                brush.setColor(mRandColor.get());
                canvas.drawRect(rectF, brush);
            }
        }
    }

}