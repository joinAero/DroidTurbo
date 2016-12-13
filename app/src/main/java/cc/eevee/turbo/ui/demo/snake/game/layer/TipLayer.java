package cc.eevee.turbo.ui.demo.snake.game.layer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.Gravity;

import cc.eevee.turbo.ui.demo.snake.engine.Painter;
import cc.eevee.turbo.ui.demo.snake.engine.Scene;
import cc.eevee.turbo.ui.demo.snake.engine.view.Layer;

public class TipLayer extends Layer {

    private int mBackgroundColor;

    private String mText;
    private float mTextSize;
    private int mTextColor;

    public TipLayer(Scene scene) {
        super(scene);
    }

    @Override
    protected void onDraw(Canvas canvas, Scene scene) {
        final Painter painter = scene.getPainter();
        if (mBackgroundColor != 0) {
            canvas.drawColor(mBackgroundColor);
        }
        if (mText != null) {
            TextPaint pencil = painter.pencil;
            pencil.setTextSize(mTextSize);
            pencil.setColor(mTextColor);
            pencil.setTextAlign(Paint.Align.CENTER);
            painter.drawText(canvas, mText, Gravity.CENTER, true);
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
