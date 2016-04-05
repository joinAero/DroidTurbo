package cc.cubone.turbo.ui.demo.snake.game.layer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cc.cubone.turbo.ui.demo.snake.engine.Painter;
import cc.cubone.turbo.ui.demo.snake.engine.Scene;
import cc.cubone.turbo.ui.demo.snake.engine.Status;
import cc.cubone.turbo.ui.demo.snake.engine.part.Direction;
import cc.cubone.turbo.ui.demo.snake.engine.util.Gesture;
import cc.cubone.turbo.ui.demo.snake.engine.util.Tick;
import cc.cubone.turbo.ui.demo.snake.engine.view.LifeLayer;
import cc.cubone.turbo.ui.demo.snake.game.base.Cell;
import cc.cubone.turbo.ui.demo.snake.game.base.Grid;
import cc.cubone.turbo.ui.demo.snake.game.sprite.Fruit;
import cc.cubone.turbo.ui.demo.snake.game.sprite.Snake;
import cc.cubone.turbo.util.TimeUtils;

public class GameLayer extends LifeLayer implements Gesture.Callback, Tick.Callback {

    private Grid mGrid;

    private Gesture mGesture;
    private Tick mTick;

    private Controller mController;

    public GameLayer(Scene scene) {
        super(scene, true);
        mGesture = new Gesture(this);
        mTick = new Tick(getStatus(), this);
        mController = new Controller(scene);
    }

    public void setTickInterval(long interval) {
        mTick.setInterval(interval);
    }

    public void setCallback(Callback callback) {
        mController.setCallback(callback);
    }

    @Override
    protected void onDraw(Canvas canvas, Scene scene) {
        final Painter painter = scene.getPainter();
        if (mGrid == null) {
            mGrid = new Grid(canvas, (int) painter.dp2px(12));
            mController.init(mGrid);
        }
        mTick.update();

        if (Status.DEBUG) drawGird(canvas, painter);
        mController.draw(canvas, getStatus());
        if (Status.DEBUG) drawInfo(canvas, painter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return mGesture.onTouchEvent(e);
    }

    @Override
    public boolean onGestureDown() {
        //getScene().toast("Down");
        return true;
    }

    @Override
    public void onGestureMove(Direction direction) {
        if (getStatus().isPausing()) return;
        if (mController.setDirection(direction) && Status.DEBUG) {
            getScene().toast("Move " + direction.name().toLowerCase());
        }
    }

    @Override
    public void onGestureUp(boolean moved) {
        //getScene().toast("Up");
        final Status status = getStatus();
        if (status.isStopped()) {
            start();
        } else if (status.isRunning()) {
            if (!moved) pause();
        } else {
            resume();
        }
    }

    @Override
    public void onTick() {
        mController.move();
    }

    private void drawGird(Canvas canvas, Painter painter) {
        float left = mGrid.left();
        float top = mGrid.top();
        int width = mGrid.width();
        int height = mGrid.height();
        int cellSize = mGrid.cellSize();

        int row = mGrid.row();
        int column = mGrid.column();

        Paint brush = painter.brush;
        brush.setColor(Color.RED);

        float y;
        float right = left + width;
        for (int i = 0; i <= row; i++) {
            y = top + i * cellSize;
            canvas.drawLine(left, y, right, y, brush);
        }
        float x;
        float bottom = top + height;
        for (int j = 0; j <= column; j++) {
            x = left + j * cellSize;
            canvas.drawLine(x, top, x, bottom, brush);
        }

        /*RectF rectF = painter.rectF;
        Cell[][] cells = mGrid.cells();
        Cell cell;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                cell = cells[i][j];
                cell.getBounds(rectF);
                brush.setColor(mRandColor.get());
                canvas.drawRect(rectF, brush);
            }
        }*/
    }

    private void drawInfo(Canvas canvas, Painter painter) {
        final TextPaint pencil = painter.pencil;
        pencil.setTextAlign(Paint.Align.RIGHT);
        String info = String.format("%dx%d\nGame: %s",
                mGrid.row(), mGrid.column(),
                TimeUtils.readableSeconds(getStatus().getTimeElapsed() / 1000));
        painter.drawText(canvas, info, Gravity.END | Gravity.BOTTOM, true);
    }

    public interface Callback {
        public void onGameFail();
        public void onGameGrow();
        public void onGameOver();
    }

    private static class Controller {

        private Grid mGrid;
        private Snake mSnake;
        private Fruit mFruit;

        private Callback mCallback;
        private Direction mDirection;

        public Controller(Scene scene) {
            mSnake = new Snake(scene);
            mFruit = new Fruit(scene);
            mDirection = Direction.RIGHT;
        }

        public void init(Grid grid) {
            mGrid = grid;
            if (mSnake.init(grid)) {
                growFruit();
            } else if (mCallback != null) {
                // fail cause not enough cells
                mCallback.onGameFail();
            }
        }

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        public boolean setDirection(Direction direction) {
            if (Direction.isOpposite(mDirection, direction)) {
                return false;
            }
            mDirection = direction;
            return true;
        }

        public void move() {
            LinkedList<Cell> snakeCells = mSnake.get();
            Cell snakeHead = snakeCells.getFirst();
            Cell snakeTail = snakeCells.getLast();

            Cell nextCell = next(snakeHead);
            if (Cell.isWalkable(nextCell)) {
                snakeCells.addFirst(nextCell);
                snakeCells.removeLast();
                snakeTail.setStyle(Cell.Style.EMPTY);
            } else {
            }
        }

        public Cell next(Cell cell) {
            int row = cell.row();
            int col = cell.column();
            switch (mDirection) {
                case LEFT:  col -= 1; if (col < 0) col = mGrid.column() - 1; break;
                case UP:    row -= 1; if (row < 0) row = mGrid.row() - 1; break;
                case RIGHT: col += 1; if (col >= mGrid.column()) col = 0; break;
                case DOWN:  row += 1; if (row >= mGrid.row()) row = 0; break;
                default: throw new IllegalArgumentException();
            }
            return mGrid.cell(row, col);
        }

        private boolean growFruit() {
            List<Cell> emptyCells = cells(mGrid, Cell.Style.EMPTY);
            int n = emptyCells.size();
            if (n <= 0) {
                mFruit.reset();
                return false;
            }
            int randLoc = new Random(System.currentTimeMillis()).nextInt(n);
            mFruit.grow(emptyCells.get(randLoc));
            return true;
        }

        private List<Cell> cells(Grid grid, Cell.Style... styles) {
            if (styles == null) return null;
            List<Cell> resultCells = new ArrayList<>();
            Cell[][] cells = grid.cells();
            int row = grid.row();
            int column = grid.column();
            Cell cell;
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    cell = cells[i][j];
                    for (Cell.Style style : styles) {
                        if (cell.getStyle() == style) {
                            resultCells.add(cell);
                            break;
                        }
                    }
                }
            }
            return resultCells;
        }

        public void draw(Canvas canvas, Status status) {
            mSnake.draw(canvas);
            if (status.isStarted()) {
                mFruit.draw(canvas);
            }
        }
    }
}
