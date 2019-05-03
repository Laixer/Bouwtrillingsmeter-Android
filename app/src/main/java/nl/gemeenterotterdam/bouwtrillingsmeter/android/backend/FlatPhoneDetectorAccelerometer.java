/*
package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.Calendar;

*/
/**
 * This class detects if we have our phone flat on the table.
 * It also detects when we pick it up again.
 *//*

class FlatPhoneDetectorAccelerometer implements AccelerometerListener {

    private static final float maxDeltaToDetermineFlat = 0.3f;
    private static final float minDeltaToDeterminePickup = 3;
    private static final long periodInMillis = 300;
    private static final FlatPhoneDetectorAccelerometer flatPhoneDetector = new FlatPhoneDetectorAccelerometer();

    private static boolean flatOnTable = false;
    private static float[] periodMin = new float[3];
    private static float[] periodMax = new float[3];
    private static long lastTimeInMillis = 0;

    */
/**
     * Initializes this class
     *//*

    static void initialize() {
        AccelerometerControl.addListener(flatPhoneDetector);
    }

    */
/**
     * This gets called when we enter the {@link BackendState#AWAITING_PHONE_FLAT} state.
     * In this way we double check if we really are flat.
     * We also trigger the {@link Backend#onPhoneFlat()} event.
     *//*

    static void forceFlatOnTableToFalse() {
        flatOnTable = false;
    }

    */
/**
     * This gets called when our accelerometer measures data.
     * The data does NOT have to be different from the previous dataset.
     * This will just get called every "tick".
     *
     * @param x The acceleration in the x direction.
     * @param y The acceleration in the y direction.
     * @param z The acceleration in the z direction.
     *//*

    @Override
    public void onReceivedData(float x, float y, float z) {
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

            // Determine
            if (!flatOnTable && d < maxDeltaToDetermineFlat) {
                flatOnTable = true;
                Backend.onPhoneFlat();
            } else if (flatOnTable && d > minDeltaToDeterminePickup) {
                flatOnTable = false;
                Backend.onPhonePickup();
            }

            // Reset the bunch
            periodMin = new float[3];
            periodMax = new float[3];
            lastTimeInMillis = Calendar.getInstance().getTimeInMillis();
        }
    }
}
*/
