package cc.cubone.turbo.core.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * The bluetooth profile proxy for operating the system bluetooth profile.
 *
 * @see android.bluetooth.BluetoothProfile
 * @see android.bluetooth.BluetoothAdapter
 * @see android.bluetooth.BluetoothManager
 */
public class BluetoothProfileProxy implements BluetoothProfile, BluetoothProfile.ServiceListener {

    private static final String TAG = "BluetoothProfileProxy";

    /** The profile is in unknown state */
    public static final int STATE_UNKNOWN  = -1;

    /** Input Device Profile */
    public static final int INPUT_DEVICE = 4;

    private Context mContext;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothProfile mBluetoothProfile;

    private BluetoothListener mBluetoothListener;
    private BluetoothCallback mBluetoothCallback;

    private int mProfile = -1;

    private boolean mRegistered = false;

    public BluetoothProfileProxy(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Register the proxy for {@link #INPUT_DEVICE} profile.
     *
     * @see #register(int, boolean)
     */
    public boolean register(boolean followBluetooth) {
        return register(INPUT_DEVICE, followBluetooth);
    }

    /**
     * Register the proxy for a bluetooth profile.
     *
     * @param profile The Bluetooth profile; either {@link #HEALTH}, {@link #HEADSET},
     *                {@link #A2DP}, {@link #GATT} or {@link #GATT_SERVER}.
     * @param followBluetooth Whether follow bluetooth state changed event or not. If follow, it
     *                        will auto register when the bluetooth turn on, unregister when turn
     *                        off.
     *
     * @see BluetoothAdapter#getProfileProxy(Context, ServiceListener, int)
     */
    public boolean register(int profile, boolean followBluetooth) {
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported");
            return false;
        }
        if (mRegistered) {
            Log.w(TAG, "BluetoothProfile has registered");
            return false;
        }
        mRegistered = mBluetoothAdapter.getProfileProxy(mContext, this, profile);
        if (mRegistered) {
            mProfile = profile;
        }
        if (followBluetooth) {
            mProfile = profile;
            followBluetooth();
        }
        return mRegistered;
    }

    /**
     * Unregister the proxy.
     */
    public void unregister() {
        unregister(true);
    }

    private void unregister(boolean formUser) {
        if (!mRegistered) return;
        mBluetoothAdapter.closeProfileProxy(mProfile, mBluetoothProfile);
        mBluetoothProfile = null;
        mRegistered = false;
        if (formUser) {
            unfollowBluetooth();
            mProfile = -1;
        }
    }

    /**
     * Enable follow bluetooth state changed event.
     *
     * <p>If follow, it will auto register when the bluetooth turn on, unregister when turn off.
     *
     * <p>Could {@link #setBluetoothCallback(BluetoothCallback)} to listen the bluetooth state
     * changed event.
     */
    public void followBluetooth() {
        if (mBluetoothListener != null) return;
        mBluetoothListener = new BluetoothListener(mContext);
        mBluetoothListener.register(new BluetoothCallback() {
            @Override
            public void onBluetoothOff() {
                unregister(false);
                if (mBluetoothCallback != null) mBluetoothCallback.onBluetoothOff();
            }
            @Override
            public void onBluetoothTurningOn() {
                if (mBluetoothCallback != null) mBluetoothCallback.onBluetoothTurningOn();
            }
            @Override
            public void onBluetoothOn() {
                register(mProfile, false);
                if (mBluetoothCallback != null) mBluetoothCallback.onBluetoothOn();
            }
            @Override
            public void onBluetoothTurningOff() {
                if (mBluetoothCallback != null) mBluetoothCallback.onBluetoothTurningOff();
            }
        });
    }

    /**
     * Disable follow bluetooth state changed event.
     */
    public void unfollowBluetooth() {
        if (mBluetoothListener == null) return;
        mBluetoothListener.unregister();
        mBluetoothListener = null;
    }

    /**
     * Set bluetooth callback to listen the bluetooth state changed event.
     *
     * <p>It only make effect when enable follow bluetooth.
     */
    public void setBluetoothCallback(BluetoothCallback callback) {
        mBluetoothCallback = callback;
    }

    /**
     * Whether registered or not.
     */
    public boolean isRegistered() {
        return mRegistered;
    }

    /**
     * Get the bluetooth profile.
     */
    public BluetoothProfile getBluetoothProfile() {
        return mBluetoothProfile;
    }

    /**
     * Whether the bluetooth profile is got or not.
     */
    public boolean hasBluetoothProfile() {
        return mBluetoothProfile != null;
    }

    private boolean checkNotBluetoothProfile() {
        if (mBluetoothProfile == null) {
            Log.w(TAG, "BluetoothProfile not connected");
            return true;
        }
        return false;
    }

    /**
     * Get connected devices for this specific profile.
     *
     * @see BluetoothProfile#getConnectedDevices()
     */
    public List<BluetoothDevice> getConnectedDevices() {
        if (checkNotBluetoothProfile()) return null;
        return mBluetoothProfile.getConnectedDevices();
    }

    /**
     * Get a list of devices that match any of the given connection states.
     *
     * @see BluetoothProfile#getDevicesMatchingConnectionStates(int[])
     */
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
        if (checkNotBluetoothProfile()) return null;
        return mBluetoothProfile.getDevicesMatchingConnectionStates(states);
    }

    /**
     * Get the current connection state of the profile.
     *
     * @see BluetoothProfile#getConnectionState(BluetoothDevice)
     */
    public int getConnectionState(BluetoothDevice device) {
        if (checkNotBluetoothProfile()) return STATE_UNKNOWN;
        return mBluetoothProfile.getConnectionState(device);
    }

    /**
     * Get the set of {@link BluetoothDevice} that are bonded (paired).
     *
     * <p>The bonded device may be connected, connecting or disconnect, however bonded at present.
     *
     * <p>Requires {@link android.Manifest.permission#BLUETOOTH}.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public Set<BluetoothDevice> getBondedDevices() {
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported");
            return null;
        }
        return mBluetoothAdapter.getBondedDevices();
    }

    /**
     * Connect the bluetooth device.
     */
    public boolean connect(BluetoothDevice device) {
        return invoke(mBluetoothProfile, "connect", device);
    }

    /**
     * Disconnect the bluetooth device.
     */
    public boolean disconnect(BluetoothDevice device) {
        return invoke(mBluetoothProfile, "disconnect", device);
    }

    /**
     * Create bond for the bluetooth device.
     */
    public boolean createBond(BluetoothDevice device) {
        return invoke(device, "createBond");
    }

    /**
     * Remove bond for the bluetooth device.
     */
    public boolean removeBond(BluetoothDevice device) {
        return invoke(device, "removeBond");
    }

    private boolean invoke(Object obj, String method, Object... args) {
        if (obj == null) return false;
        if (method == null || method.isEmpty()) return false;
        boolean ok = false;
        try {
            Class<?>[] paramTypes = null;
            if (args != null) {
                int len = args.length;
                paramTypes = new Class<?>[len];
                for (int i = 0; i < len; ++i) {
                    paramTypes[i] = args[i].getClass();
                }
            }
            Class<?> cls = obj.getClass();
            Method func = cls.getMethod(method, paramTypes);
            func.setAccessible(true);
            Object result = func.invoke(obj, args);
            try {
                ok = (Boolean) result;
            } catch (ClassCastException e) {
                ok = true;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return ok;
    }

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        mBluetoothProfile = proxy;
    }

    @Override
    public void onServiceDisconnected(int profile) {
        mBluetoothProfile = null;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            unregister(true);
        } finally {
            super.finalize();
        }
    }

}
