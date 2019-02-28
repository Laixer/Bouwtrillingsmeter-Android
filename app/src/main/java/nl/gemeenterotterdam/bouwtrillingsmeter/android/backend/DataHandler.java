package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * DataHandler class
 * <p>
 * This class triggers the collection of data.
 * This class also triggers the calculations done on said data.
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
     * This gets called when a new interval starts.
     * A new DataInterval object is created.
     */
    private static void onStartNewInterval() {
        if (currentDataInterval != null) {
            currentDataInterval.onIntervalEnd();
            MeasurementControl.getCurrentMeasurement().addDataInterval(currentDataInterval);
        }

        currentDataInterval = new DataInterval();
    }

    /**
     * This performs all calculations on the data within an interval.
     *
     * @param dataInterval The data interval.
     */
    private static void performIntervalCalculations(DataInterval dataInterval) {
        // Edge cases
        if (dataInterval.dataPoints.size() == 0) {
            System.out.println("No datapoints were added to this interval.");
            return;
        }

        // Trigger all calculations
        float[] maxAccelerations = Calculator.MaxValueInArray(dataInterval.dataPoints);

        ArrayList<DataPoint<int[]>> fftAccelerations = Calculator.FFT(dataInterval.dataPoints);
        int[] maxFrequencies = Calculator.MaxFrequency(fftAccelerations);

        ArrayList<DataPoint<int[]>> velocitiesFreqDomain = Calculator.calcVelocityFreqDomain(fftAccelerations);
        ArrayList<DataPoint<int[]>> limitValues = Calculator.limitValue(velocitiesFreqDomain);
        DominantFrequencies dominantFrequencies = Calculator.domFreq(limitValues, velocitiesFreqDomain);

        float[] maxVelocities = Calculator.MaxValueInArray(velocitiesFreqDomain);
        maxVelocities = Calculator.addMargin(maxVelocities);

        // Write relevant calculations
        dataInterval.maxAccelerations = maxAccelerations;
        dataInterval.dominantFrequencies = dominantFrequencies;
        dataInterval.maxFrequencies = maxFrequencies;
        dataInterval.maxVelocities = maxVelocities;
    }

    /**
     * Gets called by the accellerometer hardware listener.
     * If our interval has passed, a new interval is created.
     * In this case the datapoint gets added to the new interval.
     *
     * @param dataPoint The datapoint that was created
     */
    public static void onReceiveDataPoint(DataPoint<Date> dataPoint) {
        // Check if our interval has passed.
        // Date.getTime() returns total time in milliseconds.
        if (MeasurementControl.getCurrentMeasurement().dateStart.getTime() + Constants.intervalInMilliseconds >= Calendar.getInstance().getTime().getTime()) {
            onStartNewInterval();
        }

        currentDataInterval.addDataPoint(dataPoint);
    }

    /**
     * This stops all measurement loops.
     */
    public static void stopMeasuring() {
        MeasurementControl.getCurrentMeasurement().onStopMeasuring();
    }

}