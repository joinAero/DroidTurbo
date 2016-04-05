package cc.cubone.turbo.ui.demo.snake.engine;

import java.util.ArrayList;

public class LifeCircle {

    protected Status mStatus;

    private ArrayList<Callback> mCallbacks;

    private long mTimeResume;
    private long mTimeElapsedBase;

    public LifeCircle() {
        mStatus = new Status();
    }

    public Status getStatus() {
        return mStatus;
    }

    public void addCallback(Callback callback) {
        if (callback == null) return;
        if (mCallbacks == null) {
            mCallbacks = new ArrayList<>();
        }
        mCallbacks.add(callback);
    }

    public void removeCallback(Callback callback) {
        if (callback == null || mCallbacks == null) return;
        mCallbacks.remove(callback);
        if (mCallbacks.isEmpty()) {
            mCallbacks = null;
        }
    }

    public synchronized void start() {
        if (mStatus.started) return;
        mStatus.started = true;
        mStatus.running = false;
        mStatus.timeStart = System.currentTimeMillis();
        mStatus.timeElapsed = 0;
        onStart();
        if (mCallbacks != null) {
            for (Callback callback : mCallbacks) {
                callback.onLifeStart();
            }
        }
        resume();
    }

    public synchronized void resume() {
        if (mStatus.started) {
            if (mStatus.running) return;
            mStatus.running = true;
            mTimeResume = System.currentTimeMillis();
            mTimeElapsedBase = mStatus.timeElapsed;
            onResume();
            if (mCallbacks != null) {
                for (Callback callback : mCallbacks) {
                    callback.onLifeResume();
                }
            }
        } else {
            start();
        }
    }

    public synchronized void pause() {
        if (mStatus.started && mStatus.running) {
            mStatus.running = false;
            onPause();
            if (mCallbacks != null) {
                for (Callback callback : mCallbacks) {
                    callback.onLifePause();
                }
            }
        }
    }

    public synchronized void stop() {
        if (!mStatus.started) return;
        pause();
        mStatus.started = false;
        onStop();
        if (mCallbacks != null) {
            for (Callback callback : mCallbacks) {
                callback.onLifeStop();
            }
        }
    }

    public void update() {
        if (mStatus.running) {
            mStatus.timeElapsed = mTimeElapsedBase + (System.currentTimeMillis() - mTimeResume);
        }
    }

    protected void onStart() {
    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    protected void onStop() {
    }

    public interface Callback {
        public void onLifeStart();
        public void onLifeResume();
        public void onLifePause();
        public void onLifeStop();
    }
}
