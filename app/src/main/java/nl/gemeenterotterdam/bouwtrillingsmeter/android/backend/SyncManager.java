package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;

/**
 * This class handles the synchronization of our measurements.
 * It communicates with the {@link SyncConnectionManager} class.
 * TODO Implement shutdown serialize what we have not sent yet
 * TODO If we now push after measuring while disconnected, we send EVERYTHING at once.
 */
class SyncManager implements DataIntervalClosedListener {

    private static final String nameUnpushedDataIntervalsList = "unpushed_data_intervals";
    private static final String nameUnpushedDataIntervalEssentialsList = "unpushed_data_interval_essentials";
    private static final int intervalCountBeforePushing = 30;
    private static final int essentialsCountBeforePushing = 30;
    private static SyncManager syncManager;

    private static ArrayList<DataInterval> unpushedDataIntervals;
    private static ArrayList<DataIntervalEssentials> unpushedDataIntervalEssentials;

    /**
     * Initializes the instance
     */
    static void initialize() {
        unpushedDataIntervals = StorageControl.<DataInterval>retrieveArrayList(nameUnpushedDataIntervalsList);
        unpushedDataIntervalEssentials = StorageControl.<DataIntervalEssentials>retrieveArrayList(nameUnpushedDataIntervalEssentialsList);

        DataHandler.addDataIntervalClosedListener(new SyncManager());
        startSync();
    }

    /**
     * This should be called when our application shuts down.
     * It stores our unpushed data intervals and data interval essentials.
     */
    static void onApplicationShutdown() {
        StorageControl.writeObject(unpushedDataIntervals, nameUnpushedDataIntervalsList);
        StorageControl.writeObject(unpushedDataIntervalEssentials, nameUnpushedDataIntervalEssentialsList);
    }

    /**
     * This gets called when we start a new measurement.
     *
     * @param measurement The measurement
     */
    static void onMeasurementStart(Measurement measurement) {
        SyncConnectionManager.pushMeasurementMetadata(measurement);
    }

    /**
     * This gets called when we abort our current measurement.
     *
     * @param measurement The measurement
     */
    static void onMeasurementAborted(Measurement measurement) {
        SyncConnectionManager.pushMeasurementAborted(measurement);
    }

    /**
     * This gets called when we successfully close our measurement.
     * We push all our essentials.
     *
     * @param measurement The measurement
     */
    static void onMeasurementFinished(Measurement measurement) {
        SyncConnectionManager.pushMeasurementMetadata(measurement);
        SyncConnectionManager.pushDataIntervalsList(measurement.dataIntervals);
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
        ConnectionType connectionType = SyncConnectionManager.getConnectionType();

        // Send everything if we have wifi
        if (connectionType == ConnectionType.WIFI || connectionType == ConnectionType.WIFI_AND_G) {
            unpushedDataIntervals.add(dataInterval);
            // Push and reset if we have enough
            if (unpushedDataIntervals.size() > intervalCountBeforePushing) {
                SyncConnectionManager.pushDataIntervalsList(unpushedDataIntervals);
                unpushedDataIntervals = new ArrayList<DataInterval>();
            }
        }

        // Only send the essentials if we have no wifi
        else if (connectionType == ConnectionType.G) {
            DataIntervalEssentials dataIntervalEssentials = new DataIntervalEssentials(measurementUID, dataInterval.index, dataInterval.velocitiesAbsMax, dataInterval.dominantFrequencies);
            unpushedDataIntervalEssentials.add(dataIntervalEssentials);
            // Push and reset if we have enough
            if (unpushedDataIntervalEssentials.size() > essentialsCountBeforePushing) {
                SyncConnectionManager.pushDataIntervalEssentialsList(measurementUID, unpushedDataIntervalEssentials);
                unpushedDataIntervalEssentials = new ArrayList<DataIntervalEssentials>();
            }
        }
    }

    private static void startSync() {
        ConnectionType connectionType = SyncConnectionManager.getConnectionType();
    }

    private static void stopSync() {

    }

}