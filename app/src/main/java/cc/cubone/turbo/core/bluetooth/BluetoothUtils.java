package cc.cubone.turbo.core.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresPermission;

import static android.content.pm.PackageManager.FEATURE_BLUETOOTH_LE;

/**
 * Utility for detecting the bluetooth state.
 */
public class BluetoothUtils {

    /**
     * Whether bluetooth is supported on the device.
     */
    public static boolean isAvailable() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    /**
     * Whether BLE is supported on the device.
     */
    public static boolean isLeAvailable(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 // >= 18
            && context.getPackageManager().hasSystemFeature(FEATURE_BLUETOOTH_LE);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static boolean isEnabled() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter != null && adapter.isEnabled();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static void requestEnable(Context context) {
        context.startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static void requestEnable(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), requestCode);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static boolean enable() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter != null && adapter.enable();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static boolean disable() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter != null && adapter.disable();
    }
}
