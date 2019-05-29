package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.location.Location;

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

    private static Measurement currentMeasurement;
    private static Measurement lastMeasurement;
    private static ArrayList<Measurement> allMeasurements;

    /**
     * initializes the class for static use
     */
    static void initialize() {
        allMeasurements = StorageControl.retrieveAllSavedMeasurements();
    }

    /**
     * Gets the current measurement to which we are storing data
     *
     * @return The current measurement
     */
    static Measurement getCurrentMeasurement() {
        return currentMeasurement;
    }

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
     * This creates a new measurement
     * The UID for this measurement is created by the measuremetn class
     * Also sets this measurement as currentMeasurement
     */
    static void createNewMeasurement() {
        currentMeasurement = new Measurement();
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
        StorageControl.writeMeasurement(currentMeasurement);
    }

    /**
     * This aborts our current measurement, then deletes it.
     */
    static void abortCurrentMeasurement() {
        if (!currentMeasurement.isClosed()) {
            currentMeasurement.close();
            SyncManager.onMeasurementAborted(currentMeasurement);
        }
        currentMeasurement = null;
    }

    /**
     * This gets called when the program exits
     * This attempts to write all measurements to the program cache
     */
    static void onApplicationShutdown() {
        for (Measurement measurement : allMeasurements) {
            StorageControl.writeMeasurement(measurement);
        }
    }

    /**
     * Saves our current location to the current measurement.
     *
     * @param location The location
     */
    static void onNewLocationFetched(Location location) {
        if (!currentMeasurement.isClosed() && currentMeasurement.getLocationLongitude() == Double.MAX_VALUE) {
            currentMeasurement.setLocation(location);
        }
    }

}