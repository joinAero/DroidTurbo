package cc.cubone.turbo.ui.demo.snake.game.layer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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

    private Callback mCallback;

    private boolean mRestartNeeded = true;

    public GameLayer(Scene scene) {
        super(scene, true);
        mGesture = new Gesture(this);
        mTick = new Tick(getStatus(), this);
        mController = new Controller(scene);
        mController.setCallback(new Callback() {
            @Override
            public void onGameFail() {
                if (mCallback != null) mCallback.onGameFail();
            }
            @Override
            public void onGameGrow(int size) {
                if (mCallback != null) mCallback.onGameGrow(size);
            }
            @Override
            public void onGameOver(boolean perfect) {
                stop();
                if (mCallback != null) mCallback.onGameOver(perfect);
            }
        });
    }

    public void setTickInterval(long interval) {
        mTick.setInterval(interval);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public boolean isGameReady() {
        return mController.isGameReady();
    }

    public boolean isGameOver() {
        return mController.isGameOver();
    }

    public boolean isGameOverPerfect() {
        return mController.isGameOverPerfect();
    }

    public int getSnakeSize() {
        return mController.getSnakeSize();
    }

    @Override
    public void start() {
        super.start();
        mController.onStart();
    }

    public void restart() {
        mRestartNeeded = true;
        mTick.reset();
        start();
    }

    @Override
    protected void onDraw(Canvas canvas, Scene scene) {
        final Painter painter = scene.getPainter();
        if (mRestartNeeded) {
            Grid grid = new Grid(canvas, (int) painter.dp2px(16));
            mController.init(grid);
            mGrid = grid;
            mRestartNeeded = false;
        }
        mTick.update();

        drawGird(canvas, painter);
        mController.onDraw(canvas, getStatus());
        if (Status.DEBUG) {
            drawCells(canvas, painter);
        }
        drawInfo(canvas, painter);
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
            if (isGameOver()) {
                if (!moved) restart();
            } else {
                start();
            }
        } else if (status.isRunning()) {
            if (!moved) pause();
        } else {
            resume();
        }
    }

    @Override
    public void onTick() {
        mController.onMove();
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
    }

    private void drawCells(Canvas canvas, Painter painter) {
        int row = mGrid.row();
        int column = mGrid.column();

        TextPaint pencil = painter.pencil;
        pencil.setColor(Color.RED);

        Rect rect = painter.rect;
        RectF rectF = painter.rectF;

        Cell[][] cells = mGrid.cells();
        Cell cell;
        Cell.Style style;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                cell = cells[i][j];
                cell.getBounds(rectF);
                style = cell.getStyle();

                rect.set((int) rectF.left, (int) rectF.top,
                        (int) rectF.right, (int) rectF.bottom);
                painter.drawText(canvas, style.name().substring(0, 1), rect,
                        Gravity.CENTER, false, pencil);
            }
        }
    }

    private void drawInfo(Canvas canvas, Painter painter) {
        final TextPaint pencil = painter.pencil;
        pencil.setColor(Color.GREEN);
        pencil.setTextAlign(Paint.Align.RIGHT);
        String info = String.format("%dx%d\nGame: %s",
                mGrid.row(), mGrid.column(),
                TimeUtils.readableSeconds(getStatus().getTimeElapsed() / 1000));
        painter.drawText(canvas, info, Gravity.END | Gravity.BOTTOM, true);
    }

    public interface Callback {
        public void onGameFail();
        public void onGameGrow(int size);
        public void onGameOver(boolean perfect);
    }

    private static class Controller {

        private Grid mGrid;
        private Snake mSnake;
        private Fruit mFruit;

        private Callback mCallback;
        private Direction mDirection;
        private Direction mDirectionNew;

        private boolean mGameReady = false;
        private boolean mGameOver = false;
        private boolean mGameOverPerfect = false;

        public Controller(Scene scene) {
            mSnake = new Snake(scene);
            mFruit = new Fruit(scene);
        }

        public void init(Grid grid) {
            mGrid = grid;
            mDirection = Direction.RIGHT;

            boolean ready = mSnake.init(grid);
            if (ready) {
                growFruit();
            } else if (mCallback != null) {
                // fail cause not enough cells
                mCallback.onGameFail();
            }
            mGameReady = ready;
        }

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        public boolean setDirection(Direction direction) {
            if (Direction.isOpposite(mDirection, direction)) {
                return false;
            }
            mDirectionNew = direction;
            return true;
        }

        public boolean isGameReady() {
            return mGameReady;
        }

        public boolean isGameOver() {
            return mGameOver;
        }

        public boolean isGameOverPerfect() {
            return mGameOverPerfect;
        }

        public int getSnakeSize() {
            return mSnake.get().size();
        }

        public void onStart() {
            mGameOver = false;
            mGameOverPerfect = false;
        }

        public void onDraw(Canvas canvas, Status status) {
            mSnake.draw(canvas);
            if (status.isStarted() || isGameOver()) {
                mFruit.draw(canvas);
            }
        }

        public void onMove() {
            if (!mGameReady || mGameOver) return;

            // apply new properties
            if (mDirectionNew != null) {
                mDirection = mDirectionNew;
                mDirectionNew = null;
            }

            LinkedList<Cell> snakeCells = mSnake.get();
            Cell snakeHead = snakeCells.getFirst();

            Cell nextCell = next(snakeHead);
            Cell.Style nextCellStyle = nextCell.getStyle();
            if (nextCellStyle == Cell.Style.EMPTY) {
                // EMPTY, go ahead
                moveSnake(nextCell);
            } else if (nextCellStyle == Cell.Style.FRUIT) {
                // FRUIT, grow snake
                if (growSnake(nextCell)) {
                    if (mCallback != null) mCallback.onGameGrow(snakeCells.size());
                } else {
                    // fail, all cells are SNAKE
                    mGameOver = true;
                    mGameOverPerfect = true;
                    if (mCallback != null) mCallback.onGameOver(true);
                }
            } else {
                if (nextCell == snakeCells.getLast()) {
                    // move to snake tail
                    moveSnake(nextCell);
                } else {
                    // not walkable, game over
                    mGameOver = true;
                    mGameOverPerfect = false;
                    if (mCallback != null) mCallback.onGameOver(false);
                }
            }
        }

        private Cell next(Cell cell) {
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

        private void moveSnake(Cell nextCell) {
            // remove snake tail
            Cell snakeTail = mSnake.get().removeLast();
            snakeTail.setStyle(Cell.Style.EMPTY);
            // add snake head
            mSnake.get().addFirst(nextCell);
            nextCell.setStyle(Cell.Style.SNAKE);
        }

        private boolean growSnake(Cell fruitCell) {
            // add snake head cause by eating fruit
            mSnake.get().addFirst(fruitCell);
            mSnake.setColor(mFruit.getColor());
            fruitCell.setStyle(Cell.Style.SNAKE);
            return growFruit();
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

        private List<Cell> cells(Grid grid, Cell.Style style) {
            List<Cell> resultCells = new ArrayList<>();
            Cell[][] cells = grid.cells();
            int row = grid.row();
            int column = grid.column();
            Cell cell;
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    cell = cells[i][j];
                    if (cell.getStyle() == style) {
                        resultCells.add(cell);
                    }
                }
            }
            return resultCells;
        }
    }
}
