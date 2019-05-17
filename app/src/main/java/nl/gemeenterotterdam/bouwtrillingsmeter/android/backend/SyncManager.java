package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;

/**
 * This class handles the synchronization of our measurements.
 * Most functions just determine which data goes to which list.
 * <p>
 * The actual sync management happens in {@link #startSync()}.
 * The actual server communication happens in {@link SyncConnectionManager}.
 * <p>
 * TODO Implement shutdown serialize what we have not sent yet
 * TODO If we now push after measuring while disconnected, we send EVERYTHING at once.
 */
class SyncManager implements DataIntervalClosedListener {

    private static final String NAME_UNPUSHED_MEASUREMENTS_LIST = "unpushed_measurements";
    private static final String NAME_UNPUSHED_DATA_INTERVALS_LIST = "unpushed_data_intervals";
    private static final String NAME_UNPUSHED_DATA_INTERVAL_ESSENTIALS_LIST = "unpushed_data_interval_essentials";
    private static final int INTERVAL_COUNT_BEFORE_PUSHING = 30;
    private static final int INTERVAL_ESSENTIALS_COUNT_BEFORE_PUSHING = 30;
    private static final long THREAD_SLEEP_IN_MILLIS = 5000;

    private static SyncManager syncManager;
    private static SyncConnectionManager syncConnectionManager;
    private static boolean syncing;
    private static Thread threadSyncing;

    // TODO Make these stacks
    private static ArrayList<Measurement> unpushedMeasurements;
    private static ArrayList<DataInterval> unpushedDataIntervals;
    private static ArrayList<DataIntervalEssentials> unpushedDataIntervalEssentials;

    /**
     * Initializes the instance
     */
    static void initialize() {
        unpushedMeasurements = StorageControl.<Measurement>retrieveArrayList(NAME_UNPUSHED_MEASUREMENTS_LIST);
        unpushedDataIntervals = StorageControl.<DataInterval>retrieveArrayList(NAME_UNPUSHED_DATA_INTERVALS_LIST);
        unpushedDataIntervalEssentials = StorageControl.<DataIntervalEssentials>retrieveArrayList(NAME_UNPUSHED_DATA_INTERVAL_ESSENTIALS_LIST);
        syncing = false;

        syncConnectionManager = new SyncConnectionManager();
        DataHandler.addDataIntervalClosedListener(new SyncManager());
        startSync();
    }

    /**
     * This should be called when our application shuts down.
     * It stores our unpushed data intervals and data interval essentials.
     */
    static void onApplicationShutdown() {
        StorageControl.writeArrayList(unpushedDataIntervals, NAME_UNPUSHED_DATA_INTERVALS_LIST);
        StorageControl.writeArrayList(unpushedDataIntervalEssentials, NAME_UNPUSHED_DATA_INTERVAL_ESSENTIALS_LIST);
    }

    /**
     * This gets called when we start a new measurement.
     *
     * @param measurement The measurement
     */
    static void onMeasurementStart(Measurement measurement) {
        unpushedMeasurements.add(measurement);
    }

    /**
     * This gets called when we abort our current measurement.
     * TODO Do we need this?
     *
     * @param measurement The measurement
     */
    static void onMeasurementAborted(Measurement measurement) {
        //
    }

    /**
     * This gets called when we successfully close our measurement.
     * All data intervals get added to the to-be-pushed list.
     *
     * @param measurement The measurement
     */
    static void onMeasurementFinished(Measurement measurement) {
        if (!unpushedMeasurements.contains(measurement)) {
            unpushedMeasurements.add(measurement);
        }
        unpushedDataIntervals.addAll(measurement.getDataIntervals());
    }

    /**
     * This gets called when the {@link DataInterval} is closed.
     * TODO Wat doen we als je halverwege je meting WIFI aanzet?
     *
     * @param dataInterval The datainterval
     */
    @Override
    public void onDataIntervalClosed(DataInterval dataInterval) {
        unpushedDataIntervals.add(dataInterval);

        // If we are on G, we want to send the essentials.
        // In all other cases we just send the full data interval.
        SyncConnectionType syncConnectionType = syncConnectionManager.getConnectionType();
        if (syncConnectionType == SyncConnectionType.G) {
            unpushedDataIntervalEssentials.add(dataInterval.toEssentials());
        }
    }

    /**
     * This starts the process of synchronization.
     * TODO Implement
     * TODO Is sleep the way to go? There has to be a more elegant way.
     */
    private static void startSync() {
        if (syncing) {
            throw new IllegalStateException("Cannot start sync when we are already syncing.");
        }

        threadSyncing = new Thread(() -> {
            // while (syncing) {
            // }
            SyncConnectionType syncConnectionType = syncConnectionManager.getConnectionType();

            // If we have no internet we wait
            if (syncConnectionType == SyncConnectionType.NONE) {
                try {
                    Thread.sleep(THREAD_SLEEP_IN_MILLIS);
                } catch (InterruptedException e) {
                    //
                }
            }

            if (syncConnectionType == SyncConnectionType.G) {

            }

            if (syncConnectionType == SyncConnectionType.WIFI || syncConnectionType == SyncConnectionType.WIFI_AND_G) {

            }


        });
        threadSyncing.start();
    }

    /**
     * This stops the process of synchronization.
     */
    private static void stopSync() {
        if (!syncing) {
            throw new IllegalStateException("Cannot stop sync when we are not syncing.");
        }

    }
}