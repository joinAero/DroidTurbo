package cc.eevee.turbo.libgldraw;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

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

}
