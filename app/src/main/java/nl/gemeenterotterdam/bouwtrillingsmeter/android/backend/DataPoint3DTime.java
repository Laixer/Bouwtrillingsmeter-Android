package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;


import java.util.Date;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class holds a given datapoint.
 * If we are operating in the time domain, domain is of type Date.
 * If we are operating in the frequency domain, domain is of type float[x, y, z], where x y z are frequencies.
 * All other functions regarding data use an ArrayList of these datapoints.
 * <p>
 * OLD DOCUMENTATION:
 * DataPoint3D, used for storage.
 * X: Date if DataPoint3D in time domain, float[] (x, y, z values of frequencies) in frequencydomain)
 * values: x, y, z values of data, usually velocity or acceleration
 * <p>
 * Based on code by Marijn Otte
 */

public class DataPoint3DTime {

    public long time;
    public float[] values;

    /**
     * Constructor
     *
     * @param time   The time
     * @param values The corresponding data values in 3 dimensions. x=0, y=1, z=2
     */
    public DataPoint3DTime(long time, float[] values) {
        this(time, values[0], values[1], values[2]);
    }

    /**
     * Constructor
     *
     * @param time The time
     * @param x    Data x
     * @param y    Data y
     * @param z    Data z
     */
    public DataPoint3DTime(long time, float x, float y, float z) {
        float[] values = new float[3];
        values[0] = x;
        values[1] = y;
        values[2] = z;
        this.values = values;
        this.time = time;
    }

}
