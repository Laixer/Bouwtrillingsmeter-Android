package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend.Utility;

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
    private static ArrayList<Measurement> allMeasurements;

    /**
     * initializes the class for static use
     */
    public static void initialize() {
        allMeasurements = new ArrayList<Measurement>();
    }

    /**
     * Gets the current measurement to which we are storing data
     *
     * @return The current measurement
     */
    public static Measurement getCurrentMeasurement() {
        return currentMeasurement;
    }

    /**
     * Returns the entire list of all measurements
     *
     * @return The arraylist containing all measurements
     */
    public static ArrayList<Measurement> getAllMeasurements() {
        return allMeasurements;
    }

    /**
     * This creates a new measurement
     * The UID for this measurement is created by the measuremetn class
     * Also sets this measurement as currentMeasurement
     */
    public static void createNewMeasurement() {
        Measurement measurement = new Measurement();
        currentMeasurement = measurement;
    }

    /**
     * This stops our measuring.
     * This adds the current measurement to {@link #allMeasurements}.
     * Our {@link #currentMeasurement} stays the same for future reference!
     * This is only overwritten if we redo the entire measurement creation process.
     */
    public static void onFinishMeasurement() {
        currentMeasurement.onStopMeasuring();
        allMeasurements.add(currentMeasurement);
    }

    /**
     * This aborts our current measurement, then deletes it.
     */
    public static void abortCurrentMeasurement() {
        if (!currentMeasurement.isClosed()) {
            currentMeasurement.onStopMeasuring();
        }
        currentMeasurement = null;
    }

    /**
     * This adds the current measurement to the measurement list
     * This also attempts to save the current measurement to the cache
     */
    public static void saveCurrentMeasurement() {
        StorageControl.writeObject(currentMeasurement, currentMeasurement.getUID());
    }

    /**
     * This gets called when the program exits
     * This attempts to write all measurements to the program cache
     */
    public static void onApplicationShutdown() {
        for (Measurement measurement : allMeasurements) {
            StorageControl.writeObject(measurement, measurement.getUID());
        }
    }

}