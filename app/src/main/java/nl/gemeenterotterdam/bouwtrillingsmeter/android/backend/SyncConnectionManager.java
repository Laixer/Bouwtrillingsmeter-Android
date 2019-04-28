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
     * Push a measurements metadata.
     * This overwrites anything existing on the server, so we can
     * use this as an update function.
     *
     * @param measurement The measurement
     * @return True if successful
     */
    static boolean pushMeasurementMetadata(Measurement measurement) {
        return true;
    }

    /**
     * Tells the server that our measurement was aborted.
     *
     * @param measurement The measurement
     * @return True if successful
     */
    static boolean pushMeasurementAborted(Measurement measurement) {
        return true;
    }

    /**
     * Push data interval essentials
     *
     * @param measurementUID            The measurement UID
     * @param allDataIntervalEssentials All the data interval essentials
     * @return True if successful
     */
    static boolean pushAllDataIntervalEssentials(String measurementUID, ArrayList<DataIntervalEssentials> allDataIntervalEssentials) {
        return true;
    }

    /**
     * Push all dataintervals that belong to a measurement
     *
     * @param measurement The measurement
     * @return True if successful
     */
    static boolean pushMeasurementDataIntervals(Measurement measurement) {
        return true;
    }

}
