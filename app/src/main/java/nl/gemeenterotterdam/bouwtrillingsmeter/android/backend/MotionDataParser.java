package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * MotionDataPaser Class
 * <p>
 * This class translates our sensor data to actual usable information.
 * The data is stored in a DataCollection object.
 */
class MotionDataParser {

    public static DataCollection currentDataCollection;

    /**
     * This starts the collection of data.
     * TODO Create a bulletproof way of getting sensor access.
     */
    public static void startDataCollection() {
        // Check all edge cases where we can not start collecting data
        if (!SensorControl.getHasSensorAccess()) {
            System.out.println("We do not have access to the sensors.");
            return;
        }

        // If we are ready to go
        currentDataCollection = new DataCollection();
    }

    /**
     * This will close our DataCollection object.
     * This will also stop the calculation loop.
     */
    public static void endDataCollection() {
        if (null == currentDataCollection) {
            System.out.println("No data collection was initialized.");
        }

        if (currentDataCollection.isCurrentlyCollectingData()) {
            currentDataCollection.onStopCollectionOfData();
        } else {
            System.out.println("Current data collection has already ended.");
        }
    }

    private static void updateFFT() {

    }

    private static void updateFrequency() {

    }

    /**
     * This calculates the dominant frequencies on a given interval
     * TODO Calculate
     *
     * @return A DominantFrequency object with the calculated frequencies
     */
    private static DominantFrequency[] calculateDominantFrequencies() {
        DominantFrequency[] result = new DominantFrequency[3];

        // One for every dimension
        result[0] = new DominantFrequency(1, 4, false);
        result[1] = new DominantFrequency(2, 2, false);
        result[2] = new DominantFrequency(3, 1, false);

        return result;
    }

    private static void velocityInFrequncyDomain() {

    }

    private static void getLimitValue() {

    }

}
