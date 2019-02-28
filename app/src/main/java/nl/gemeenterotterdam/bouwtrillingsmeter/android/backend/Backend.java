package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * Backend class
 *
 * This is used as a main communication unit between the frontend and the backend
 * This will initialize all backend components in one go.
 * TODO Rethink this structure
 */
public class Backend {

    /**
     * Initialize the backend
     * All is static so this can be accessed from anywhere in the backend
     */
    public static void Initialize() {
        MeasurementControl.Initialize();
        SensorControl.initialize();
    }

    /**
     * This gets called when the application is opened
     * TODO Implement reading every serialized object in here
     */
    public static void OnApplicationStartup() {
        //
    }

    /**
     * This gets called when the application is shut down
     */
    public static void OnApplicationShutdown() {
        MeasurementControl.OnApplicationShutdown();
    }

}
