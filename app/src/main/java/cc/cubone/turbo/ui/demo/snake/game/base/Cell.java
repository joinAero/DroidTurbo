package cc.cubone.turbo.ui.demo.snake.game.base;

import android.graphics.RectF;

public class Cell {

    private float mX;
    private float mY;
    private float mSize;

    private Cell.Style mStyle = Style.BLANK;
    private int mColor;

    public enum Style {
        BLANK, FILL,
    }

    public Cell(float x, float y, float size) {
        mX = x;
        mY = y;
        mSize = size;
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
}
