package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * @author Thomas Beckers
 * @version 1.0
 *
 * DominantFrequencies class
 *
 * This class is used to store a dominant frequency found in a measurement.
 * Nothing more than an elegant way of storing data.
 */
class DominantFrequencies {

    public int[] frequencies;
    public float[] velocities;
    public boolean[] exceedsLimit;

    /**
     * Constructor
     */
    public DominantFrequencies(int[] frequencies, float[] velocities, boolean[] exceedsLimit) {
        this.frequencies = frequencies;
        this.velocities = velocities;
        this.exceedsLimit = exceedsLimit;
    }

}