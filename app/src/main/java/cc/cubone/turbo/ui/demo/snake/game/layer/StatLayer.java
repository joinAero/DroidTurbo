package cc.cubone.turbo.ui.demo.snake.game.layer;

import android.graphics.Canvas;
import android.view.Gravity;

import cc.cubone.turbo.ui.demo.snake.engine.Painter;
import cc.cubone.turbo.ui.demo.snake.engine.Scene;
import cc.cubone.turbo.ui.demo.snake.engine.view.Layer;
import cc.cubone.turbo.ui.demo.snake.game.status.Level;
import cc.cubone.turbo.ui.demo.snake.game.status.Score;

public class StatLayer extends Layer {

    private Score mScore;
    private Level mLevel;

    public StatLayer(Scene scene, Score score, Level level) {
        super(scene);
        mScore = score;
        mLevel = level;
    }

    @Override
    protected void onDraw(Canvas canvas, Scene scene) {
        final Painter painter = scene.getPainter();
        painter.drawText(canvas, "Score: " + mScore.value(), Gravity.START | Gravity.TOP);
        painter.drawText(canvas, "Level: " + mLevel.value(), Gravity.END | Gravity.TOP);
    }
}
