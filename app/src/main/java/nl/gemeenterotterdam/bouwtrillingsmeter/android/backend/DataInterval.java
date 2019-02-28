package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;

/**
 * @author Thomas Beckers
 * @version 1.0
 *
 * DataInterval class
 *
 * This class holds a reference to all datapoints within a given time interval
 * This also holds all result values of calculations (fft etc)
 *
 * Based on code by Marijn Otte
 *
 */
public final class DataInterval {

    public final ArrayList<DataPoint<int[]>> dataPoints;

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

        maxAccelerations = null;
        maxVelocities = null;
        maxFrequencies = null;
        dominantFrequency = null;
    }

}
