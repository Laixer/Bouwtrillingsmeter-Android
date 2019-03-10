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

    private static BackendState backendState;
    private static ArrayList<BackendStateListener> backendStateListeners;

    /**
     * Initialize the backend.
     */
    public static void initialize() {
        backendState = BackendState.BEFORE_MEASURING;
        backendStateListeners = new ArrayList<BackendStateListener>();

        MeasurementControl.initialize();
        AccelerometerControl.initialize();
        DataHandler.initialize();
    }

    /**
     * This gets the backend state as an {@link BackendState} enum.
     *
     * @return The backend state.
     */
    static BackendState getBackendState() {
        return backendState;
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
    private static void onChangeBackendState(BackendState newState) {
        if (newState == null || newState == BackendState.NONE) {
            throw new IllegalStateException("New backend state is not valid.");
        }

        BackendState oldState = backendState;
        switch (newState) {
            case BEFORE_MEASURING:
                break;

            case AWAITING_PHONE_FLAT:
                break;

            case MEASURING:
                break;

            case MEASURING_END:
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
     * This gets called when the application is opened
     * TODO Implement reading every serialized object in here
     */
    public static void onApplicationStartup() {

    }

    /**
     * This gets called when the application is shut down
     */
    public static void onApplicationShutdown() {
        MeasurementControl.onApplicationShutdown();
    }

    /**
     * This creates a new measurement.
     * This gets called by the + sign.
     */
    public static void createNewMeasurement() {
        if (!MeasurementControl.getCurrentMeasurement().isClosed()) {
            throw new IllegalStateException("Our current measurement object is still measuring! Creating a new measurement is not allowed.");
        }

        MeasurementControl.createNewMeasurement();
    }

    /**
     * This starts the current measurement.
     */
    public static void startMeasuring() {
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
    }

    /**
     * This stops the current measurement.
     */
    public static void stopMeasuring() {
        // Edge cases
        if (MeasurementControl.getCurrentMeasurement() == null) {
            throw new IllegalStateException("No measurement object was present.");
        }

        if (MeasurementControl.getCurrentMeasurement().isClosed()) {
            throw new IllegalStateException("The current measurement object is already closed. Closing it again is not possible.");
        }

        DataHandler.stopMeasuring();
        MeasurementControl.getCurrentMeasurement().onStopMeasuring();
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
        if (backendState != BackendState.BEFORE_MEASURING) {
            throw new IllegalStateException("We are in the BEFORE_MEASURING state. No settings object can be created at the moment.");
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