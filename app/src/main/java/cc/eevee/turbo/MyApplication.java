package cc.eevee.turbo;

import android.app.Application;
import android.os.StrictMode;

import cc.eevee.turbo.core.debug.CrashHandler;
import cc.eevee.turbo.core.util.Log;

import static cc.eevee.turbo.BuildConfig.DEBUG;

public class MyApplication extends Application {

    public static MyApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
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
