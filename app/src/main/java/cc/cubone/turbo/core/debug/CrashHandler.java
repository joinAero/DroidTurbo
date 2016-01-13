package cc.cubone.turbo.core.debug;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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
 * <p>Requires {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE} to
 * write to external storage.
 *
 * @see android.Manifest.permission#WRITE_EXTERNAL_STORAGE
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    /**
     * Handles the crash by user.
     */
    public interface OnCrashCallback {
        /**
         * Does something that user customized.
         *
         * @param thread    The thread that has an uncaught exception
         * @param ex        The exception that was thrown
         * @param crashFile The file that stores crash info, maybe null
         * @param handler   The current handler
         * @return True for killing process in {@link #mKillWaitTime}.
         *         Otherwise, handle exception by default handler.
         */
        boolean onHandle(Thread thread, Throwable ex,
                         File crashFile, CrashHandler handler);
    }

    static final String CRASH_PREFIX = "crash";

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
        File crashFile = null;
        Writer writer = null;
        try {
            crashFile = createTempFile();
            writer = new OutputStreamWriter(new FileOutputStream(crashFile));

            StringBuilder buffer = new StringBuilder(200);

            writeCrashVersionInfo(mContext, writer, buffer);
            writer.write("\nSTACK_TRACE=\n");
            writeCrashCauseInfo(ex, writer);

            writer.write('\n');
            write(writer, buffer, Build.class);
            writer.write('\n');
            write(writer, buffer, Build.VERSION.class);

            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
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
        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmssSSS")).format(new Date());
        return new File(dir, crashFilePrefix() + '_' + timeStamp);
    }

    private void writeCrashVersionInfo(Context context, Writer writer, StringBuilder buffer)
            throws IOException {
        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pkgInfo != null) {
                write(writer, buffer, "VERSION_CODE", String.valueOf(pkgInfo.versionCode));
                write(writer, buffer, "VERSION_NAME", pkgInfo.versionName == null
                        ? "null" : pkgInfo.versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeCrashCauseInfo(Throwable ex, Writer writer)
            throws IOException {
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        printWriter.flush();
    }

    private void write(Writer writer, StringBuilder buffer, Class<?> cls)
            throws IOException {
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                write(writer, buffer, field.getName(), toString(field.get(null)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void write(Writer writer, StringBuilder buffer, String key, String value)
            throws IOException {
        buffer.append(key);
        buffer.append('=');
        buffer.append(value);
        buffer.append('\n');
        writer.write(buffer.toString());
        buffer.setLength(0);
    }

    private String toString(Object object) {
        if (object instanceof Object[]) {
            return Arrays.toString((Object[]) object);
        }
        return String.valueOf(object);
    }

}
