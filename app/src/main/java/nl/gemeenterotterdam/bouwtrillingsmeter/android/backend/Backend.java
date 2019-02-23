package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * This is used as a main communication unit between the frontend and the backend
 * TODO Rethink this structure
 */
public class Backend {

    public static MeasurementControl MeasurementControl;

    /**
     * Initialize the backend
     * All is static so this can be accessed from anywhere in the frontend
     */
    public static void Initialize() {
        MeasurementControl = new MeasurementControl();

        // TODO Remove this
        ReadWrite.DebugFunction();
    }

}
