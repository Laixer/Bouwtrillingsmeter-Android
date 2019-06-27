package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This interface is used to listen to changes in the accelerometer.
 * The function {@link IAccelerometerListener#onReceivedData(float, float, float) onReceivedData()}
 * will only be called if an accelerometer is present.
 */
public interface IAccelerometerListener {

    /**
     * This gets called when our accelerometer measures data.
     * The data does NOT have to be different from the previous dataset.
     * This will just get called every "tick".
     *
     * @param x The acceleration in the x direction.
     * @param y The acceleration in the y direction.
     * @param z The acceleration in the z direction.
     */
    void onReceivedData(float x, float y, float z);

}
