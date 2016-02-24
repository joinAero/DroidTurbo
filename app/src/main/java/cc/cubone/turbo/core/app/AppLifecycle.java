package cc.cubone.turbo.core.app;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * App Lifecycle.
 *
 * @see <a href="http://steveliles.github.io/is_my_android_app_currently_foreground_or_background.html">
 *     Is app foreground ...</a>
 * @see <a href="http://stackoverflow.com/questions/4414171/how-to-detect-when-an-android-app-goes-to-the-background-and-come-back-to-the-fo">
 *     how to detect background ...</a>
 */
public class AppLifecycle implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private boolean mAppBackground = true;

    public AppLifecycle(Application app) {
        app.registerActivityLifecycleCallbacks(this);
        app.registerComponentCallbacks(this);
    }

    public boolean isAppForeground() {
        return !mAppBackground;
    }

    public boolean isAppBackground() {
        return mAppBackground;
    }

    public void onAppForeground() {
    }

    public void onAppBackground() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (mAppBackground) {
            mAppBackground = false;
            onAppForeground();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onTrimMemory(int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            mAppBackground = true;
            onAppBackground();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override
    public void onLowMemory() {
    }

}
