package cc.cubone.turbo.core.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * The bluetooth listener for receiving the state changed.
 *
 * <p>Requires {@link android.Manifest.permission#BLUETOOTH} to receive.
 */
public class BluetoothListener {

    private Context mContext;
    private BluetoothCallback mCallback;
    private BluetoothReceiver mReceiver;

    public BluetoothListener(Context context) {
        mContext = context;
    }

    /**
     * Register this bluetooth listener to the context with a callback.
     */
    public void register(BluetoothCallback callback) {
        mCallback = callback;

        if (mReceiver == null) {
            mReceiver = new BluetoothReceiver();
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            mContext.registerReceiver(mReceiver, filter);
        }
    }

    /**
     * Unregister this bluetooth listener from the context.
     */
    public void unregister() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    /**
     * Receives the {@link android.bluetooth.BluetoothAdapter#ACTION_STATE_CHANGED}.
     */
    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    mCallback.onBluetoothOff();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    mCallback.onBluetoothTurningOn();
                    break;
                case BluetoothAdapter.STATE_ON:
                    mCallback.onBluetoothOn();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    mCallback.onBluetoothTurningOff();
                    break;
            }
        }
    }

}
