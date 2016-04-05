package cc.cubone.turbo.ui.demo.snake.game.base;

import android.graphics.Canvas;

public class Grid {

    private int mColumn;
    private int mRow;

    private float mLeft;
    private float mTop;

    private int mWidth;
    private int mHeight;
    private int mCellSize;

    private Cell[][] mCells;

    public Grid(Canvas canvas, int cellSize) {
        init(canvas, cellSize);
    }

    public int column() {
        return mColumn;
    }

    public int row() {
        return mRow;
    }

    public float left() {
        return mLeft;
    }

    public float top() {
        return mTop;
    }

    public int width() {
        return mWidth;
    }

    public int height() {
        return mHeight;
    }

    public int cellSize() {
        return mCellSize;
    }

    public Cell cell(int row, int column) {
        return mCells[row][column];
    }

    public Cell[][] cells() {
        return mCells;
    }

    public void init(Canvas canvas, int cellSize) {
        mCellSize = cellSize;

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
                mCells[i][j] = new Cell(i, j, mLeft + j * cellSize, y, cellSize);
            }
        }
    }

}
