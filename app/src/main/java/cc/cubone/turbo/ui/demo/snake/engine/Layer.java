package cc.cubone.turbo.ui.demo.snake.engine;

public abstract class Layer implements Drawable {

    private boolean mVisible = true;

    public boolean isVisible() {
        return mVisible;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

}
