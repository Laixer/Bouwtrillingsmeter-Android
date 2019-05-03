package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import java.util.Calendar;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * This class detects if we have our phone flat on the table.
 * It also detects when we pick it up again.
 */
class FlatPhoneDetector implements SensorEventListener {

    private static final float maxDeltaToDetermineFlat = 0.025f;
    private static final float maxDeltaToDetermineAbsoluteFlat = 0.05f;
    private static final float minDeltaToDeterminePickup = 0.06f;

    private boolean flat = false;
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
     */
    private void updateOrientationAngles() {
        // Calculate orientation
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        float[] orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles);
//        System.out.println(String.format("%s %s %s", orientation[0], orientation[1], orientation[2]));

        // Do some maths to determine flat or pickup position
        float dx = Math.abs(orientation[0] - orientationComparing[0]);
        float dy = Math.abs(orientation[1] - orientationComparing[1]);
        float dz = Math.abs(orientation[2] - orientationComparing[2]);
        float d = Math.max(dx, Math.max(dy, dz));
        System.out.print("d = " + d);

        if (!flat && d < maxDeltaToDetermineFlat) {
            dx = Math.abs(orientation[0] - orientationFlat[0]);
            dy = Math.abs(orientation[1] - orientationFlat[1]);
            dz = Math.abs(orientation[2] - orientationFlat[2]);
            d = Math.max(dx, Math.max(dy, dz));
            System.out.println(",   d 000 = " + d);
            if (d < maxDeltaToDetermineAbsoluteFlat) {
                System.out.println("FLAT!");
                flat = true;
            }
        } else if (flat && d > minDeltaToDeterminePickup) {
            System.out.println("PICKUP!");
            flat = false;
        }

        // Save orientation if we are not flat
        if (!flat) {
            orientationComparing = new float[]{orientation[0], orientation[1], orientation[2]};
        }

        System.out.println("");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
