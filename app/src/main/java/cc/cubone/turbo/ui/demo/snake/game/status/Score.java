package cc.cubone.turbo.ui.demo.snake.game.status;

public class Score {

    private int mValue = 0;

    public int value() {
        return mValue;
    }

    public void set(int value) {
        mValue = value;
    }

    public void add(Level level) {
        add(level.value() * 10);
    }

    public void add(int value) {
        mValue += value;
    }
}
