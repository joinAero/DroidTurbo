package cc.cubone.turbo.core.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;

import static android.content.pm.PackageManager.FEATURE_BLUETOOTH_LE;

/**
 * Utility for detecting the bluetooth state.
 */
public class BluetoothUtils {

    /**
     * Whether bluetooth is supported on the device.
     */
    public static boolean bluetoothAvailable() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    /**
     * Whether BLE is supported on the device.
     */
    public static boolean bluetoothLeAvailable(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 // >= 18
            && context.getPackageManager().hasSystemFeature(FEATURE_BLUETOOTH_LE);
    }

}
