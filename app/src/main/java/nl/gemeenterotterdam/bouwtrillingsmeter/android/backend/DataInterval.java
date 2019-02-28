package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Thomas Beckers
 * @version 1.0
 * <p>
 * DataInterval class
 * <p>
 * This class holds a reference to all datapoints within a given time interval
 * This also holds all result values of calculations (fft etc)
 * <p>
 * Based on code by Marijn Otte
 */
public final class DataInterval {

    public final ArrayList<DataPoint<int[]>> dataPoints;
    public Date dateStart;
    public Date dateEnd;

    public final float[] maxAccelerations;
    public final float[] maxVelocities;
    public final int[] maxFrequencies;
    public final DominantFrequency dominantFrequency;

    /**
     * Constructor
     * TODO Put all calculations in here?
     */
    public DataInterval() {
        dataPoints = new ArrayList<DataPoint<int[]>>();
        dateStart = Calendar.getInstance().getTime();

        maxAccelerations = null;
        maxVelocities = null;
        maxFrequencies = null;
        dominantFrequency = null;
    }

    /**
     * Adds a datapoint to our arraylist
     *
     * @param dataPoint The datapoint to add
     */
    public void addDataPoint(DataPoint<int[]> dataPoint) {
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

}
