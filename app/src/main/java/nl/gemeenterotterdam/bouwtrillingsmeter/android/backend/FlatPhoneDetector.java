package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Calendar;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend.PreferenceManager;

/**
 * This class detects if we have our phone flat on the table.
 * It also detects when we pick it up again.
 * TODO Make this unsubscribe and not calculate when we are not measuring
 */
class FlatPhoneDetector implements SensorEventListener {

    private static final float MAX_DELTA_ROTATION_TO_DETERMINE_FLAT = 0.08f;
    private static final float MIN_DELTA_ROTATION_TO_DETERMINE_PICKUP = 0.1f;
    private static final long PERIOD_FLAT_IN_MILLIS = 500;

    private boolean flatPending = false;
    private boolean flat = false;
    private long periodStart;

    /**
     * This is used to check whether or not the phone is flat.
     * Only the y and z are relevant.
     * X can be whatever.
     */
    private final float[] orientationFlat = new float[]{0, 0, 0};
    private float[] orientationComparing = new float[3];

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    /**
     * Constructor
     */
    FlatPhoneDetector() {
        SensorManager sensorManager = (SensorManager) Backend.applicationContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * Gets called when we receive new sensor data.
     *
     * @param event The event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.length);
            updateOrientationAngles();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.length);

        }
    }

    /**
     * Calculate the orientation angles.
     * orientation[0] = rotation while upright, irrelevant for upright. Range = [-pi, pi]
     * orientation[1] = 0 when screen faces up. Range = [-pi, pi]
     * orientation[2] = 0 when screen faces up. Range = [-pi, pi]
     */
    private void updateOrientationAngles() {
        // Calculate orientation
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        float[] orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles);

        if(!flat) {
            float dy = Math.abs(orientation[1] - orientationFlat[1]);
            float dz = Math.abs(orientation[2] - orientationFlat[2]);
            float d = Math.max(dy, dz);

            if (d < MAX_DELTA_ROTATION_TO_DETERMINE_FLAT) {
                // If we are within the bounds
                if (!flatPending) {
                    // Start of a period
                    flatPending = true;
                    periodStart = Calendar.getInstance().getTimeInMillis();
                } else {
                    // In period
                    long dt = Calendar.getInstance().getTimeInMillis() - periodStart;

                    if (dt > PERIOD_FLAT_IN_MILLIS) {
                        // If the time limit exceeds we are flat
                        flat = true;
                        flatPending = false;

                        if (PreferenceManager.readBooleanPreference(R.string.pref_use_pickup)) {
                            Backend.onReadyToStartMeasurement();
                        }

                        orientationComparing = new float[]{orientation[0], orientation[1], orientation[2]};
                    }
                }
            } else {
                // If we are not within the bounds and pending, we no longer are pending
                if (flatPending) {
                    flatPending = false;
                }
            }
        } else if (flat) {
            // If we are already flat we check for pickup / orientation changes
            float dx = Math.abs(orientation[0] - orientationComparing[0]);
            float dy = Math.abs(orientation[1] - orientationComparing[1]);
            float dz = Math.abs(orientation[2] - orientationComparing[2]);
            float d = Math.max(dx, Math.max(dy, dz));

            if (d > MIN_DELTA_ROTATION_TO_DETERMINE_PICKUP) {
                flat = false;
                flatPending = false;

                if (PreferenceManager.readBooleanPreference(R.string.pref_use_pickup)) {
                    Backend.onRequestEndMeasurement();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    /**
     * Sets flat on table to false to enable our event calls again.
     */
    void forceFlatToFalse() {
        flat = false;
    }
}
