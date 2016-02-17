package cc.cubone.turbo.core.rom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;

/**
 * Helper for accessing custom features for 3rd party roms.
 *
 * @see <a href="https://github.com/weikano/RomHelper">RomHelper</a>
 */
public final class RomCompat {

    interface RomCompatImpl {
        public String getVersionName();
        public boolean isThirdParty();
        public boolean isAutoStart();
        public boolean hasPermissionManager();
        public boolean hasFloatWindowPermission(Context context);
        public Intent toAppDetails(Context context, String packageName);
        public Intent toPermissionManager(Context context, String packageName);
        public Intent toAutoStartPermission(Context context, String packageName);
        public Intent toFloatWindowPermission(Context context, String packageName);
        public Intent toRootPermission(Context context, String packageName);
    }

    static class BaseRomCompatImpl implements RomCompatImpl {
        @Override
        public String getVersionName() {
            return String.valueOf(Build.VERSION.SDK_INT);
        }

        @Override
        public boolean isThirdParty() {
            return false;
        }

        @Override
        public boolean isAutoStart() {
            return true;
        }

        @Override
        public boolean hasPermissionManager() {
            return Build.VERSION.SDK_INT >= 23; // 23, 6.0, MARSHMALLOW
        }

        @Override
        public boolean hasFloatWindowPermission(Context context) {
            return ContextCompat.checkSelfPermission(context, SYSTEM_ALERT_WINDOW)
                    == PackageManager.PERMISSION_GRANTED;
        }

        @Override
        public Intent toAppDetails(Context context, String packageName) {
            Uri packageUri = Uri.parse("package:" + packageName);
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            return intent;
        }

        @Override
        public Intent toPermissionManager(Context context, String packageName) {
            return null;
        }

        @Override
        public Intent toAutoStartPermission(Context context, String packageName) {
            return null;
        }

        @Override
        public Intent toFloatWindowPermission(Context context, String packageName) {
            return null;
        }

        @Override
        public Intent toRootPermission(Context context, String packageName) {
            return null;
        }
    }

    @TargetApi(23) // 23, 6.0, MARSHMALLOW
    static class MarshmallowRomCompatImpl extends BaseRomCompatImpl {
        @Override
        public boolean hasFloatWindowPermission(Context context) {
            return Settings.canDrawOverlays(context);
        }

        @Override
        public Intent toFloatWindowPermission(Context context, String packageName) {
            return new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + packageName));
        }
    }

    static class ThirdPartyRomCompatImpl extends BaseRomCompatImpl {
        @Override
        public boolean isThirdParty() {
            return true;
        }

        @Override
        public boolean hasPermissionManager() {
            return true;
        }
    }

    static class FlymeRomCompatImpl extends ThirdPartyRomCompatImpl {
        @Override
        public Intent toPermissionManager(Context context, String packageName) {
            return toAppDetails(context, packageName);
        }

        @Override
        public Intent toAutoStartPermission(Context context, String packageName) {
            return toAppDetails(context, packageName);
        }

        @Override
        public Intent toFloatWindowPermission(Context context, String packageName) {
            return toAppDetails(context, packageName);
        }

        @Override
        public Intent toRootPermission(Context context, String packageName) {
            return null;
        }
    }

    static class MIUIRomCompatImpl extends ThirdPartyRomCompatImpl {
        @Override
        public String getVersionName() {
            return MIUIUtils.getVersionName();
        }

        @Override
        public Intent toPermissionManager(Context context, String packageName) {
            return MIUIUtils.toPermissionManager(context, packageName);
        }

        @Override
        public Intent toAutoStartPermission(Context context, String packageName) {
            return MIUIUtils.toAutoStartPermission();
        }

        @Override
        public Intent toFloatWindowPermission(Context context, String packageName) {
            return MIUIUtils.toFloatWindowPermission(context, packageName);
        }

        @Override
        public Intent toRootPermission(Context context, String packageName) {
            return MIUIUtils.toRootPermission();
        }
    }

    static final RomCompatImpl IMPL;
    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 23) { // 23, 6.0, MARSHMALLOW
            IMPL = new MarshmallowRomCompatImpl();
        } else if (FlymeUtils.isFlyme()) {
            IMPL = new FlymeRomCompatImpl();
        } else if (MIUIUtils.isMIUI()) {
            IMPL = new MIUIRomCompatImpl();
        } else {
            IMPL = new BaseRomCompatImpl();
        }
    }

    public static String getVersionName() {
        return IMPL.getVersionName();
    }

    public static boolean isThirdParty() {
        return IMPL.isThirdParty();
    }

    public static boolean isAutoStart() {
        return IMPL.isAutoStart();
    }

    public static boolean hasPermissionManager() {
        return IMPL.hasPermissionManager();
    }

    public static boolean hasFloatWindowPermission(Context context) {
        return IMPL.hasFloatWindowPermission(context);
    }

    public static Intent toAppDetails(Context context) {
        return toAppDetails(context, context.getPackageName());
    }

    public static Intent toAppDetails(Context context, String packageName) {
        return IMPL.toAppDetails(context, packageName);
    }

    public static Intent toPermissionManager(Context context) {
        return toPermissionManager(context, context.getPackageName());
    }

    public static Intent toPermissionManager(Context context, String packageName) {
        return IMPL.toPermissionManager(context, packageName);
    }

    public static Intent toAutoStartPermission(Context context) {
        return toAutoStartPermission(context, context.getPackageName());
    }

    public static Intent toAutoStartPermission(Context context, String packageName) {
        return IMPL.toAutoStartPermission(context, packageName);
    }

    public static Intent toFloatWindowPermission(Context context) {
        return toFloatWindowPermission(context, context.getPackageName());
    }

    public static Intent toFloatWindowPermission(Context context, String packageName) {
        return IMPL.toFloatWindowPermission(context, packageName);
    }

    public static Intent toRootPermission(Context context) {
        return toRootPermission(context, context.getPackageName());
    }

    public static Intent toRootPermission(Context context, String packageName) {
        return IMPL.toRootPermission(context, packageName);
    }
}
