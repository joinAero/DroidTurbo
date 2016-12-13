package cc.eevee.turbo.core.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TokenActivity extends AppCompatActivity {

    private boolean mAttached;

    /*public boolean isAttached() {
        return mAttached;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        tryResumeUnderAttached();
    }

    /**
     * To avoid {@link android.view.WindowManager.BadTokenException}, should show
     * {@link android.widget.PopupWindow}, {@link android.app.Dialog} in this method.
     * Must not show them in {@link #onCreate(Bundle)}, {@link #onResume()} directly.
     *
     * <p>It is also recommended that register the {@link android.content.BroadcastReceiver}
     * in this method and unregister it in {@link #onDetachedFromWindow()}.
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttached = true;
        tryResumeUnderAttached();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttached = false;
    }

    private void tryResumeUnderAttached() {
        if (mAttached) {
            onResumeUnderAttached();
        }
    }

    /**
     * Ensure {@link #onResume()} under the window has been attached.
     */
    protected void onResumeUnderAttached() {
    }

}
