package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.Calendar;

/**
 * This class detects if we have our phone flat on the table.
 * It also detects when we pick it up again.
 */
class FlatPhoneDetector implements AccelerometerListener {

    private static final float maxDeltaToDetermineFlat = 0.2f;
    private static final float minDeltaToDeterminePickup = 3;
    private static final long periodInMillis = 300;
    private static final FlatPhoneDetector flatPhoneDetector = new FlatPhoneDetector();

    private static boolean flatOnTable = false;
    private static float[] periodMin = new float[3];
    private static float[] periodMax = new float[3];
    private static long lastTimeInMillis = 0;

    /**
     * Initializes this class
     */
    static void initialize() {
        AccelerometerControl.addListener(flatPhoneDetector);
    }

    /**
     * This gets called when our accelerometer measures data.
     * The data does NOT have to be different from the previous dataset.
     * This will just get called every "tick".
     *
     * @param x The acceleration in the x direction.
     * @param y The acceleration in the y direction.
     * @param z The acceleration in the z direction.
     */
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
                System.out.println("Set to true!");
            } else if (flatOnTable && d > minDeltaToDeterminePickup) {
                flatOnTable = false;
                System.out.println("Set to false!");
            }
//            System.out.println(String.format("Flat on table is now %s. d = %s Min = [%s, %s, %s]. Max = [%s, %s, %s].", flatOnTable, d, periodMin[0], periodMin[1], periodMin[2], periodMax[0], periodMax[1], periodMax[2]));

            // Reset the bunch
            periodMin = new float[3];
            periodMax = new float[3];
            lastTimeInMillis = Calendar.getInstance().getTimeInMillis();
        }
    }
}
