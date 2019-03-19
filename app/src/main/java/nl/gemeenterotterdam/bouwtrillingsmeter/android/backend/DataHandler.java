package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class triggers the collection of data.
 * It also triggers the calculations done on said data, which are executed by the {@link Calculator}.
 * Data is received in the form of a {@link DataPoint3DTime}.
 * The collection and measurements are stored in a {@link DataInterval} object.
 * These intervals are stored within a {@link Measurement} object.
 */
public class DataHandler implements AccelerometerListener {

    private static boolean currentlyMeasuring;
    private static DataInterval currentDataInterval;
    private static DataInterval lastCalculatedDataInterval;
    private static int currentDataIntervalIndex;
    private static int lastExceedingIndex;
    private static ArrayList<Integer> indexesToBeCleared;

    private static ArrayList<DataIntervalClosedListener> dataIntervalClosedListeners;

    /**
     * This is used as a workaround to implement the {@link AccelerometerListener} interface
     * in a static context.
     */
    private static DataHandler listenerInstance;

    /**
     * Initializes this static class.
     * This adds this class as an accelerometer listener.
     * Call {@link AccelerometerControl#initialize()} first!
     */
    static void initialize() {
        currentlyMeasuring = false;
        currentDataInterval = null;
        listenerInstance = new DataHandler();

        dataIntervalClosedListeners = new ArrayList<DataIntervalClosedListener>();

        AccelerometerControl.addListener(listenerInstance);
    }

    /**
     * Adds a {@link DataIntervalClosedListener} listener.
     * This will be called every time a data interval is closed.
     *
     * @param listener The listener object. Don't forget to @Override!
     */
    public static void addDataIntervalClosedListener(DataIntervalClosedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener to be added to data interval closed listeners can not be null.");
        }

