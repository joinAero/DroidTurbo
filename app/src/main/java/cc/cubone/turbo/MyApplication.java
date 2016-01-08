package cc.cubone.turbo;

import android.app.Application;
import android.os.StrictMode;

import cc.cubone.turbo.core.debug.CrashHandler;
import cc.cubone.turbo.core.util.Log;

import static cc.cubone.turbo.BuildConfig.DEBUG;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.setLog(DEBUG);
        if (DEBUG) {
            setupStrictMode();
        }
        new CrashHandler(this);
    }

    private void setupStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

}
