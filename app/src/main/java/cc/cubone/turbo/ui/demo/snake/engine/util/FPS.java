package cc.cubone.turbo.ui.demo.snake.engine.util;

import java.util.Vector;

public class FPS {

    private Vector<Long> mFrames;

    public FPS() {
        this(100);
    }

    public FPS(int samplingFrameCount) {
        mFrames = new Vector<>(samplingFrameCount);
    }

    public void reset() {
        mFrames.clear();
    }

    public void update() {
        if (mFrames.size() >= mFrames.capacity()) {
            mFrames.remove(0);
        }
        mFrames.add(System.currentTimeMillis());
    }

    public float get() {
        final int n = mFrames.size() - 1;
        return n <= 0 ? 0 : 1000f * n / (mFrames.get(n) - mFrames.get(0));
    }

}
