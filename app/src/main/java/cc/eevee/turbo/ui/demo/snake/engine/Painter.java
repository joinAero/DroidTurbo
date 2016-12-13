package cc.eevee.turbo.ui.demo.snake.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

public class Painter {

    public final Paint brush = new Paint();
    public final TextPaint pencil = new TextPaint();

    public final Rect rect = new Rect();
    public final Rect rect2 = new Rect();
    public final RectF rectF = new RectF();

    public final Context context;
    public final float textSizeDefault;

    public Painter(Context context) {
        this.context = context;
        this.textSizeDefault = dp2px(12);
        reset();
    }

    public void reset() {
        resetBrush();
        resetPencil();
    }

    public Paint resetBrush() {
        brush.reset();
        brush.setAntiAlias(true);
        brush.setStyle(Paint.Style.FILL);
        return brush;
    }

    public TextPaint resetPencil() {
        pencil.reset();
        pencil.setAntiAlias(true);
        pencil.setStyle(Paint.Style.FILL);
        pencil.setTypeface(Typeface.DEFAULT);
        pencil.setTextAlign(Paint.Align.LEFT);
        pencil.setTextSize(textSizeDefault);
        pencil.setColor(Color.GREEN);
        return pencil;
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public void drawText(Canvas canvas, String text, int gravity) {
        drawText(canvas, text, gravity, false, pencil);
    }

    public void drawText(Canvas canvas, String text, int gravity, boolean multiline) {
        drawText(canvas, text, gravity, multiline, pencil);
    }

    public void drawText(Canvas canvas, String text, int gravity, boolean multiline, TextPaint paint) {
        rect.set(10, 0, canvas.getWidth() - 10, canvas.getHeight());
        drawText(canvas, text, rect, gravity, multiline, paint);
    }

    public void drawText(Canvas canvas, String text, Rect container, int gravity,
                         boolean multiline, TextPaint paint) {
        final Rect rect = rect2;
        if (multiline) {
            final StaticLayout layout = new StaticLayout(text, paint, container.width(),
                    Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            final float w = getLineWidthMax(layout);
            final int h = layout.getHeight();
            Gravity.apply(gravity, (int) w, h, container, rect);
            float dx = rect.left;
            switch (paint.getTextAlign()) {
                case LEFT: dx = rect.left; break;
                case CENTER: dx += w * 0.5f; break;
                case RIGHT: dx += w; break;
            }
            canvas.save();
            canvas.translate(dx, rect.top);
            layout.draw(canvas);
            canvas.restore();
        } else {
            paint.getTextBounds(text, 0, text.length(), rect);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            final int height = (int) (fontMetrics.bottom - fontMetrics.top);
            Gravity.apply(gravity, rect.width(), height, container, rect);
            canvas.drawText(text, rect.left, rect.top - fontMetrics.top, paint);
        }
    }

    private float getLineWidthMax(StaticLayout layout) {
        float width;
        float widthMax = 0.0f;
        final int lineCount = layout.getLineCount();
        for(int i = 0; i < lineCount; i++) {
            width = layout.getLineWidth(i);
            if (width > widthMax) {
                widthMax = width;
            }
        }
        return widthMax;
    }

}