        dataIntervalClosedListeners.add(listener);
    }

    /**
     * This triggers all listeners, which have the
     * {@link DataIntervalClosedListener} interface.
     *
     * @param dataInterval The closed datainterval, including all calculations.
     */
    private static void triggerDataIntervalClosedEvent(DataInterval dataInterval) {
        for (DataIntervalClosedListener listener : dataIntervalClosedListeners) {
            if (listener != null) {
                listener.onDataIntervalClosed(dataInterval);
            }
        }
    }

    /**
     * Check if we are measuring right now.
     *
     * @return True if we are currently measuring.
     */
    static boolean isCurrentlyMeasuring() {
        return currentlyMeasuring;
    }

    /**
     * This starts all measurement loops.
     * The measurement object that all data is stored to is accessed
     * via the MeasurementControl.getCurrentMeasurement().
     */
    static void startMeasuring() {
        // Give an error if we have no measurement object
        if (MeasurementControl.getCurrentMeasurement() == null) {
            throw new NullPointerException("We have no current measurement object!");
        }

        currentlyMeasuring = true;
        currentDataIntervalIndex = 0;
        lastExceedingIndex = -1;
        indexesToBeCleared = new ArrayList<Integer>();
        currentDataInterval = new DataInterval(currentDataIntervalIndex);
    }

    /**
     * This gets called when a new interval starts.
     * A new DataInterval object is created.
     * <p>
     * This also calls {@link #clearAbundantDataPoints()}.
     * This also calls {@link #performIntervalCalculations(DataInterval)}.
     * This does not check if we exceed limits. This is done
     * async in the {@link #performIntervalCalculations(DataInterval)} function.
     */
    private static void onStartNewInterval() {
        // Create and add our new interval
        currentDataInterval.onIntervalEnd();
        performIntervalCalculations(currentDataInterval);
        MeasurementControl.getCurrentMeasurement().addDataInterval(currentDataInterval);

        // Clear abundant datapoints in our current measurement.
        clearAbundantDataPoints();

        currentDataInterval = new DataInterval(currentDataIntervalIndex);
        currentDataIntervalIndex += 1;

        // Trigger our interval closed event
        triggerDataIntervalClosedEvent(lastCalculatedDataInterval);
    }

    /**
     * This will attempt to clear any datapoints in the {@link #currentDataInterval} that are not relevant for our exceeded limits.
     * Intervals which have to be cleared are added to the {@link #indexesToBeCleared}.
     * This is because {@link #performIntervalCalculations(DataInterval)} is done in a separate thread.
     * In this way we can work thread safe.
     * <p>
     * How many data intervals will be saved is determined by {@link Constants#saveDataIntervalsBeforeExceeding}
     * and by {@link Constants#saveDataIntervalsBeforeExceeding}.
     * <p>
     * TODO First interval and last x intervals are never cleared.
     */
    private static void clearAbundantDataPoints() {
        // See which indexes have to be cleared
        int indexSafeInterval = Constants.saveDataIntervalsBeforeExceeding + Constants.saveDataIntervalsAfterExceeding;
        int indexSafeBefore = Math.max(0, lastExceedingIndex - Constants.saveDataIntervalsBeforeExceeding);
        int indexSafeAfter = lastExceedingIndex + Constants.saveDataIntervalsAfterExceeding;

        int indexLookingAt = Math.max(0, currentDataIntervalIndex - indexSafeInterval);
        if (lastExceedingIndex != -1 && indexLookingAt < indexSafeBefore || indexLookingAt > indexSafeAfter) {
            indexesToBeCleared.add(indexLookingAt);
        }

        // Attempt to clear all that has to be cleared
        ArrayList<DataInterval> dataIntervals = MeasurementControl.getCurrentMeasurement().dataIntervals;
        ArrayList<Integer> indexesClearedSuccessfully = new ArrayList<Integer>();
        for (int index : indexesToBeCleared) {
            if (dataIntervals.get(index).attemptDeleteDataPoints()) {
                indexesClearedSuccessfully.add(index);
            }
        }

        // Remove all cleared indexes in a clean way
        // Just putting remove(index) would not work
        for (int index : indexesClearedSuccessfully) {
            indexesToBeCleared.remove(new Integer(index));
        }
    }

    /**
     * This performs all calculations on the data within an interval.
     * All these calculations are done within their own thread.
     * <p>
     * This also checks if we exceeded any limit.
     * If so, this calls {@link Backend#onExceedLimit()}.
     * TODO Will this break if we attempt to delete any datapoints while calculations are in progress?
     *
     * @param dataInterval The data interval.
     */
    private static void performIntervalCalculations(DataInterval dataInterval) {
        // Edge cases
        if (dataInterval.dataPoints3DAcceleration.size() == 0) {
            System.out.println("No datapoints were added to this interval.");
            return;
        }

        // Create a thread for all calculations
        final DataInterval thisDataInterval = dataInterval;
        new Thread(new Runnable() {
            public void run() {
                // Lock dataInterval
                thisDataInterval.onThreadCalculationsStart();

                // Trigger all calculations
//                float[] maxAccelerations = Calculator.maxValueInArray(thisDataInterval.dataPoints3DAcceleration);

                ArrayList<DataPoint3D<int[]>> fftAccelerations = Calculator.FFT(thisDataInterval.dataPoints3DAcceleration);
                int[] maxFrequencies = Calculator.maxFrequencies(fftAccelerations);

                ArrayList<DataPoint3D<int[]>> velocitiesFreqDomain = Calculator.calcVelocityFreqDomain(fftAccelerations);
                ArrayList<DataPoint3D<int[]>> limitValues = Calculator.limitValue(velocitiesFreqDomain);
                DominantFrequencies dominantFrequencies = Calculator.getDominantFrequencies(limitValues, velocitiesFreqDomain);

                float[] maxVelocities = Calculator.maxValueInArray(velocitiesFreqDomain);
                maxVelocities = Calculator.addMargin(maxVelocities);

                // Write relevant calculations
//                thisDataInterval.maxAccelerations = maxAccelerations;
                thisDataInterval.dominantFrequencies = dominantFrequencies;
                thisDataInterval.maxFrequencies = maxFrequencies;
                thisDataInterval.maxVelocities = maxVelocities;

                // Check if we exceed any limits
                if (thisDataInterval.isExceedingLimit()) {
                    lastExceedingIndex = thisDataInterval.index;
                    Backend.onExceedLimit();
                }

                // Unlock this data interval
                thisDataInterval.onThreadCalculationsEnd();

                // Save calculated data interval
                lastCalculatedDataInterval = thisDataInterval;
            }
        }).start();
    }

    /**
     * Gets called by the accellerometer hardware listener.
     * If our interval has passed, a new interval is created.
     * In this case the datapoint gets added to the new interval.
     */
    @Override
    public void onReceivedData(float x, float y, float z) {
        // If we are measuring
        if (isCurrentlyMeasuring() && currentDataInterval != null) {
            // Check if our interval has passed.
            long timePassed = -currentDataInterval.dateStart.getTime() + Calendar.getInstance().getTime().getTime();
            long interval = Constants.intervalInMilliseconds;
            if (timePassed > interval) {
                onStartNewInterval();
            }

            // Calculate the time for this datapoint
            long startTime = MeasurementControl.getCurrentMeasurement().getStartTimeInMillis();
            long currentTime = Calendar.getInstance().getTimeInMillis();
            long dataPointTime = currentTime - startTime;

            // Push the datapoint
            DataPoint3DTime dataPoint3DTime = new DataPoint3DTime(dataPointTime, x, y, z);
            currentDataInterval.addDataPoint3D(dataPoint3DTime);
        }
    }

    /**
     * This stops all measurement loops.
     */
    static void stopMeasuring() {
        if (currentlyMeasuring == false) {
            throw new IllegalStateException("Attempted to stop measuring when we were not measuring.");
        }

        currentlyMeasuring = false;
    }

}