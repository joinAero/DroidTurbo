package cc.eevee.turbo.ui.demo.snake.engine.view;

import cc.eevee.turbo.ui.demo.snake.engine.Scene;

public abstract class Sprite extends Layer {

    public Sprite(Scene scene) {
        super(scene);
    }

    public Sprite(Scene scene, boolean touchable) {
        super(scene, touchable);
    }
}
