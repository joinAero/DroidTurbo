package cc.eevee.turbo.ui.demo.snake.game.status;

public class Level {

    private int mValue = 0;
    private int mMax = 0;

    public Level(int max) {
        this(0, max);
    }

    public Level(int value, int max) {
        if (value > max) {
            throw new IllegalArgumentException();
        }
        mValue = value;
        mMax = max;
    }

    public int value() {
        return mValue;
    }

    public int max() {
        return mMax;
    }

    public void set(int value) {
        mValue = value;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public boolean isMax() {
        return mValue == mMax;
    }

    public void up() {
        if (isMax()) return;
        ++mValue;
    }
}
