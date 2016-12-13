package cc.eevee.turbo.libsanangeles;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SanAngelesGLSurfaceView extends GLSurfaceView {

    static {
        System.loadLibrary("sanangeles");
    }

    SanAngelesRenderer mRenderer;

    public SanAngelesGLSurfaceView(Context context) {
        super(context);
        initGLView();
    }

    public SanAngelesGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGLView();
    }

    private void initGLView() {
        mRenderer = new SanAngelesRenderer();
        setRenderer(mRenderer);
    }

    @Override
    public void onPause() {
        super.onPause();
        nativePause();
    }

    @Override
    public void onResume() {
        super.onResume();
        nativeResume();
    }

    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            nativeTogglePauseResume();
        }
        return true;
    }

    private static native void nativePause();
    private static native void nativeResume();
    private static native void nativeTogglePauseResume();
}
