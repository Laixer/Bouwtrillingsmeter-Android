package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.Calendar;

/**
 * DataHandler class
 *
 * This class triggers the collection of data and the calculations done on said data.
 * TODO Put constants in a nice place somewhere
 * TODO Check if the pass by value structure of Java does what we want it to do here
 *
 * @author Thomas Beckers
 * @since 2019-02-28
 */
class DataHandler {

    private static DataInterval currentDataInterval;

    /**
     * This starts all measurement loops.
     * The measurement object that all data is stored to is accessed
     * via the MeasurementControl.getCurrentMeasurement().
     */
    public static void startMeasuring() {
        onStartNewInterval();
    }

    /**
     * This gets called when our interval is over.
     * A new DataInterval class is created.
     */
    private static void onStartNewInterval() {
        if (currentDataInterval != null) {
            currentDataInterval.onIntervalEnd();
            MeasurementControl.getCurrentMeasurement().addDataInterval(currentDataInterval);
        }

        currentDataInterval = new DataInterval();
    }

    /**
     * Gets called by the accellerometer hardware listener.
     * If our interval has passed, a new interval is created.
     * In this case the datapoint gets added to the new interval.
     *
     * @param dataPoint The datapoint that was created
     */
    public static void onReceiveDataPoint(DataPoint<int[]> dataPoint) {
        // Check if our interval has passed.
        // Date.getTime() returns total time in milliseconds.
        if (MeasurementControl.getCurrentMeasurement().dateStart.getTime() + Constants.intervalInMilliseconds >= Calendar.getInstance().getTime().getTime()) {
            onStartNewInterval();
        }

        // Add datapoint.
        currentDataInterval.addDataPoint(dataPoint);
    }

    /**
     * This stops all measurement loops.
     */
    public static void stopMeasuring() {
        MeasurementControl.getCurrentMeasurement().onStopMeasuring();
    }

}