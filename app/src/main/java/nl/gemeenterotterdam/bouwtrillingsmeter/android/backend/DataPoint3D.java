package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;


import java.io.Serializable;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class holds a given datapoint.
 * If we are operating in the time xAxisValue, xAxisValue is of type long
 * If we are operating in the frequency xAxisValue, xAxisValue is of type int or int[].
 * All other functions regarding data use an ArrayList of these datapoints.
 */

public class DataPoint3D<X> implements Serializable {

    public X xAxisValue;
    public float[] values;

    /**
     * Constructor
     *
     * @param xAxisValue The xAxisValue. Long is used for time. Int or int[] is used for frequency.
     * @param values     The corresponding data values
     */
    public DataPoint3D(X xAxisValue, float[] values) {
        this(xAxisValue, values[0], values[1], values[2]);
    }

    /**
     * Constructor
     *
     * @param xAxisValue The xAxisValue, being of type a Date (time xAxisValue) or float[x, y, z] (frequency xAxisValue)
     * @param x          Data x
     * @param y          Data y
     * @param z          Data z
     */
    public DataPoint3D(X xAxisValue, float x, float y, float z) {
        this.xAxisValue = xAxisValue;
        float[] values = new float[3];
        values[0] = x;
        values[1] = y;
        values[2] = z;
        this.values = values;
    }

    /**
     * Converts our generic x axis value to a float.
     *
     * @return X axis  value as float
     */
    public float xAxisValueAsFloat() {
        try {
            Long value = (long) xAxisValue;
            double x = value;
            return (float) x;
        } catch (ClassCastException e) {
            /* Do nothing */
        }

        try {
            Double value = (double) xAxisValue;
            double x = value;
            return (float) x;
        } catch (ClassCastException e) {
            /* Do nothing */
        }

        throw new RuntimeException("Could not convert x axis value");
    }

}




