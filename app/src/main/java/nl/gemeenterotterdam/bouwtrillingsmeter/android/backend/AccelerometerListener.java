package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * This interface is used to listen to changes in the accelerometer.
 * The function onReceivedData() will only be called if an accelerometer is present.
 */
public interface AccelerometerListener {

    void onReceivedData(float x, float y, float z);

}
