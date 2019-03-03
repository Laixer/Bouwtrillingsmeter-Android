package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend.Utility;

/**
 * @author Thomas Beckers
 * @version 1.0
 * <p>
 * Accelerometer class
 * <p>
 * This communicates with the accelerometer sensor in your phone.
 * No other class communicates with the hardware.
 */
class Accelerometer implements SensorEventListener {

    public static Accelerometer accelerometer;

    private static final int sensorType = Sensor.TYPE_LINEAR_ACCELERATION;
    private static final int sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
    private static SensorManager sensorManager;
    private static Sensor sensor;

    /**
     * Constructor.
     * This attempts to connect to the hardware present.
     * @throws UnsupportedOperationException Thrown when the hardware is not supported on this phone.
     */
    public Accelerometer() {
        sensorManager = (SensorManager) Utility.ApplicationContext.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, sensorDelay);
        } else {
            throw new UnsupportedOperationException("No accelerometer available");
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
        AccelerometerControl.onAccelerometerChanged(event.values[0], event.values[1], event.values[2]);
    }

    /**
     * Not required thus not implemented.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use
    }

}
