package cc.eevee.turbo.ui.demo.snake.engine;

public class Status {

    public static boolean DEBUG = false;

    /*package*/ boolean started = false;
    /*package*/ boolean running = false;
    /*package*/ long timeStart;
    /*package*/ long timeElapsed;

    public boolean isStarted() {
        return started;
    }

    public boolean isStopped() {
        return !started;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPausing() {
        return !running;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public long getTimeStart() {
        return timeStart;
    }
}
