package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.app.Activity;

import java.util.ArrayList;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This is used as a main communication unit between the frontend and the backend
 * This will initialize all backend components in one go by firing {@link #initialize()}.
 * <p>
 * TODO Consistency with where we put the edge cases (exception throwers) in the backend state.
 */
public class Backend {

    private static BackendState backendState;
    private static ArrayList<BackendStateListener> backendStateListeners;
    private static boolean initialized = false;

    /**
     * Initialize the backend.
     * This has a failsafe so that we can only call this once.
     * The only way this might happen is if our {@link nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend.MainActivity} gets droppoed from memory
     * in the case of low phone memory.
     */
    public static void initialize() {
        if (!initialized) {
            backendStateListeners = new ArrayList<BackendStateListener>();

            MeasurementControl.initialize();
            AccelerometerControl.initialize();
            DataHandler.initialize();

            changeBackendState(BackendState.BROWSING_APP);

            initialized = true;
        }
    }

    /**
     * Adds a backend state listener.
     * Do not forget to override the interface method!
     *
     * @param backendStateListener The object that implements the {@link BackendStateListener} interface.
     */
    public static void addBackendStateListener(BackendStateListener backendStateListener) {
        backendStateListeners.add(backendStateListener);
    }

    /**
     * Changes the backend state.
     *
     * @param newState The new state.
     */
    private static void changeBackendState(BackendState newState) {
        if (newState == null || newState == BackendState.NONE) {
            throw new IllegalStateException("New backend state is not valid.");
        }

        BackendState oldState = backendState;
        backendState = newState;

        switch (newState) {
            case BROWSING_APP:
                break;

            case PREPARING_MEASUREMENT:
                MeasurementControl.createNewMeasurement();
                break;

            case AWAITING_PHONE_FLAT:

                break;

            case MEASURING:
                // Edge cases
                if (MeasurementControl.getCurrentMeasurement() == null) {
                    throw new IllegalStateException("No measurement object was present.");
                }

                if (MeasurementControl.getCurrentMeasurement().isClosed()) {
                    throw new IllegalStateException("The current measurement object is already closed. No more data can be added.");
                }

                // Start the measurement
                MeasurementControl.getCurrentMeasurement().onStartMeasuring();
                DataHandler.startMeasuring();
                break;

            case FINISHED_MEASUREMENT:
                DataHandler.stopMeasuring();
                MeasurementControl.onFinishMeasurement();
                break;
        }

        // Call all the listeners.
        for (BackendStateListener listener : backendStateListeners) {
            if (listener != null) {
                 listener.onBackendStateChanged(newState);
            }
        }

    }

    /**
     * Makes sure that if we exit an acitivity we get returned to the proper state
     */
    public static void onPressedBackButton() {
        switch (backendState) {
            // We exited the settings menu
            // Go back to browsing the app
            case PREPARING_MEASUREMENT:
                MeasurementControl.abortCurrentMeasurement();
                changeBackendState(BackendState.BROWSING_APP);
                break;

            // We abort the "await for phone to be flat" bit
            // This is done after the popup message
            case AWAITING_PHONE_FLAT:
                MeasurementControl.abortCurrentMeasurement();
                changeBackendState(BackendState.BROWSING_APP);
                break;

            // Abort our measurement
            case MEASURING:
                DataHandler.stopMeasuring();
                MeasurementControl.abortCurrentMeasurement();
                changeBackendState(BackendState.BROWSING_APP);
                break;
        }
    }

    /**
     * This stops the current measurement.
     */
    protected static void stopMeasuring() {
        // Edge cases
        if (MeasurementControl.getCurrentMeasurement() == null) {
            throw new IllegalStateException("No measurement object was present.");
        }

        if (MeasurementControl.getCurrentMeasurement().isClosed()) {
            throw new IllegalStateException("The current measurement object is already closed. Closing it again is not possible.");
        }

        changeBackendState(BackendState.FINISHED_MEASUREMENT);
    }

    /**
     * These functions are called from the frontend
     * TODO Make sure all are in the right place, some functions are internal.
     */

    /**
     * TODO Javadoc
     */
    public static void onClickCreateNewMeasurement() {
        if (MeasurementControl.getCurrentMeasurement() != null && !MeasurementControl.getCurrentMeasurement().isClosed()) {
            throw new IllegalStateException("Our current measurement object is still measuring! Creating a new measurement is not allowed.");
        }

        changeBackendState(BackendState.PREPARING_MEASUREMENT);
    }

    /**
     * TODO Javadoc
     * TODO Implement failsafe for invalid settings file
     */
    public static void onClickCompleteSettingsSetup() {
        changeBackendState(BackendState.AWAITING_PHONE_FLAT);
    }

    /**
     * This attempts to end the measurement.
     */
    public static void onPickUpPhoneWhileMeasuring() {
        changeBackendState(BackendState.FINISHED_MEASUREMENT);
    }

    /**
     * This gets called when the user presses the back button or something like that.
     * TODO Implement some kind of catch mechanism, maybe a popup message?
     */
    public static void onUserForceStopMeasurement() {
        changeBackendState(BackendState.FINISHED_MEASUREMENT);
    }

    /**
     * Gets called when we are done with the aftermath of our measurement.
     * This basically allows us to return to the main screen again (if implemented in any frontend).
     */
    public static void onDoneWithMeasurement() {
        changeBackendState(BackendState.BROWSING_APP);
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
            throw new IllegalStateException("We are measuring. Do not change the settings.");
        }

        // If we are in the wrong state
        if (backendState != BackendState.PREPARING_MEASUREMENT) {
            throw new IllegalStateException("We are NOT in the PREPARING_MEASUREMENT state. No settings object can be created at the moment.");
        }

        getCurrentMeasurement().settings = settings;
    }

    /**
     * This gets called by the {@link DataHandler} if we exceed a limit.
     * The frontend should refer to this function in some way.
     * TODO Make this an event for the frontend?
     */
    protected static void onExceedLimit() {
        System.out.println("Exceeded limit!");
    }

    /**
     * TODO Remove this debug function.
     */
    public static void debugOnPhoneFlat() {
        changeBackendState(BackendState.MEASURING);
    }
}