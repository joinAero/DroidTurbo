package cc.cubone.turbo.core.debug;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import cc.cubone.turbo.core.util.DirUtils;
import cc.cubone.turbo.core.util.ProcessUtils;

/**
 * Handles the crash.
 *
 * <p>Example code to handle application crash:
 * <pre>
 * public class MyApplication extends Application {
 *     @Override
 *     public void onCreate() {
 *         super.onCreate();
 *         new CrashHandler(this);
 *     }
 * }
 * </pre>
 *
 * <p>Requires the {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE}.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    /**
     * Handles the crash by user.
     */
    public static interface OnCrashCallback {
        /**
         * Does something that user customized.
         * @param thread    the thread that has an uncaught exception
         * @param ex        the exception that was thrown
         * @param crashFile the file that stores crash info
         * @param handler   the current handler
         * @return true for killing process in {@link #mKillWaitTime}.
         *         Otherwise, handle exception by default handler.
         */
        boolean onHandle(Thread thread, Throwable ex,
                         File crashFile, CrashHandler handler);
    }

    static final String CRASH_PREFIX = "crash";

    static final String VERSION_NAME = "VERSION_NAME";
    static final String VERSION_CODE = "VERSION_CODE";
    static final String STACK_TRACE = "STACK_TRACE";

    private Context mContext;
    private File mCrashPath;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private boolean mIsMainProcess = true;
    private OnCrashCallback mCrashCallback;

    private long mKillWaitTime = 3000;  // 3s

    public CrashHandler(Context context) {
        this(context, new File(DirUtils.getExternalPackageDir(context), "crash"));
    }

    public CrashHandler(Context context, File crashPath) {
        mContext = context;
        setCrashPath(crashPath);

        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * Sets the path that the crash info files be saved to.
     */
    public CrashHandler setCrashPath(File crashPath) {
        mCrashPath = crashPath;
        return this;
    }

    /**
     * Sets the crash callback for the user.
     */
    public CrashHandler setOnCrashCallback(OnCrashCallback callback) {
        mCrashCallback = callback;
        return this;
    }

    /**
     * Sets kill wait time in millis.
     */
    public CrashHandler setKillWaitTime(long time) {
        mKillWaitTime = time;
        return this;
    }

    /**
     * Gets all crash files in the crash path.
     */
    public File[] getCrashFiles() {
        if (mCrashPath != null && mCrashPath.isDirectory()) {
            return mCrashPath.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.startsWith(CRASH_PREFIX);
                }
            });
        }
        return null;
    }

    /**
     * Whether the process is main or not.
     */
    public boolean isMainProcess() {
        return mIsMainProcess;
    }

    private String crashFilePrefix() {
        String prefix = CRASH_PREFIX;
        String suffix = ProcessUtils.procNameSuffix(mContext);
        if (suffix != null) {
            prefix += '_' + suffix;
            mIsMainProcess = false;
        }
        mIsMainProcess = true;
        return prefix;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(thread, ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(mKillWaitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(2);
        }
    }

    private boolean handleException(Thread thread, Throwable ex) {
        if (ex == null) {
            return false;
        }
        final File crashFile = saveCrashInfo(ex);
        if (mCrashCallback != null) {
            return mCrashCallback.onHandle(thread, ex, crashFile, this);
        }
        return false;
    }

    private File saveCrashInfo(Throwable ex) {
        Properties crashInfo = new Properties();
        collectCrashDeviceInfo(mContext, crashInfo);
        collectCrashCauseInfo(ex, crashInfo);
        return saveCrashInfoToFile(crashInfo);
    }

    public void collectCrashDeviceInfo(Context context, Properties crashInfo) {
        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pkgInfo != null) {
                crashInfo.put(VERSION_CODE, String.valueOf(pkgInfo.versionCode));
                crashInfo.put(VERSION_NAME, pkgInfo.versionName == null ? "null" : pkgInfo.versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                crashInfo.put(field.getName(), String.valueOf(field.get(null)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void collectCrashCauseInfo(Throwable ex, Properties crashInfo) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        printWriter.flush();
        printWriter.close();
        crashInfo.put(STACK_TRACE, info.toString());
    }

    private File saveCrashInfoToFile(Properties crashInfo) {
        File crashFile = null;
        FileOutputStream fos = null;
        try {
            crashFile = createTempFile();
            fos = new FileOutputStream(crashFile);
            crashInfo.store(fos, "");
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
        return crashFile;
    }

    private File createTempFile() throws IOException {
        File dir = mCrashPath;
        if (dir != null) {
            dir.mkdirs();
            if (!dir.isDirectory()) {
                throw new IOException("Unable to create directories of " + dir);
            }
        }
        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
        return new File(dir, crashFilePrefix() + '_' + timeStamp);
    }

}
