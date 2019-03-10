package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This is used as a main communication unit between the frontend and the backend
 * This will initialize all backend components in one go by firing {@link #initialize()}.
 */
public class Backend {

    /**
     * initialize the backend
     * All is static so this can be accessed from anywhere in the backend
     */
    public static void initialize() {
        MeasurementControl.initialize();
        AccelerometerControl.initialize();
    }

    /**
     * This gets called when the application is opened
     * TODO Implement reading every serialized object in here
     */
    public static void OnApplicationStartup() {

    }

    /**
     * This gets called when the application is shut down
     */
    public static void OnApplicationShutdown() {
        MeasurementControl.onApplicationShutdown();
    }

    /**
     * This gets called when we are waiting for the user to put the phone on a flat surface.
     * TODO How to make the return call?
     */
    public static void onAwaitingPhoneFlatOnTable() {
        //
    }

    /**
     * This creates a new measurement.
     * This gets called by the + sign.
     */
    public static void createNewMeasurement() {
        MeasurementControl.createNewMeasurement();
    }

    /**
     * This starts the current measurement.
     * TODO How to handle missing measurement
     */
    public static void startMeasuring() {
        DataHandler.startMeasuring();
    }

    /**
     * This stops the current measurement.
     */
    public static void stopMeasuring() {
        DataHandler.stopMeasuring();
    }

    /**
     * Gets a list with all known measurements.
     * This is used by the UI to display all the measurements the user made.
     *
     * @return The known measurements list
     */
    public static ArrayList<Measurement> getAllMeasurementsList() {
        return MeasurementControl.getAllMeasurements();
    }

    /**
     * This returns the current measurement if there is one.
     * TODO How to handle null
     *
     * @return The current measurement
     */
    public static Measurement getCurrentMeasurement() {
        return MeasurementControl.getCurrentMeasurement();
    }

    /**
     * This overwrites the current settings.
     *
     * @param settings The settings object
     */
    public static void onGeneratedNewSettings(Settings settings) {
        // If something went wrong with the setup
        if (settings.buildingCategory == null ||
                settings.buildingCategory == BuildingCategory.NONE ||
                settings.vibrationCategory == null ||
                settings.vibrationCategory == VibrationCategory.NONE) {
            throw new IllegalArgumentException("Our settings file contained incomplete values.");
        }

        // If we are currently measuring
        if (DataHandler.isCurrentlyMeasuring()) {
            System.out.println("We are measuring. Do not change the settings.");
            return;
        }

        getCurrentMeasurement().settings = settings;
    }

    /**
     * This gets called by the {@link DataHandler} if we exceed a limit.
     * The frontend should refer to this function in some way.
     * TODO Make this an event for the frontend?
     */
    public static void onExceedLimit() {
        System.out.println("Exceeded limit!");
    }
}