package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;

/**
 * This class handles the synchronization of our measurements.
 * It communicates with the {@link SyncConnectionManager} class.
 * TODO Don't we want to push everything we have if we are connected to wifi?
 */
class SyncManager implements DataIntervalClosedListener {

    private static final int essentialsCountBeforePushing = 30;
    private static SyncManager syncManager;

    private static ArrayList<DataIntervalEssentials> allDataIntervalEssentials;

    /**
     * Initializes the instance
     */
    public static void initialize() {
        allDataIntervalEssentials = new ArrayList<DataIntervalEssentials>();
        DataHandler.addDataIntervalClosedListener(new SyncManager());
    }

    /**
     * This gets called when we start a new measurement.
     *
     * @param measurement The measurement
     */
    public static void onMeasurementStart(Measurement measurement) {

    }

    /**
     * This gets called when we abort our current measurement.
     *
     * @param measurement The measurement
     */
    public static void onMeasurementAborted(Measurement measurement) {

    }

    /**
     * This gets called when we successfully close our measurement.
     * We push all our essentials.
     *
     * @param measurement The measurement
     */
    public static void onMeasurementClosed(Measurement measurement) {

    }

    /**
     * This gets called when the {@link DataInterval} is closed.
     * All listeners get called from the {@link DataHandler} class.
     * Do not forget to add the @Override tag to this function!
     *
     * @param dataInterval The datainterval
     */
    @Override
    public void onDataIntervalClosed(DataInterval dataInterval) {
        String measurementUID = MeasurementControl.getCurrentMeasurement().getUID();
        DataIntervalEssentials dataIntervalEssentials = new DataIntervalEssentials(measurementUID, dataInterval.velocitiesAbsMax, dataInterval.dominantFrequencies);
        allDataIntervalEssentials.add(dataIntervalEssentials);

        // Push and reset if we have enough
        if (allDataIntervalEssentials.size() > essentialsCountBeforePushing) {
            SyncConnectionManager.pushAllDataIntervalEssentials(measurementUID, allDataIntervalEssentials);
            allDataIntervalEssentials = new ArrayList<DataIntervalEssentials>();
        }
    }

    private static void startSync() {

    }

    private static void stopSync() {

    }


}