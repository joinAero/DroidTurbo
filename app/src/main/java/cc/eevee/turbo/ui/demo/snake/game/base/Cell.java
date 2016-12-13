package cc.eevee.turbo.ui.demo.snake.game.base;

import android.graphics.RectF;

public class Cell {

    private int mRow;
    private int mColumn;

    private float mX;
    private float mY;
    private float mSize;

    private Cell.Style mStyle = Style.EMPTY;
    private int mColor;

    public enum Style {
        EMPTY, FRUIT, SNAKE, WALL,
    }

    public Cell(int row, int column, float x, float y, float size) {
        mRow = row;
        mColumn = column;
        mX = x;
        mY = y;
        mSize = size;
    }

    public int row() {
        return mRow;
    }

    public int column() {
        return mColumn;
    }

    public void getBounds(RectF rectF) {
        rectF.set(mX, mY, mX + mSize, mY + mSize);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public Style getStyle() {
        return mStyle;
    }

    public void setStyle(Style style) {
        mStyle = style;
    }

    public static boolean isEmpty(Cell cell) {
        return cell.mStyle == Style.EMPTY;
    }

    public static boolean isWalkable(Cell cell) {
        return cell.mStyle == Style.EMPTY || cell.mStyle == Style.FRUIT;
    }
}
