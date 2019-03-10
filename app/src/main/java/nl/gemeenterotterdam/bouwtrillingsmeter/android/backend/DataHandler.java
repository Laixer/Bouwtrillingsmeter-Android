package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class triggers the collection of data.
 * It also triggers the calculations done on said data, which are executed by the {@link Calculator}.
 * Data is received in the form of a {@link DataPoint}.
 * The collection and measurements are stored in a {@link DataInterval} object.
 * These intervals are stored within a {@link Measurement} object.
 */
class DataHandler {

    private static boolean currentlyMeasuring;
    private static DataInterval currentDataInterval;
    private static int currentDataIntervalIndex;
    private static int lastExceedingIndex;
    private static ArrayList<Integer> indexesToBeCleared;

    /**
     * Check if we are measuring right now.
     *
     * @return True if we are currently measuring.
     */
    public static boolean isCurrentlyMeasuring() {
        return currentlyMeasuring;
    }

    /**
     * This starts all measurement loops.
     * The measurement object that all data is stored to is accessed
     * via the MeasurementControl.getCurrentMeasurement().
     */
    public static void startMeasuring() {
        currentlyMeasuring = true;
        currentDataIntervalIndex = 0;
        lastExceedingIndex = -1;
        indexesToBeCleared = new ArrayList<Integer>();
        onStartNewInterval();
    }

    /**
     * This gets called when a new interval starts.
     * A new DataInterval object is created.
     * This also calls {@link #clearAbundantDataPoints()}.
     * This also calls {@link Backend#onExceedLimit()}.
     * This also calls {@link #performIntervalCalculations(DataInterval)}.
     */
    private static void onStartNewInterval() {
        // Create and add our new interval
        if (currentDataInterval != null) {
            currentDataInterval.onIntervalEnd();
            performIntervalCalculations(currentDataInterval);
            MeasurementControl.getCurrentMeasurement().addDataInterval(currentDataInterval);
        }


        // Check if we exceed any limits
        if (currentDataInterval.isExceedingLimit()) {
            lastExceedingIndex = currentDataIntervalIndex;
            Backend.onExceedLimit();
        }

        // Clear abundant datapoints in our CURRENT measurement.
        clearAbundantDataPoints();

        currentDataInterval = new DataInterval();
        currentDataIntervalIndex += 1;
    }

    /**
     * This will attempt to clear any datapoints in the {@link #currentDataInterval} that are not relevant for our exceeded limits.
     * Intervals which have to be cleared are added to the {@link #indexesToBeCleared}.
     * This is because {@link #performIntervalCalculations(DataInterval)} is done in a separate thread.
     * In this way we can work thread safe.
     * <p>
     * How many data intervals will be saved is determined by {@link Constants#saveDataIntervalsBeforeExceeding}
     * and by {@link Constants#saveDataIntervalsBeforeExceeding}.
     */
    private static void clearAbundantDataPoints() {
        // See which indexes have to be cleared
        int indexSafeInterval = Constants.saveDataIntervalsBeforeExceeding + Constants.saveDataIntervalsAfterExceeding;
        int indexSafeBefore = Math.min(0, lastExceedingIndex - Constants.saveDataIntervalsBeforeExceeding);
        int indexSafeAfter = lastExceedingIndex + Constants.saveDataIntervalsAfterExceeding;

        int indexLookingAt = Math.min(0, currentDataIntervalIndex - indexSafeInterval);
        if (indexLookingAt < indexSafeBefore || indexLookingAt > indexSafeAfter) {
            indexesToBeCleared.add(indexLookingAt);
        }

        // Attempt to clear all that have to be cleared
        ArrayList<DataInterval> dataIntervals = MeasurementControl.getCurrentMeasurement().dataIntervals;
        for (int index : indexesToBeCleared) {
            if (dataIntervals.get(index).deleteDataPoints()) {
                dataIntervals.remove(index);
            }
        }
    }

    /**
     * This performs all calculations on the data within an interval.
     * All these calculations are done within their own thread.
     * TODO Will this break if we attempt to delete any datapoints while calculations are in progress?
     *
     * @param dataInterval The data interval.
     */
    private static void performIntervalCalculations(DataInterval dataInterval) {
        // Edge cases
        if (dataInterval.dataPoints.size() == 0) {
            System.out.println("No datapoints were added to this interval.");
            return;
        }

        final DataInterval thisDataInterval = dataInterval;
        new Thread(new Runnable() {
            public void run() {
                // Trigger all calculations
                float[] maxAccelerations = Calculator.maxValueInArray(thisDataInterval.dataPoints);

                ArrayList<DataPoint<int[]>> fftAccelerations = Calculator.FFT(thisDataInterval.dataPoints);
                int[] maxFrequencies = Calculator.maxFrequencies(fftAccelerations);

                ArrayList<DataPoint<int[]>> velocitiesFreqDomain = Calculator.calcVelocityFreqDomain(fftAccelerations);
                ArrayList<DataPoint<int[]>> limitValues = Calculator.limitValue(velocitiesFreqDomain);
                DominantFrequencies dominantFrequencies = Calculator.getDominantFrequencies(limitValues, velocitiesFreqDomain);

                float[] maxVelocities = Calculator.maxValueInArray(velocitiesFreqDomain);
                maxVelocities = Calculator.addMargin(maxVelocities);

                // Write relevant calculations
                thisDataInterval.maxAccelerations = maxAccelerations;
                thisDataInterval.dominantFrequencies = dominantFrequencies;
                thisDataInterval.maxFrequencies = maxFrequencies;
                thisDataInterval.maxVelocities = maxVelocities;
            }
        }).start();
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
        if (currentlyMeasuring == false) {
            throw new IllegalStateException("Attempted to stop measuring when we were not measuring.");
        }

        currentlyMeasuring = false;
        MeasurementControl.getCurrentMeasurement().onStopMeasuring();
    }

}