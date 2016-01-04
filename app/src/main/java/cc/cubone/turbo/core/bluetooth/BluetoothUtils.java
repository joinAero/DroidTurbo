package cc.cubone.turbo.core.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

import static android.content.pm.PackageManager.FEATURE_BLUETOOTH_LE;

/**
 * Utility for detecting the bluetooth state.
 */
public class BluetoothUtils {

    /**
     * Gets the bluetooth manager.
     *
     * <p>Requires the {@link android.Manifest.permission#BLUETOOTH}.
     */
    public static BluetoothManager getBluetoothManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) { // >= 18
            return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        return null;
    }

    /**
     * Gets the bluetooth adapter.
     *
     * @return the default local adapter, or null if Bluetooth is not supported
     *         on this hardware platform
     */
    @SuppressLint("NewApi")
    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        BluetoothManager btManager = getBluetoothManager(context);
        if (btManager == null) {
            return BluetoothAdapter.getDefaultAdapter();
        } else { // >= 18
            return btManager.getAdapter();
        }
    }

    /**
     * Whether BLE is supported on the device.
     */
    public static boolean bluetoothLeAvailable(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 // >= 18
                && context.getPackageManager().hasSystemFeature(FEATURE_BLUETOOTH_LE);
    }

    /**
     * Whether bluetooth is supported on the device.
     */
    public static boolean bluetoothAvailable(Context context) {
        return getBluetoothAdapter(context) != null;
    }

    /**
     * Whether bluetooth is currently enabled and ready for use.
     *
     * <p>Requires the {@link android.Manifest.permission#BLUETOOTH} permission.
     *
     * @return true if enabled, false if bluetooth is not supported or turned on.
     */
    public static boolean bluetoothEnabled(Context context) {
        return bluetoothEnabled(getBluetoothAdapter(context));
    }

    public static boolean bluetoothEnabled(BluetoothAdapter btAdapter) {
        return btAdapter != null && btAdapter.isEnabled();
    }

    /**
     * Get the current state of the bluetooth.
     *
     * <p>Requires the {@link android.Manifest.permission#BLUETOOTH} permission.
     *
     * Possible return values are
     * {@link android.bluetooth.BluetoothAdapter#STATE_OFF},
     * {@link android.bluetooth.BluetoothAdapter#STATE_TURNING_ON},
     * {@link android.bluetooth.BluetoothAdapter#STATE_ON},
     * {@link android.bluetooth.BluetoothAdapter#STATE_TURNING_OFF}.
     */
    public static int bluetoothState(Context context) {
        return bluetoothState(getBluetoothAdapter(context));
    }

    public static int bluetoothState(BluetoothAdapter btAdapter) {
        return btAdapter == null ? BluetoothAdapter.STATE_OFF : btAdapter.getState();
    }

}
