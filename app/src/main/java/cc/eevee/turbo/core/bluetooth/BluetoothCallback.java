package cc.eevee.turbo.core.bluetooth;

/**
 * Interface definition for a callback to be invoked when bluetooth state changed.
 */
public interface BluetoothCallback {

    /**
     * Called when the bluetooth is off.
     */
    void onBluetoothOff();

    /**
     * Called when the bluetooth is turning on.
     */
    void onBluetoothTurningOn();

    /**
     * Called when the bluetooth is on, and ready for use.
     */
    void onBluetoothOn();

    /**
     * Called when the bluetooth is turning off.
     */
    void onBluetoothTurningOff();

    /**
     * This stub class provides empty implementations of the methods.
     */
    public static class Stub implements BluetoothCallback {
        @Override
        public void onBluetoothOff() {
        }
        @Override
        public void onBluetoothTurningOn() {
        }
        @Override
        public void onBluetoothOn() {
        }
        @Override
        public void onBluetoothTurningOff() {
        }
    }

}
