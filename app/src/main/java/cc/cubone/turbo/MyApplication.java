package cc.cubone.turbo;

import android.app.Application;

import cc.cubone.turbo.core.debug.CrashHandler;
import cc.cubone.turbo.core.util.Log;

import static cc.cubone.turbo.BuildConfig.DEBUG;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.setLog(DEBUG);
        new CrashHandler(this);
    }

}
