package cc.cubone.turbo.ui.demo.snake.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import cc.cubone.turbo.ui.demo.snake.engine.Layer;
import cc.cubone.turbo.ui.demo.snake.engine.Painter;
import cc.cubone.turbo.ui.demo.snake.engine.Status;

public class TipLayer extends Layer {

    private int mBackgroundColor;

    private String mText;
    private float mTextSize;
    private int mTextColor;

    @Override
    public void draw(Canvas canvas, Painter painter, Status status) {
        if (mBackgroundColor != 0) {
            canvas.drawColor(mBackgroundColor);
        }
        if (mText != null && !mText.isEmpty() && mTextSize > 0) {
            TextPaint pencil = painter.pencil;
            pencil.setTextSize(mTextSize);
            pencil.setColor(mTextColor);
            pencil.setTextAlign(Paint.Align.CENTER);

            StaticLayout layout = new StaticLayout(mText, pencil, canvas.getWidth(),
                    Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            canvas.save();
            canvas.translate(canvas.getWidth() * 0.5f,
                    (canvas.getHeight() - layout.getHeight()) * 0.5f);
            layout.draw(canvas);
            canvas.restore();
        }

    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
    }

    public void setTextColor(int color) {
        mTextColor = color;
    }
}
