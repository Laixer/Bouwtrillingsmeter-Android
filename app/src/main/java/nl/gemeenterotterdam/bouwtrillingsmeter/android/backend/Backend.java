package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This is used as a main communication unit between the frontend and the backend.
 * This initializes all in {@link #initialize(Context, Resources)}.
 */
public class Backend {

    /**
     * Singleton instance of this class.
     */
    private static Backend backend;
    private String userUID = "user_id";

    private BackendState backendState;
    private ArrayList<IBackendListener> IBackendListeners = new ArrayList<IBackendListener>();
    private boolean initialized = false;
    private boolean currentMeasurementExceeded;
    private Date timeLastExceeding;
    private FlatPhoneDetector flatPhoneDetector;

    private Context context;
    private Resources resources;
    private LocationExtractor locationExtractor;

    /**
     * Creates a new Backend object for us to work with.
     * Call {@link #getInstance} to fetch as a singleton.
     *
     * TODO Doc the exception
     *
     * @param forceRefresh Set to true if you wish to
     *                     refresh the Backend singleton
     * @param context      The context which creates this
     */
    public Backend(boolean forceRefresh, Context context) {
        if (!forceRefresh && backend != null) {
            throw new RuntimeException("Cannot overwrite Backend object unless" +
                    "forceRefresh is set to true");
        }
        this.context = context;
        this.resources = context.getResources();
        backend = this;
    }

    /**
     * Initialize the backend.
     * This has a failsafe so that we can only call this once.
     * The only way this might happen is if our {@link nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend.MainActivity} gets dropped from memory
     * in the case of low phone memory.
     * The other only way this might happen is if we rotate our main menu.
     *
     * @param applicationContext Application context pointer
     * @param resources          Application resources pointer
     */
    public void initialize(Context applicationContext, Resources resources) {
        if (!initialized) {

            PreferenceManager.fetchSharedPreferences();
            generateOrFetchUserUID();

            locationExtractor = new LocationExtractor();

            StorageControl.initialize();

            MeasurementControl.initialize();
            AccelerometerControlOld.initialize();
            DataHandler.initialize();
            flatPhoneDetector = new FlatPhoneDetector();

            SyncManager.initialize();

            changeBackendState(BackendState.BROWSING_APP);

            initialized = true;
        }
    }

    /**
     * Adds a backend state listener.
     * Do not forget to override the interface method!
     *
     * @param IBackendListener The object that implements the {@link IBackendListener} interface.
     */
    public void addBackendStateListener(IBackendListener IBackendListener) {
        if (IBackendListener != null) {
            IBackendListeners.add(IBackendListener);
        }
    }

    /**
     * Removes a given listener
     *
     * @param listener The listener
     */
    public void removeBackendStateListener(IBackendListener listener) {
        if (IBackendListeners.contains(listener)) {
            IBackendListeners.remove(listener);
        }
    }

    /**
     * Changes the backend state.
     *
     * @param newState The new state.
     */
    private void changeBackendState(BackendState newState) {
        BackendState oldState = backendState;
        backendState = newState;

        try {
            switch (newState) {
                case BROWSING_APP:
                    break;

                case PREPARING_MEASUREMENT:
                    MeasurementControl.createNewMeasurement();
                    break;

                case AWAITING_PHONE_FLAT:
                    flatPhoneDetector.forceFlatToFalse();
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
                    MeasurementControl.getCurrentMeasurement().start();
                    DataHandler.startMeasuring();
                    SyncManager.onMeasurementStart(MeasurementControl.getCurrentMeasurement());
                    locationExtractor.fetchCurrentLocation();
                    break;

                case FINISHED_MEASUREMENT:
                    DataHandler.stopMeasuring();
                    MeasurementControl.onFinishMeasurement();
                    SyncManager.onMeasurementFinished(MeasurementControl.getCurrentMeasurement());
                    break;

                case UNSUPPORTED_HARDWARE:
                    break;

                default:
                    throw new IllegalStateException("New backend state is not valid.");
            }

            // Call all the listeners.
            for (IBackendListener listener : IBackendListeners) {
                if (listener != null) {
                    listener.onBackendStateChanged(newState);
                }
            }
        } catch (IllegalStateException | NullPointerException e) {
            System.out.println("New backend state is not valid. Changing back to browsing app.");
            changeBackendState(BackendState.BROWSING_APP);
        }
    }

    /**
     * Makes sure that if we exit an acitivity we get returned to the proper state
     */
    public void onPressedBackButton() {
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
    public void onApplicationShutdown() {
        MeasurementControl.onApplicationShutdown();
        SyncManager.onApplicationShutdown();
    }

    /**
     * This gets called when we click a "create new measurement" button in the frontend.
     */
    public void onClickCreateNewMeasurement() {
        if (MeasurementControl.getCurrentMeasurement() != null && !MeasurementControl.getCurrentMeasurement().isClosed()) {
            System.out.println("Our current measurement object is still measuring! Creating a new measurement is not allowed.");
            return;
        }

        changeBackendState(BackendState.PREPARING_MEASUREMENT);
    }

    /**
     * This gets called when our frontend created a settings file.
     * We assume that this settings file is always valid.
     */
    public void onClickCompleteSettingsSetup() {
        changeBackendState(BackendState.AWAITING_PHONE_FLAT);
    }

    /**
     * Gets called when we are done with the aftermath of our measurement.
     * This basically allows us to return to the main screen again (if implemented in any frontend).
     */
    public void onDoneWithMeasurement() {
        changeBackendState(BackendState.BROWSING_APP);
    }

    /**
     * @return True if our current measurement has exceeded a limit.
     */
    public boolean isCurrentMeasurementExceeded() {
        return currentMeasurementExceeded;
    }

    /**
     * @return The date object created during our last exceeding event.
     */
    public Date getTimeLastExceeding() {
        return timeLastExceeding;
    }

    /**
     * Gets a list with all known measurements.
     * This is used by the UI to display all the measurements the user made.
     *
     * @return The known measurements list
     */
    public ArrayList<Measurement> getAllMeasurementsList() {
        return MeasurementControl.getAllMeasurements();
    }

    /**
     * This returns the current measurement if there is one.
     *
     * @return The current measurement
     */
    public Measurement getCurrentMeasurement() {
        return MeasurementControl.getCurrentMeasurement();
    }

    /**
     * @return The last measurement
     */
    public Measurement getLastMeasurement() {
        return MeasurementControl.getLastMeasurement();
    }

    /**
     * This overwrites the current settings.
     *
     * @param settings The settings object
     */
    public void onGeneratedNewSettings(Settings settings) {
        // If something went wrong with the setup
        if (settings.getBuildingCategory() == null ||
                settings.getBuildingCategory() == BuildingCategory.NONE ||
                settings.getVibrationCategory() == null ||
                settings.getVibrationCategory() == VibrationCategory.NONE) {
            System.out.println("Our settings file contained incomplete values.");
            return;
        }

        // If we are currently measuring
        if (DataHandler.isCurrentlyMeasuring()) {
            System.out.println("We are measuring. Do not change the settings.");
            return;
        }

        // If we are in the wrong state
        if (backendState != BackendState.PREPARING_MEASUREMENT) {
            System.out.println("We are NOT in the PREPARING_MEASUREMENT state. No settings object can be created at the moment.");
            return;
        }

        getCurrentMeasurement().overwriteSettings(settings);
    }

    /**
     * This gets called by the {@link DataHandler} if we exceed a limit.
     * The frontend should refer to this function in some way.
     */
    protected void onExceedLimit() {
        currentMeasurementExceeded = true;
        timeLastExceeding = Calendar.getInstance().getTime();

        for (IBackendListener listener : IBackendListeners) {
            listener.onExceededLimit();
        }
    }

    /**
     * This is called by our {@link FlatPhoneDetector} when the phone is lying on the table.
     */
    public void onReadyToStartMeasurement() {
        if (backendState == BackendState.AWAITING_PHONE_FLAT) {
            changeBackendState(BackendState.MEASURING);
        }
    }

    /**
     * This is called by our {@link FlatPhoneDetector} when the phone is picked up again.
     */
    public void onRequestEndMeasurement() {
        if (backendState == BackendState.MEASURING) {
            changeBackendState(BackendState.FINISHED_MEASUREMENT);
        }
    }

    /**
     * This triggers when we don't have sufficient hardware.
     */
    void onUnsupportedHardware() {
        changeBackendState(BackendState.UNSUPPORTED_HARDWARE);
    }

    public String getUserUID() {
        return userUID;
    }

    /**
     * Used to load user UID.
     * If none is present, we create one.
     */
    private void generateOrFetchUserUID() {
        String storedUserUID = PreferenceManager.readStringPreference(R.string.pref_user_uid);
        if (storedUserUID == null) {
            userUID = UUID.randomUUID().toString();
            PreferenceManager.writeStringPreference(R.string.pref_user_uid, userUID);
        } else {
            userUID = storedUserUID;
        }
    }

    /**
     * @return A pointer to the resources
     */
    protected Resources getResources() {
        return resources;
    }

    /**
     * @return The application context which created
     * our Backend singleton
     */
    protected Context getContext() {
        return context;
    }

    /**
     * @return The singleton Backend object
     */
    public static Backend getInstance() {
        return backend;
    }
}