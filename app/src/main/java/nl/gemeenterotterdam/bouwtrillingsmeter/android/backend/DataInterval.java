package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class holds a reference to all {@link DataPoint}s within a given time interval.
 * This also holds all result values of calculations (fft etc)
 */
public class DataInterval {

    public int index;
    public ArrayList<DataPoint<Date>> dataPoints;
    public Date dateStart;
    public Date dateEnd;

    public float[] maxAccelerations;
    public float[] maxVelocities;
    public int[] maxFrequencies;
    public DominantFrequencies dominantFrequencies;

    private boolean isLockedByThread;

    /**
     * Constructor
     */
    public DataInterval(int index) {
        this.index = index;

        dataPoints = new ArrayList<DataPoint<Date>>();
        dateStart = Calendar.getInstance().getTime();
        isLockedByThread = false;
    }

    /**
     * Adds a datapoint to our arraylist
     *
     * @param dataPoint The datapoint to add
     */
    public void addDataPoint(DataPoint<Date> dataPoint) {
        dataPoints.add(dataPoint);
    }

    /**
     * This gets called when the interval is over.
     * This class does not call this function by itself.
     * This function gets called by the DataHandler.
     */
    public void onIntervalEnd() {
        dateEnd = Calendar.getInstance().getTime();
    }

    /**
     * Checks if we have exceeded any limits within our interval.
     * The checks are done on the {@link #dominantFrequencies}.
     * TODO Remove the debug thingy with chance.
     *
     * @return True if we have exceeded any limits.
     */
    public boolean isExceedingLimit() {
        if (index == 10) {
            return true;
        } else {
            return false;
        }

        /**
        if (dominantFrequencies == null) {
            double chance = Math.random();
            if (chance < 0.1) {
                return true;
            } else {
                return false;
            }
        }

        for (boolean bool : dominantFrequencies.exceedsLimit) {
           if (bool == true) {
               return true;
           }
        }

        return false;
         */
    }

    /**
     * This locks our object when the calculations thread is working on it.
     */
    public void onThreadCalculationsStart() {
        isLockedByThread = true;
    }

    /**
     * This unlocks our object when the calculation thread is done with it.
     */
    public void onThreadCalculationsEnd() {
        isLockedByThread = false;
    }

    /**
     * This will clear our {@link #dataPoints} array.
     * A clear is only performed if we are not {@link #isLockedByThread}.
     * This is done to save memory and prevent sending large files across the internet.
     */
    public boolean attemptDeleteDataPoints() {
        if (isLockedByThread) {
            return false;
        }

        else {
            dataPoints.clear();
            return true;
        }
    }

}
