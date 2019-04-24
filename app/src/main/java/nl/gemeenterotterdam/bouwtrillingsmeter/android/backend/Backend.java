package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This is used as a main communication unit between the frontend and the backend.
 * This initializes all in {@link #initialize(Context, Resources)}.
 * <p>
 * TODO Consistency with where we put the edge cases (exception throwers) in the backend state.
 */
public class Backend {

    private static BackendState backendState;
    private static ArrayList<BackendStateListener> backendStateListeners;
    private static boolean initialized = false;
    private static boolean currentMeasurementExceeded;
    private static Date timeLastExceeding;

    protected static Context applicationContext;
    protected static Resources resources;

    /**
     * Initialize the backend.
     * This has a failsafe so that we can only call this once.
     * The only way this might happen is if our {@link nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend.MainActivity} gets dropped from memory
     * in the case of low phone memory.
     */
    public static void initialize(Context applicationContext, Resources resources) {
        if (!initialized) {
            Backend.applicationContext = applicationContext;
            Backend.resources = resources;

            backendStateListeners = new ArrayList<BackendStateListener>();

            MeasurementControl.initialize();
            AccelerometerControl.initialize();
            DataHandler.initialize();
            FlatPhoneDetector.initialize();
            StorageControl.retrieveAllSavedMeasurements();

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
     * Removes a given listener
     *
     * @param listener The listener
     */
    public static void removeBackendStateListener(BackendStateListener listener) {
        if (backendStateListeners.contains(listener)) {
            backendStateListeners.remove(listener);
        }
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
                FlatPhoneDetector.forceFlatOnTableToFalse();
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
                currentMeasurementExceeded = false;
                Calculator.onStartMeasurementCalculations();
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
     * These are the getters called by the frontend
     * TODO Move the measuremnet things to measurementcontrol?
     */

    /**
     * @return True if our current measurement has exceeded a limit.
     */
    public static boolean isCurrentMeasurementExceeded() {
        return currentMeasurementExceeded;
    }

    /**
     * @return The date object created during our last exceeding event.
     */
    public static Date getTimeLastExceeding() {
        return timeLastExceeding;
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
        currentMeasurementExceeded = true;
        timeLastExceeding = Calendar.getInstance().getTime();
    }

    /**
     * These are the events called by the backend
     */

    /**
     * This is called by our {@link FlatPhoneDetector} when the phone is lying on the table.
     */
    static void onPhoneFlat() {
        if (backendState == BackendState.AWAITING_PHONE_FLAT) {
            changeBackendState(BackendState.MEASURING);
        }
    }

    /**
     * This is called by our {@link FlatPhoneDetector} when the phone is picked up again.
     */
    static void onPhonePickup() {
        if (backendState == BackendState.MEASURING) {
            changeBackendState(BackendState.FINISHED_MEASUREMENT);
        }
    }


}