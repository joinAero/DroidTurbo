package cc.eevee.turbo.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.File;

import cc.eevee.turbo.core.util.Log;

/**
 * Log receiver to control the log state.
 *
 * <pre>
 *     <receiver android:name="cc.eevee.turbo.core.receiver.LogReceiver"
 *         android:exported="true">
 *         <intent-filter>
 *             <action android:name="cc.cubone.action.LOG_ON" />
 *             <action android:name="cc.cubone.action.LOG_OFF" />
 *             <action android:name="cc.cubone.action.FILE_ON" />
 *             <action android:name="cc.cubone.action.FILE_OFF" />
 *         </intent-filter>
 *     </receiver>
 * </pre>
 */
public class LogReceiver extends BroadcastReceiver {

    public static final String ACTION_LOG_ON    = "cc.cubone.action.LOG_ON";
    public static final String ACTION_LOG_OFF   = "cc.cubone.action.LOG_OFF";
    public static final String ACTION_FILE_ON   = "cc.cubone.action.FILE_ON";
    public static final String ACTION_FILE_OFF  = "cc.cubone.action.FILE_OFF";

    public static final String EXTRA_PATH   = "cc.cubone.extra.PATH";
    public static final String EXTRA_LEVEL  = "cc.cubone.extra.LEVEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) { return; }
        final String action = intent.getAction();
        if (action == null) { return; }
        android.util.Log.i("onReceive", "action: " + action);

        if (action.equals(ACTION_LOG_ON)) {
            Log.setLog(true);
        } else if (action.equals(ACTION_LOG_OFF)) {
            Log.setLog(false);
        } else if (action.equals(ACTION_FILE_ON)) {
            final Log.FileChannel fileChannel = new Log.FileChannel(context);
            final String path = intent.getStringExtra(EXTRA_PATH);
            if (path != null) {
                fileChannel.setLogPath(new File(path));
            }
            final int level = intent.getIntExtra(EXTRA_LEVEL, Log.INFO);
            fileChannel.setLogLevel(level);
            Log.addChannel(fileChannel);
        } else if (action.equals(ACTION_FILE_OFF)) {
            Log.clearChannel();
        }
    }

}
