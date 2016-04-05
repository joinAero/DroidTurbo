package cc.cubone.turbo.ui.demo.snake.game.status;

public class Level {

    private int mMax = 0;
    private int mValue = 0;

    public Level(int max) {
        mMax = max;
    }

    public int max() {
        return mMax;
    }

    public int value() {
        return mValue;
    }

    public void up() {
        if (mValue >= mMax) return;
        ++mValue;
    }
}
