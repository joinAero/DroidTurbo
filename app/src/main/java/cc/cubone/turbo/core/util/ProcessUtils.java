package cc.cubone.turbo.core.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

import java.util.List;

/**
 * Utility class to deal with android process.
 */
public class ProcessUtils {

    /**
     * Gets all running services.
     */
    public static List<ActivityManager.RunningServiceInfo> getRunningServices(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningServices(Integer.MAX_VALUE);
    }

    /**
     * Whether the service is running or not.
     *
     * @param context The context.
     * @param service The service class.
     */
    public static boolean isServiceRunning(Context context, Class<?> service) {
        List<ActivityManager.RunningServiceInfo> servList = getRunningServices(context);
        final String servClsName = service.getName();
        for (ActivityManager.RunningServiceInfo servInfo : servList) {
            if (servClsName.equals(servInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //------------------------------------------------------------------------------

    /**
     * Gets all running processes.
     */
    public static List<RunningAppProcessInfo> getRunningProcesses(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses();
    }

    /**
     * Returns the identifier of this process.
     * @see {@link android.os.Process#myPid()}
     */
    public static int pid() {
        return android.os.Process.myPid();
    }

    /**
     * Returns the info of this process, or null if not found.
     */
    public static RunningAppProcessInfo procInfo(Context context) {
        final int pid = pid();
        for (RunningAppProcessInfo info : getRunningProcesses(context)) {
            if (info.pid == pid) {
                return info;
            }
        }
        return null;
    }

    /**
     * Returns the name of this process, or null if not found.
     */
    public static String procName(Context context) {
        final RunningAppProcessInfo info = procInfo(context);
        return info == null ? null : info.processName;
    }

    /**
     * Returns the suffix of process name, or null if it's the main process or not found.
     */
    public static String procNameSuffix(Context context) {
        String pkgName = context.getPackageName();
        String procName = procName(context);
        int pkgNameLen = pkgName.length();
        if (procName != null && procName.length() > pkgNameLen
                && procName.startsWith(pkgName)
                && ':' == procName.charAt(pkgNameLen)) {
            return procName.substring(pkgNameLen + 1);
        }
        return null;
    }

}
