package cc.eevee.turbo.ui.demo.snake.game.layer;

import android.graphics.Canvas;
import android.view.Gravity;

import cc.eevee.turbo.ui.demo.snake.engine.Painter;
import cc.eevee.turbo.ui.demo.snake.engine.Scene;
import cc.eevee.turbo.ui.demo.snake.engine.view.Layer;
import cc.eevee.turbo.ui.demo.snake.game.status.Level;
import cc.eevee.turbo.ui.demo.snake.game.status.Score;

public class StatLayer extends Layer {

    private Score mScore;
    private Level mLevel;

    private int mHighScore;

    public StatLayer(Scene scene, Score score, Level level) {
        super(scene);
        mScore = score;
        mLevel = level;
    }

    public int getHighScore() {
        return mHighScore;
    }

    public void setHighScore(int score) {
        mHighScore = score;
    }

    public void updateHighScore() {
        int score = mScore.value();
        if (mHighScore < score) {
            mHighScore = score;
        }
    }

    @Override
    protected void onDraw(Canvas canvas, Scene scene) {
        final Painter painter = scene.getPainter();
        painter.drawText(canvas, "Score: " + mScore.value(), Gravity.START | Gravity.TOP);
        painter.drawText(canvas, "Level: " + mLevel.value(), Gravity.END | Gravity.TOP);
        painter.drawText(canvas, "HighScore: " + mHighScore, Gravity.CENTER_HORIZONTAL | Gravity.TOP);
    }

}
