package cc.cubone.turbo.core.rom;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

import java.io.IOException;

import cc.cubone.turbo.core.os.BuildProperties;

/**
 * @see <a href="http://dev.xiaomi.com/doc/?p=254">如何识别小米设备/MIUI系统</a>
 * @see <a href="http://www.cnblogs.com/fangyucun/p/4027750.html">关于MIUI悬浮窗权限问题的解决方案</a>
 * @see <a href="http://www.cnblogs.com/niray/p/5153530.html">Android 沉浸式状态栏</a>
 */
public class MIUIUtils {

    public static final String MIUI_V5 = "V5";
    public static final String MIUI_V6 = "V6";

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    public static boolean isMIUI() {
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isMIUIV5() {
        return getVersionName().equals(MIUI_V5);
    }

    public static boolean isMIUIV6() {
        return getVersionName().equals(MIUI_V6);
    }

    public static String getVersionName() {
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_NAME);
        } catch (IOException e) {
            return "";
        }
    }

    @SuppressWarnings("IncompatibleBitwiseMaskOperation")
    public static boolean isFloatWindowOpAllowed(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            final int mode = manager.checkOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                    Binder.getCallingUid(), context.getPackageName());
            return AppOpsManager.MODE_ALLOWED == mode;
        } else {
            return (context.getApplicationInfo().flags & 1 << 27) == 1;
        }
    }

    public static Intent toPermissionManager(Context context, String packageName) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        String version = getVersionName();
        if (MIUI_V5.equals(version)) {
            PackageInfo pInfo;
            try {
                pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException ignored) {
                return null;
            }
            intent.setClassName("com.android.settings", "com.miui.securitycenter.permission.AppPermissionsEditor");
            intent.putExtra("extra_package_uid", pInfo.applicationInfo.uid);
        } else { // MIUI_V6 and above
            final String PKG_SECURITY_CENTER = "com.miui.securitycenter";
            try {
                context.getPackageManager().getPackageInfo(PKG_SECURITY_CENTER, PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException ignored) {
                return null;
            }
            intent.setClassName(PKG_SECURITY_CENTER, "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", packageName);
        }
        return intent;
    }

    public static Intent toAutoStartPermission() {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.OP_AUTO_START");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        return intent;
    }

    /**
     * Note: V5的悬浮窗权限在应用详情里面
     */
    public static Intent toFloatWindowPermission(Context context, String packageName) {
        Uri packageUri = Uri.parse("package:" + packageName);
        Intent detailsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri);
        detailsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        if (isMIUIV5()) {
            return detailsIntent;
        } else {
            Intent permIntent = toPermissionManager(context, packageName);
            return permIntent == null ? detailsIntent : permIntent;
        }
    }

    public static Intent toRootPermission() {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.ROOT_MANAGER");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        return intent;
    }
}
