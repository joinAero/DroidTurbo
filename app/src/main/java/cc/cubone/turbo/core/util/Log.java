package cc.cubone.turbo.core.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Log class with multiple channels.
 *
 * <p>Example code to add file channel:
 * <pre>
 * Log.setLog(BuildConfig.DEBUG);
 * if (Log.LOG_ON) {
 *     FileChannel fileChannel = new FileChannel(this);
 *     Log.i(TAG, "Log to file: " + fileChannel.getLogFile());
 *     Log.addChannel(fileChannel);
 * }
 * </pre>
 */
public final class Log {

    public static final int NONE = -1;
    public static final int VERBOSE = android.util.Log.VERBOSE;
    public static final int DEBUG = android.util.Log.DEBUG;
    public static final int INFO = android.util.Log.INFO;
    public static final int WARN = android.util.Log.WARN;
    public static final int ERROR = android.util.Log.ERROR;
    public static final int ASSERT = android.util.Log.ASSERT;

    /**
     * Whether enable the log or not.
     */
    public static boolean LOG_ON = true;

    private static final LogChannel mHeadChannel;
    private static LogChannel mTailChannel;
    static {
        mHeadChannel = new ConsoleChannel();
        mTailChannel = mHeadChannel;
    }

    public static void setLog(boolean visible) {
        LOG_ON = visible;
    }

    public static void addChannel(LogChannel channel) {
        mTailChannel.setNext(channel);
        mTailChannel = channel;
    }

    public static void removeChannel(LogChannel channel) {
        LogChannel curr = mHeadChannel;
        LogChannel next = curr.getNext();
        while (next != null) {
            if (next == channel) {
                next = next.getNext();
                curr.setNext(next);
            } else {
                curr = next;
                next = next.getNext();
            }
        }
        mTailChannel = curr;
    }

    public static void clearChannel() {
        mHeadChannel.setNext(null);
        mTailChannel = mHeadChannel;
    }

    public static String getStackTraceString(Throwable tr) {
        return android.util.Log.getStackTraceString(tr);
    }

    public static void println(int priority, String tag, String msg, Throwable tr) {
        if (LOG_ON) {
            mHeadChannel.println(priority, tag, msg, tr);
        }
    }

    public static void println(int priority, String tag, String msg) {
        println(priority, tag, msg, null);
    }

    /**
     * Traces the class method which called this.
     * @param priority Log level of the data being logged. Verbose, Error, etc.
     */
    public static void trace(int priority) {
        if (!LOG_ON) {
            return;
        }
        boolean sawLogger = false;
        final String logClassName = Log.class.getName();
        for (StackTraceElement element : new Throwable().getStackTrace()) {
            String current = element.getClassName();
            if (current.startsWith(logClassName)) {
                sawLogger = true;
            } else if (sawLogger) {
                println(priority, element.getClassName(), element.getMethodName());
                break;
            }
        }
    }

    public static void v() {
        trace(VERBOSE);
    }

    public static void d() {
        trace(DEBUG);
    }

    public static void i() {
        trace(INFO);
    }

    public static void w() {
        trace(WARN);
    }

    public static void e() {
        trace(ERROR);
    }

    public static void wtf() {
        trace(ASSERT);
    }

    public static void v(String tag, String msg, Throwable tr) {
        println(VERBOSE, tag, msg, tr);
    }

