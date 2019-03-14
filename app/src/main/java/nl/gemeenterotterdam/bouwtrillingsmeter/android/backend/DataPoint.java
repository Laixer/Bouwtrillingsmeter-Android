package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;


import android.app.Application;

import java.util.Calendar;
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
 * DataPoint, used for storage.
 * X: Date if DataPoint in time domain, float[] (x, y, z values of frequencies) in frequencydomain)
 * values: x, y, z values of data, usually velocity or acceleration
 * <p>
 * Based on code by Marijn Otte
 */

public class DataPoint<X> {

    public X domain;
    public float[] values;
    public long time;

    /**
     * Constructor
     *
     * @param domain The domain, being of type a Date (time domain) or float[x, y, z] (frequency domain)
     * @param values The corresponding data values
     */
    public DataPoint(X domain, float[] values) {
        this(domain, values[0], values[1], values[2]);
    }

    /**
     * Constructor
     *
     * @param domain The domain, being of type a Date (time domain) or float[x, y, z] (frequency domain)
     * @param x      Data x
     * @param y      Data y
     * @param z      Data z
     */
    public DataPoint(X domain, float x, float y, float z) {
        this.domain = domain;
        float[] values = new float[3];
        values[0] = x;
        values[1] = y;
        values[2] = z;
        this.values = values;

        /**
         * If we are dealing with a {@link Date} object,
         * our domain variable is the starttime of our {@link Measurement}.
         * We need to calculate the time since the starttime of the measurement
         * and set it as our domain, for the graphs to work properly.
         */
        if (domain instanceof Date) {
            // TODO Implement
        }

    }

}
