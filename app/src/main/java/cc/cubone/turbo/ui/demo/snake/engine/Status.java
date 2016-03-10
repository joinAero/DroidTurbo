package cc.cubone.turbo.ui.demo.snake.engine;

public class Status {

    /*package*/ boolean debug = false;
    /*package*/ boolean started = false;
    /*package*/ boolean pausing = false;
    /*package*/ long timeStart;
    /*package*/ long timeElapsed;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isStopped() {
        return !started;
    }

    public boolean isRunning() {
        return started && !pausing;
    }

    public boolean isPausing() {
        return pausing;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }
}