    public static void v(String tag, String msg) {
        v(tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable tr) {
        println(DEBUG, tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable tr) {
        println(INFO, tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable tr) {
        println(WARN, tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    public static void w(String tag, Throwable tr) {
        w(tag, null, tr);
    }

    public static void e(String tag, String msg, Throwable tr) {
        println(ERROR, tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        println(ASSERT, tag, msg, tr);
    }

    public static void wtf(String tag, String msg) {
        wtf(tag, msg, null);
    }

    public static void wtf(String tag, Throwable tr) {
        wtf(tag, null, tr);
    }

    //------------------------------------------------------------------------------

    /**
     * The log channel for being used to output.
     */
    public static abstract class LogChannel {

        protected LogChannel mNext;

        public LogChannel getNext() {
            return mNext;
        }

        public void setNext(LogChannel next) {
            mNext = next;
        }

        public void println(int priority, String tag, String msg, Throwable tr) {
            String useMsg = msg;
            if (useMsg == null) {
                useMsg = "";
            }

            if (tr != null) {
                msg += '\n' + Log.getStackTraceString(tr);
            }

            println(priority, tag, useMsg);

            // If this isn't the last node in the chain, move things along.
            if (mNext != null) {
                mNext.println(priority, tag, msg, tr);
            }
        }

        public abstract void println(int priority, String tag, String msg);

    }

    /**
     * The default log channel which wraps Android's native Log utility.
     */
    static final class ConsoleChannel extends LogChannel {

        @Override
        public void println(int priority, String tag, String msg) {
            android.util.Log.println(priority, tag, msg);
        }

    }

    /**
     * The file log channel for outputting to a file.
     *
     * <p>Write external path requires the {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE}
     * permission.
     */
    public static final class FileChannel extends LogChannel {

        static final String LOG_NAME = "log";

        static final SimpleDateFormat mDateFormat;
        static final HashMap<Integer, Character> mLogToken;
        static {
            mDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
            mLogToken = new HashMap<>();
            mLogToken.put(VERBOSE, 'V');
            mLogToken.put(DEBUG, 'D');
            mLogToken.put(INFO, 'I');
            mLogToken.put(WARN, 'W');
            mLogToken.put(ERROR, 'E');
            mLogToken.put(ASSERT, 'A');
        }

        private File mLogFile;
        private int mLogLevel = INFO;

        public FileChannel(Context context) {
            // Default filepath for example: /storage/emulated/0/.cc.cubone.example
            this(context, new File(Environment.getExternalStorageDirectory(),
                    '.' + context.getPackageName()));
        }

        public FileChannel(Context context, File logPath) {
            setLogFile(new File(logPath, logName(context)));
        }

        public FileChannel(File logFile) {
            setLogFile(logFile);
        }

        /**
         * Sets the log file that you wanna save as.
         */
        public FileChannel setLogFile(File logFile) {
            if (logFile.exists() && !logFile.isFile()) {
                throw new IllegalArgumentException(logFile + " is not a file.");
            }
            final File parent = logFile.getParentFile();
            if (parent != null) {
                //noinspection ResultOfMethodCallIgnored
                parent.mkdirs();
            }
            mLogFile = logFile;
            return this;
        }

        /**
         * Sets the log path where you wanna save the log file.
         */
        public FileChannel setLogPath(File logPath) {
            setLogFile(new File(logPath, mLogFile.getName()));
            return this;
        }

        /**
         * Sets the minimum log level that allows to write, INFO by default.
         * @param priority Log level of the data being logged. Verbose, Error, etc.
         */
        public FileChannel setLogLevel(int priority) {
            mLogLevel = priority;
            return this;
        }

        private String logName(Context context) {
            String suffix = ProcessUtils.procNameSuffix(context);
            return suffix == null ? LOG_NAME : LOG_NAME + '_' + suffix;
        }

        public File getLogFile() {
            return mLogFile;
        }

        public int getLogLevel() {
            return mLogLevel;
        }

        /**
         * Log a message to the log file
         */
        private synchronized void println(String msg) {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(mLogFile, true));
                writer.println(msg);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void println(int priority, String tag, String msg) {
            if (priority < mLogLevel) {
                return;
            }
            Character token = mLogToken.get(priority);
            if (token == null) {
                token = '?';
            }
            println(String.format("%s  %s/%s: %s", mDateFormat.format(new Date()), token, tag, msg));
        }

    }

}
