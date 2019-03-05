package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

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

    /**
     * Constructor
     *
     * @param domain The domain, being of type a Date (time domain) or float[x, y, z] (frequency domain)
     * @param values The corresponding data values
     */
    public DataPoint(X domain, float[] values) {
        this.domain = domain;
        this.values = values;
    }

}
