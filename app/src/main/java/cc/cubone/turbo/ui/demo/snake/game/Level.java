package cc.cubone.turbo.ui.demo.snake.game;

public class Level {

    public static final int TICK_INTERVAL[] = {
            500, 400, 300, 200, 100,
    };

    private static final int LEVEL_MAX = TICK_INTERVAL.length - 1;

    private int mValue = 0;

    public int value() {
        return mValue;
    }

    public int tickInterval() {
        return TICK_INTERVAL[mValue];
    }

    public void up() {
        if (mValue >= LEVEL_MAX) return;
        ++mValue;
    }
}
