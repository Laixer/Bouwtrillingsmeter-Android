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
public class DataInterval {

    public ArrayList<DataPoint<Date>> dataPoints;
    public Date dateStart;
    public Date dateEnd;

    public float[] maxAccelerations;
    public float[] maxVelocities;
    public int[] maxFrequencies;
    public DominantFrequencies dominantFrequencies;

    /**
     * Constructor
     */
    public DataInterval() {
        dataPoints = new ArrayList<DataPoint<Date>>();
        dateStart = Calendar.getInstance().getTime();
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

}
