package cc.cubone.turbo.ui.demo.snake.engine;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.TypedValue;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

public class Painter {

    public final Paint brush = new Paint();
    public final TextPaint pencil = new TextPaint();

    public final Rect rect = new Rect();
    public final RectF rectF = new RectF();

    public final Context context;
    public final float textSizeDefault;

    public Painter(Context context) {
        this.context = context;
        this.textSizeDefault = dp2px(12);
        reset();
    }

    public void reset() {
        brush.reset();
        brush.setAntiAlias(true);
        brush.setStyle(Paint.Style.FILL);

        pencil.reset();
        pencil.setAntiAlias(true);
        pencil.setStyle(Paint.Style.FILL);
        pencil.setTypeface(Typeface.DEFAULT);
        pencil.setTextAlign(Paint.Align.LEFT);
        pencil.setTextSize(textSizeDefault);
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
