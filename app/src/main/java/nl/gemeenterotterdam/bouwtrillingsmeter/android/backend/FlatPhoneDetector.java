package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Calendar;

/**
 * This class detects if we have our phone flat on the table.
 * It also detects when we pick it up again.
 */
class FlatPhoneDetector implements SensorEventListener {

    private static final int sensorType = Sensor.TYPE_ROTATION_VECTOR;
    private static final int sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
    private static final float maxDeltaToDetermineFlat = 0.01f;
    private static final float minDeltaToDeterminePickup = 0.02f;
    private static final long periodInMillis = 300;

    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean flatOnTable = false;
    private float[] periodMin = new float[3];
    private float[] periodMax = new float[3];
    private float[] orientationFlat = new float[3];
    private long lastTimeInMillis = 0;

    FlatPhoneDetector() {
        sensorManager = (SensorManager) Backend.applicationContext.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, sensorDelay);
        } else {
            throw new UnsupportedOperationException("No accelerometer available");
        }
    }

    /**
     * This gets called when we enter the {@link BackendState#AWAITING_PHONE_FLAT} state.
     * In this way we double check if we really are flat.
     * We also trigger the {@link Backend#onPhoneFlat()} event.
     */
    void forceFlatOnTableToFalse() {
        flatOnTable = false;
    }

    /**
     * Gets called when we get new sensor data.
     * values[0] is pointed east
     * values[1] is pointed north
     * values[2] is pointed up
     * TODO Implement that we are facing upwards?
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Get values
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
//        System.out.println(String.format("%s %s %s", x, y, z));

        // Get min and max
        periodMin[0] = Math.min(periodMin[0], x);
        periodMin[1] = Math.min(periodMin[1], y);
        periodMin[2] = Math.min(periodMin[2], z);
        periodMax[0] = Math.max(periodMax[0], x);
        periodMax[1] = Math.max(periodMax[1], y);
        periodMax[2] = Math.max(periodMax[2], z);

        // If our period is over
        if (Calendar.getInstance().getTimeInMillis() - lastTimeInMillis > periodInMillis) {
            // Get highest delta
            float d = 0;
            for (int dimension = 0; dimension < 3; dimension++) {
                d = Math.max(periodMax[dimension] - periodMin[dimension], d);
            }

            System.out.println(String.format("Max delta = %s", d));

            // Determine
            if (!flatOnTable && d < maxDeltaToDetermineFlat) {
                flatOnTable = true;
                orientationFlat = new float[]{x, y, z};
//                Backend.onPhoneFlat();
                System.out.println("FLAT!");
            } else if (flatOnTable && d > minDeltaToDeterminePickup) {
                flatOnTable = false;
//                Backend.onPhonePickup();
                System.out.println("PICKUP!");
            }

            // Reset the bunch
            periodMin = new float[3];
            periodMax = new float[3];
            lastTimeInMillis = Calendar.getInstance().getTimeInMillis();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
