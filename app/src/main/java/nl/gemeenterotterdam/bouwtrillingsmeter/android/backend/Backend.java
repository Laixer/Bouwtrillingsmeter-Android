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
 */
public class Backend {

    private static BackendState backendState;
    private static ArrayList<BackendListener> backendListeners = new ArrayList<BackendListener>();
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

            MeasurementControl.initialize();
            AccelerometerControl.initialize();
            DataHandler.initialize();
            FlatPhoneDetector.initialize();

            SyncManager.initialize();
            SyncConnectionManager.initialize();

            changeBackendState(BackendState.BROWSING_APP);

            initialized = true;
        }
    }

    /**
     * Adds a backend state listener.
     * Do not forget to override the interface method!
     *
     * @param backendListener The object that implements the {@link BackendListener} interface.
     */
    public static void addBackendStateListener(BackendListener backendListener) {
        if (backendListener != null) {
            backendListeners.add(backendListener);
        }
    }

    /**
     * Removes a given listener
     *
     * @param listener The listener
     */
    public static void removeBackendStateListener(BackendListener listener) {
        if (backendListeners.contains(listener)) {
            backendListeners.remove(listener);
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
                SyncManager.onMeasurementStart(MeasurementControl.getCurrentMeasurement());
                break;

            case FINISHED_MEASUREMENT:
                DataHandler.stopMeasuring();
                MeasurementControl.onFinishMeasurement();
                SyncManager.onMeasurementFinished(MeasurementControl.getCurrentMeasurement());
                break;

            case UNSUPPORTED_HARDWARE:
                break;
        }

        // Call all the listeners.
        for (BackendListener listener : backendListeners) {
            if (listener != null) {
                listener.onBackendStateChanged(newState);
            }
        }

        // TODO Remove this
        System.out.println(String.format("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tChanged from %s to %s", oldState, newState));

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
     * Call this when the application shuts down
     */
    public static void onApplicationShutdown() {
        MeasurementControl.onApplicationShutdown();
        SyncManager.onApplicationShutdown();
    }

    /**
     * This gets called when we click a "create new measurement" button in the frontend.
     */
    public static void onClickCreateNewMeasurement() {
        if (MeasurementControl.getCurrentMeasurement() != null && !MeasurementControl.getCurrentMeasurement().isClosed()) {
            throw new IllegalStateException("Our current measurement object is still measuring! Creating a new measurement is not allowed.");
        }

        changeBackendState(BackendState.PREPARING_MEASUREMENT);
    }

    /**
     * This gets called when our frontend created a settings file.
     * We assume that this settings file is always valid.
     */
    public static void onClickCompleteSettingsSetup() {
        changeBackendState(BackendState.AWAITING_PHONE_FLAT);
    }

    /**
     * Gets called when we are done with the aftermath of our measurement.
     * This basically allows us to return to the main screen again (if implemented in any frontend).
     */
    public static void onDoneWithMeasurement() {
        changeBackendState(BackendState.BROWSING_APP);
    }

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
     */
    protected static void onExceedLimit() {
        System.out.println("Exceeded limit!");
        currentMeasurementExceeded = true;
        timeLastExceeding = Calendar.getInstance().getTime();

        for (BackendListener listener : backendListeners) {
            listener.onExceededLimit();
        }
    }

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

    /**
     * This triggers when we don't have sufficient hardware.
     *
     */
    static void onUnsupportedHardware() {
        changeBackendState(BackendState.UNSUPPORTED_HARDWARE);
    }

}