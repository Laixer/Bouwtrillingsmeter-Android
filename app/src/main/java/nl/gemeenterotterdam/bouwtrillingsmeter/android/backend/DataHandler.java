package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * DataHandler class
 *
 * This class triggers the collection of data and the calculations done on said data.
 * TODO Put constants in a nice place somewhere
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
        currentDataInterval = new DataInterval();
        MeasurementControl.getCurrentMeasurement().addDataInterval(currentDataInterval);
    }

    /**
     * Gets called by the accellerometer hardware listener
     *
     * @param dataPoint The datapoint that was created
     */
    public static void onRecieveDataPoint(DataPoint<int[]> dataPoint) {
        currentDataInterval.addDataPoint(dataPoint);
    }

    /**
     * This stops all measurement loops.
     */
    public static void stopMeasuring() {

    }

}
