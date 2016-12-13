package cc.eevee.turbo.ui.demo.snake.engine.util;

import android.view.MotionEvent;

import cc.eevee.turbo.ui.demo.snake.engine.part.Direction;

public class Gesture {

    private Callback mCallback;

    private float mDownX;
    private float mDownY;
    private float mMoveStartX;
    private float mMoveStartY;

    private final int mMoveSlopSquare;

    private boolean mMoved;

    public Gesture(Callback callback) {
        this(20, callback); // 20px
    }

    public Gesture(int moveSlop, Callback callback) {
        if (callback == null) throw new IllegalArgumentException();
        mCallback = callback;
        mMoveSlopSquare = moveSlop * moveSlop;
    }

    public boolean onTouchEvent(MotionEvent e) {
        final int action = e.getAction();

        final float x = e.getX();
        final float y = e.getY();

        boolean handled = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = mMoveStartX = x;
                mDownY = mMoveStartY = y;
                mMoved = false;
                handled = mCallback.onGestureDown();
                break;
            case MotionEvent.ACTION_MOVE:
                final int deltaX = (int) (x - mMoveStartX);
                final int deltaY = (int) (y - mMoveStartY);
                int distance = (deltaX * deltaX) + (deltaY * deltaY);
                if (distance > mMoveSlopSquare) {
                    if (deltaX < 0) {
                        if (deltaY < 0) {
                            if (deltaX < deltaY) {
                                mCallback.onGestureMove(Direction.LEFT);
                            } else {
                                mCallback.onGestureMove(Direction.UP);
                            }
                        } else { // deltaY >= 0
                            if (-deltaX > deltaY) {
                                mCallback.onGestureMove(Direction.LEFT);
                            } else {
                                mCallback.onGestureMove(Direction.DOWN);
                            }
                        }
                    } else { // deltaX >= 0
                        if (deltaY < 0) {
                            if (deltaX > -deltaY) {
                                mCallback.onGestureMove(Direction.RIGHT);
                            } else {
                                mCallback.onGestureMove(Direction.UP);
                            }
                        } else { // deltaY >= 0
                            if (deltaX > deltaY) {
                                mCallback.onGestureMove(Direction.RIGHT);
                            } else {
                                mCallback.onGestureMove(Direction.DOWN);
                            }
                        }
                    }
                    mMoveStartX = x;
                    mMoveStartY = y;
                    mMoved = true;
                }
                handled = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mCallback.onGestureUp(mMoved);
                handled = true;
                break;
        }
        return handled;
    }

    public interface Callback {
        public boolean onGestureDown();
        public void onGestureMove(Direction direction);
        public void onGestureUp(boolean moved);
    }

}
