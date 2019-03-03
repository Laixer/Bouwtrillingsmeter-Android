package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;

/**
 * TODO Javadoc
 */
public class AccelerometerControl {

    public static Accelerometer accelerometer;
    private static ArrayList<AccelerometerListener> listeners;

    /**
     * Gets called when the backend is initialized.
     * This attempts to connect to the accelerometer.
     */
    public static void initialize() {
        listeners = new ArrayList<AccelerometerListener>();

        try {
            accelerometer = new Accelerometer();
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Gets the accelerometer that is listening to the phone sensors
     *
     * @return The accelerometer class object used
     */
    public static Accelerometer getAccelerometer() {
        if (accelerometer == null) {
            accelerometer = new Accelerometer();
        }

        return accelerometer;
    }

    /**
     * Adds a listener to the list.
     *
     * @param listener The listener object, with implemented interface.
     */
    public static void addListener(AccelerometerListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Gets called when the accelerometer class receives new values from the sensor.
     * This will trigger all listeners.
     */
    public static void onAccelerometerChanged(float x, float y, float z) {
        for (AccelerometerListener listener : listeners) {
            if (listener != null) {
                listener.onReceivedData(x, y, z);
            }
        }
    }

}
