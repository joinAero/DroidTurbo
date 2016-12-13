package cc.eevee.turbo.ui.demo.snake.engine.view;

import android.graphics.Canvas;

import cc.eevee.turbo.ui.demo.snake.engine.LifeCircle;
import cc.eevee.turbo.ui.demo.snake.engine.Scene;
import cc.eevee.turbo.ui.demo.snake.engine.Status;

public abstract class LifeLayer extends Layer {

    private LifeCircle mLifeCircle;

    public LifeLayer(Scene scene) {
        this(scene, false);
    }

    public LifeLayer(Scene scene, boolean touchable) {
        super(scene, touchable);
        mLifeCircle = new LifeCircle();
    }

    public Status getStatus() {
        return mLifeCircle.getStatus();
    }

    public void addCallback(LifeCircle.Callback callback) {
        mLifeCircle.addCallback(callback);
    }

    public void removeCallback(LifeCircle.Callback callback) {
        mLifeCircle.removeCallback(callback);
    }

    public void start() {
        mLifeCircle.start();
    }

    public void resume() {
        mLifeCircle.resume();
    }

    public void pause() {
        mLifeCircle.pause();
    }

    public void stop() {
        mLifeCircle.stop();
    }

    @Override
    public void draw(Canvas c) {
        mLifeCircle.update();
        super.draw(c);
    }

}
