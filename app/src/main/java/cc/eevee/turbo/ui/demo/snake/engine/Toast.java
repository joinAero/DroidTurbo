package cc.eevee.turbo.ui.demo.snake.engine;

import android.graphics.Canvas;
import android.text.TextPaint;
import android.view.Gravity;

import cc.eevee.turbo.ui.demo.snake.engine.feature.Drawable;

/*public*/ class Toast implements Drawable {

    public static final int LONG = 3500;
    public static final int SHORT = 2000;

    private Scene mScene;
    private long mDuration;

    private String mText;
    private int mTextColor;

    private long mTimeShowStart = -1;

    public Toast(Scene scene, long duration) {
        mScene = scene;
        mDuration = duration;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setTextColor(int color) {
        mTextColor = color;
    }

    public void show() {
        mTimeShowStart = System.currentTimeMillis();
    }

    public boolean isShowing() {
        return mTimeShowStart != -1;
    }

    @Override
    public void draw(Canvas c) {
        if (mTimeShowStart == -1) return;
        final long elapsed = System.currentTimeMillis() - mTimeShowStart;
        if (elapsed > mDuration) {
            mTimeShowStart = -1;
            onExpire(1f);
            return;
        }
        float ratio = 1f * elapsed / mDuration;
        onUpdate(ratio);

        if (mText != null) {
            final Painter painter = mScene.getPainter();
            final TextPaint pencil = painter.resetPencil();
            final int alpha = (int) (0xff * (1 - ratio));
            pencil.setColor((mTextColor & 0x00ffffff) | (alpha << 24));
            painter.rect.set(0, 0, c.getWidth(), c.getHeight() * 9 / 10);
            painter.drawText(c, mText, painter.rect, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
                    false, pencil);
        }
    }

    protected void onUpdate(float ratio) {
    }

    protected void onExpire(float ratio) {
    }

}
