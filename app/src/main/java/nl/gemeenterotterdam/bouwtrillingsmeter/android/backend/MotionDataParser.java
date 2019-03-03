package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * TODO Rethink structure
 *
 * MotionDataPaser Class
 * <p>
 * This class translates our sensor data to actual usable information.
 * The data is stored in a DataCollection object.
 *
 * @author Thomas Beckers
 * @since 2019-02-28
 */
class MotionDataParser {

//    public static DataCollection currentDataCollection;
//
//    /**
//     * This starts the collection of data.
//     * TODO Create a bulletproof way of getting sensor access.
//     */
//    public static void startDataCollection() {
//        // Check all edge cases where we can not start collecting data
//        if (!SensorControl.getHasSensorAccess()) {
//            System.out.println("We do not have access to the sensors.");
//            return;
//        }
//
//        // If we are ready to go
//        currentDataCollection = new DataCollection();
//    }
//
//    /**
//     * This will close our DataCollection object.
//     * This will also stop the calculation loop.
//     */
//    public static void endDataCollection() {
//        if (null == currentDataCollection) {
//            System.out.println("No data collection was initialized.");
//        }
//
//        if (currentDataCollection.isCurrentlyCollectingData()) {
//            currentDataCollection.onStopCollectionOfData();
//        } else {
//            System.out.println("Current data collection has already ended.");
//        }
//    }
//
//    private static void updateFFT() {
//
//    }
//
//    private static void updateFrequency() {
//
//    }
//
//    /**
//     * This calculates the dominant frequencies on a given interval
//     * TODO Calculate
//     *
//     * @return A DominantFrequencies object with the calculated frequencies
//     */
//    private static DominantFrequencies calculateDominantFrequencies() {
//        DominantFrequencies result = null;
//
//        return result;
//    }
//
//    private static void velocityInFrequncyDomain() {
//
//    }
//
//    private static void getLimitValue() {
//
//    }

}
