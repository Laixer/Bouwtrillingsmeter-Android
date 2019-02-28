package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;
import java.util.List;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend.Utility;

/**
 * MeasurementControl class
 *
 * This class controls all measurements.
 * It holds the current measurement.
 * It handles the loading and saving of all measurements.
 */
class MeasurementControl {

    private static Measurement currentMeasurement;
    private static ArrayList<Measurement> allMeasurements;

    /**
     * initializes the class for static use
     */
    public static void Initialize() {
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
     * Returns a measurement based on its index in the arraylist
     *
     * @param index
     * @return The desired measurement
     */
    public static Measurement getMeasurementByIndex(int index) throws IndexOutOfBoundsException {
        int count = allMeasurements.size();
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException();
        }

        return allMeasurements.get(index);
    }

    /**
     * This creates a new measurement
     * The UID for this measurement is created by the measuremetn class
     * Also sets this measurement as currentMeasurement
     */
    public static void CreateNewMeasurement() {
        Measurement measurement = new Measurement();
        currentMeasurement = measurement;
    }

    /**
     * This adds the current measurement to the measurement list
     * This also attempts to save the current measurement to the cache
     */
    public static boolean SaveCurrentMeasurement() {
        boolean hasExported = ReadWrite.TryWriteMeasurement(Utility.ApplicationContext, currentMeasurement, currentMeasurement.getUID());
        return hasExported;
    }

    /**
     * This gets called when the program exits
     * This attempts to write all measurements to the program cache
     */
    public static void OnApplicationShutdown() {
        for (Measurement measurement : allMeasurements) {
            ReadWrite.TryWriteMeasurement(Utility.ApplicationContext, currentMeasurement, currentMeasurement.getUID());
        }
    }


    public static void SetDebugMeasurementList() {
        allMeasurements = new ArrayList<Measurement>();
        allMeasurements.add(new Measurement());
        allMeasurements.add(new Measurement());
    }

}