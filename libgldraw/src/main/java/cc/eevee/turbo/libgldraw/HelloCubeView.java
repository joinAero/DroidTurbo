package cc.eevee.turbo.libgldraw;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HelloCubeView extends GLSurfaceView {

    static {
        System.loadLibrary("hellocube");
    }

    HelloCubeRenderer mRenderer;

    public HelloCubeView(Context context) {
        super(context);
        initGLView();
    }

    public HelloCubeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGLView();
    }

    private void initGLView() {
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new HelloCubeRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onResume() {
        super.onResume();
        nativeResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        nativePause();
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, we are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                nativeTouchRotate((dx + dy) * TOUCH_SCALE_FACTOR);  // = 180.0f / 320
                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

    private static native void nativeResume();
    private static native void nativePause();
    private static native void nativeTouchRotate(float angle);
}
