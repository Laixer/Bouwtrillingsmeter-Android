package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * This manages our accelerometer. This is used
 * as a singleton.
 */
class AccelerometerControl implements SensorEventListener {

    /**
     * Sensor type belonging to our accelerometer.
     */
    private static final int SENSOR_TYPE = Sensor.TYPE_LINEAR_ACCELERATION;

    /**
     * Sensor delay belonging to our accelerometer.
     */
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;

    /**
     * Singleton instance.
     */
    private static AccelerometerControl accelerometerControl;

    /**
     * All listeners that process the sensor data.
     */
    private ArrayList<IAccelerometerListener> listeners;

    /**
     * If our hardware is not sufficient enough this
     * will become false.
     */
    private boolean valid;

    /**
     * Constructor for this class. This is used as
     * a singleton.
     *
     * @throws UnsupportedHardwareException If we don't have
     *                                      sufficient hardware
     */
    AccelerometerControl() throws UnsupportedHardwareException {
        if (accelerometerControl != null) {
            throw new RuntimeException("Cannot make two instances");
        }

        accelerometerControl = this;
        listeners = new ArrayList<>();

        Context context = Backend.getInstance().getContext();
        SensorManager sensorManager = (SensorManager) context.
                getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(SENSOR_TYPE);

        if (sensor != null) {
            sensorManager.registerListener(
                    this, sensor, SENSOR_DELAY);
        } else {
            throw new UnsupportedHardwareException(
                    "Device does not support hardware operations");
        }
    }

    /**
     * This validates if our hardware is
     * able to sample fast enough for our
     * purposes.
     *
     * TODO Implement
     */
    private void validateSensor() {
        System.out.println("Implement validating sensor.");

        Backend.getInstance().onSensorsValidated(true);
    }

    /**
     * Adds a listener to the list.
     *
     * @param listener The listener object, with implemented interface.
     */
    public void addListener(IAccelerometerListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Called when there is a new sensor event.  Note that "on changed"
     * is somewhat of a misnomer, as this will also be called if we have a
     * new reading from a sensor with the exact same sensor values (but a
     * newer timestamp).
     *
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     *
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        for (IAccelerometerListener listener : listeners) {
            if (listener != null) {
                listener.onReceivedData(
                        event.values[0],
                        event.values[1],
                        event.values[2]);
            }
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     *
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor   The sensor that was called
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /* Not in use */
    }

    /**
     * @return True if our hardware is measured to be valid.
     */
    boolean isValid() {
        return valid;
    }

    /**
     * @return The singleton instance of this class.
     */
    public static AccelerometerControl getInstance() {
        return accelerometerControl;
    }

}
