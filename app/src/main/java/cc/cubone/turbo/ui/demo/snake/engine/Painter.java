package cc.cubone.turbo.ui.demo.snake.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
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

    public void drawText(Canvas canvas, String text, int gravity, Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        paint.getTextBounds(text, 0, text.length(), rect);
        final int height = (int) (fontMetrics.bottom - fontMetrics.top);
        final int diff = height - rect.height();
        rect2.set(0, 0, canvas.getWidth(), canvas.getHeight());
        Gravity.apply(gravity, rect.width() + diff, height, rect2, rect);
        canvas.drawText(text, rect.left + diff * 0.5f, rect.top - fontMetrics.top, pencil);
    }
}
