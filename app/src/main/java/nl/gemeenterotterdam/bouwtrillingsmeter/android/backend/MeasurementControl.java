package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class controls all {@link Measurement}s.
 * It holds all saved {@link Measurement}s and triggers saving / loading.
 * Our {@link #currentMeasurement} is the {@link Measurement} object to which we are currently saving our data.
 */
class MeasurementControl {

    private static LocationExtractorGoogle locationExtractorGoogle;
    private static Measurement currentMeasurement;
    private static Measurement lastMeasurement;
    private static ArrayList<Measurement> allMeasurements;

    /**
     * Initializes the class for static use.
     *
     * @param locationExtractorGoogle The used location
     *                                extractor
     */
    static void initialize(LocationExtractorGoogle locationExtractorGoogle) {
        try {
            allMeasurements = StorageControl.retrieveAllSavedMeasurements();
        } catch (StorageReadException e) {
            // TODO Handle
            System.out.println("Could not import saved measurements. Handle this");
            System.out.println(e.getMessage());
        }

        MeasurementControl.locationExtractorGoogle = locationExtractorGoogle;
    }

    /**
     * Gets the current measurement to which we are storing data
     *
     * @return The current measurement
     */
    static Measurement getCurrentMeasurement() {
        return currentMeasurement;
    }

    /**
     * Returns the last measurement that was created.
     *
     * @return Our last measurement
     */
    static Measurement getLastMeasurement() {
        return lastMeasurement;
    }

    /**
     * Returns the entire list of all measurements
     *
     * @return The arraylist containing all measurements
     */
    static ArrayList<Measurement> getAllMeasurements() {
        return allMeasurements;
    }

    /**
     * This creates a new measurement. The UID for this
     * measurement is created by the measurement class.
     * Also sets this measurement as currentMeasurement.
     * Also subscribes this measurement with the location
     * extractor.
     */
    static void createNewMeasurement() {
        currentMeasurement = new Measurement();
        locationExtractorGoogle.subscribeForLocation(currentMeasurement);
    }

    /**
     * This stops our measuring.
     * This adds the current measurement to {@link #allMeasurements}.
     * Our {@link #currentMeasurement} stays the same for future reference!
     * This is only overwritten if we redo the entire measurement creation process.
     */
    static void onFinishMeasurement() {
        currentMeasurement.close();
        lastMeasurement = currentMeasurement;
        allMeasurements.add(currentMeasurement);

        try {
            StorageControl.writeMeasurementMetaData(currentMeasurement);
            StorageControl.writeMeasurementDataIntervals(currentMeasurement);
        } catch (StorageWriteException e) {
            // TODO Handle
            System.out.println("Could not write measurement data to storage. Handle this");
            System.out.println(e.getMessage());
        }
    }

    /**
     * This aborts our current measurement, then deletes it.
     */
    static void abortCurrentMeasurement() {
        if (!currentMeasurement.isClosed()) {
            currentMeasurement.close();
            SyncManager.onMeasurementAborted(currentMeasurement);
        }

        // TODO this is commented out now, might mess things up
        // currentMeasurement = null;
    }

    /**
     * This gets called when the program exits
     * This attempts to write all measurements to the program cache
     */
    static void onApplicationShutdown() {
        for (Measurement measurement : allMeasurements) {
            try {
                StorageControl.writeMeasurementMetaData(measurement);
                StorageControl.writeMeasurementDataIntervals(measurement);
            } catch (StorageWriteException e) {
                // TODO Handle
                System.out.println("Could not write measurements on application shutdown, handle this");
                System.out.println(e.getMessage());
            }
        }
    }

}