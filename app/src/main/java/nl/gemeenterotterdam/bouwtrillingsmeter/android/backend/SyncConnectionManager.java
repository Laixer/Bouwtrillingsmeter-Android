package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;

/**
 * This class handles all our connections to WIFI and 3G/4G/5G.
 * It communicates with the {@link SyncManager} class.
 * TODO Implement feedback that indicates if we have successfully pushed something.
 */
class SyncConnectionManager {

    /**
     * Initializes the instance
     */
    public static void initialize() {

    }

    /**
     * Push a measurements metadata
     *
     * @param measurement The measurement
     * @return True if successful
     */
    static boolean pushMeasurementCreation(Measurement measurement) {
        return true;
    }

    /**
     * Push data interval essentials
     *
     * @param measurementUID The measurement UID
     * @param allDataIntervalEssentials All the data interval essentials
     * @return True if successful
     */
    static boolean pushAllDataIntervalEssentials(String measurementUID, ArrayList<DataIntervalEssentials> allDataIntervalEssentials) {
        return true;
    }

    /**
     * Push an entire data interval
     *
     * @param measurementUID The measurement UID
     * @param dataInterval The data interval
     * @return True if successful
     */
    static boolean pushDataInterval(String measurementUID, DataInterval dataInterval) {
        return true;
    }

}
