package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

/**
 * This hosts our constants for the frontend.
 * TODO Rethink this structure
 */
public class Constants {

    /**
     * The amount of ms we wait before displaying our next string message
     * in the {@link MeasuringActivity} while measuring.
     */
    public static int measuringTextCycleSleepTimeInMilis = 1400;

    /**
     * The minimum time needed between two exceeding events in order for our UI to display a new message.
     */
    public static int minimumTimeInMilisBetweenExceedings = 1500;


}
