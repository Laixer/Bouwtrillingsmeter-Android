package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * @author Thomas Beckers
 * @version 1.0
 *
 * DominantFrequency class
 *
 * This class is used to store a dominant frequency found in a measurement.
 * Nothing more than an elegant way of storing data.
 */
class DominantFrequency {

    public int[] frequencies;
    public float[] velocities;
    public boolean[] exceedsLimit;

    /**
     * Constructor
     */
    public DominantFrequency(int[] frequencies, float[] velocities, boolean[] exceedsLimit) {
        this.frequencies = frequencies;
        this.velocities = velocities;
        this.exceedsLimit = exceedsLimit;
    }

}