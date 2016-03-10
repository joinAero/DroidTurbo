package cc.cubone.turbo.ui.demo.snake.engine.util;

import java.util.Random;

public class Rand {

    public static class Color {

        float h;
        float[] hsv;

        public Color() {
            h = new Random(System.currentTimeMillis()).nextFloat();
            hsv = new float[]{h, 0.5f, 0.95f};
        }

        public int get() {
            h += 0.618033988749895f;
            h %= 1f;
            hsv[0] = h * 360;
            return android.graphics.Color.HSVToColor(0xFF, hsv);
        }

    }
}
