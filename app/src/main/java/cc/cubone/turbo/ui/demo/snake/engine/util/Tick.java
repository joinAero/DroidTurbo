package cc.cubone.turbo.ui.demo.snake.engine.util;

import cc.cubone.turbo.ui.demo.snake.engine.Status;

public class Tick {

    private Status mStatus;
    private Callback mCallback;

    private long mTickTime;
    private long mTickInterval;

    public Tick(Status status, Callback callback) {
        this(status, 1000, callback); // 1s
    }

    public Tick(Status status, long interval, Callback callback) {
        if (callback == null) throw new IllegalArgumentException();
        mStatus = status;
        mCallback = callback;
        mTickTime = 0;
        mTickInterval = interval;
    }

    public void reset() {
        mTickTime = 0;
    }

    public void reset(Status status) {
        mStatus = status;
        mTickTime = 0;
    }

    public void setInterval(long interval) {
        mTickInterval = interval;
    }

    public void update() {
        final long elapsed = mStatus.getTimeElapsed();
        final long tickDiff = (elapsed - mTickTime) - mTickInterval;
        if (tickDiff >= 0) {
            mTickTime = elapsed - tickDiff;
            mCallback.onTick();
        }
    }

    public interface Callback {
        public void onTick();
    }
}
