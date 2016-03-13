package cc.cubone.turbo.ui.demo.snake.game;

import android.view.MotionEvent;

public class Gesture {

    public static final int MOVE_LEFT   = 0;
    public static final int MOVE_UP     = 1;
    public static final int MOVE_RIGHT  = 2;
    public static final int MOVE_DOWN   = 3;

    /*public*/ static final String[] MOVE_NAMES = new String[] {
            "Move Left", "Move Up", "Move Right", "Move Down",
    };

    private Callback mCallback;

    private float mDownX;
    private float mDownY;
    private float mMoveStartX;
    private float mMoveStartY;

    private final int mMoveSlopSquare;

    private boolean mMoved;

    public Gesture(int moveSlop, Callback callback) {
        if (callback == null) throw new IllegalArgumentException();
        mCallback = callback;
        mMoveSlopSquare = moveSlop * moveSlop;
    }

    public static String moveName(int direction) {
        return MOVE_NAMES[direction];
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
                                mCallback.onGestureMove(MOVE_LEFT);
                            } else {
                                mCallback.onGestureMove(MOVE_UP);
                            }
                        } else { // deltaY >= 0
                            if (-deltaX > deltaY) {
                                mCallback.onGestureMove(MOVE_LEFT);
                            } else {
                                mCallback.onGestureMove(MOVE_DOWN);
                            }
                        }
                    } else { // deltaX >= 0
                        if (deltaY < 0) {
                            if (deltaX > -deltaY) {
                                mCallback.onGestureMove(MOVE_RIGHT);
                            } else {
                                mCallback.onGestureMove(MOVE_UP);
                            }
                        } else { // deltaY >= 0
                            if (deltaX > deltaY) {
                                mCallback.onGestureMove(MOVE_RIGHT);
                            } else {
                                mCallback.onGestureMove(MOVE_DOWN);
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
        public void onGestureMove(int direction);
        public void onGestureUp(boolean moved);
    }

}
