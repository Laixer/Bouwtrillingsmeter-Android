package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class is used to store a dominant frequency found in a {@link DataInterval}.
 * The created instance is also stored in said {@link DataInterval}.
 * It's nothing more than an elegant way of storing data.
 */
public class DominantFrequencies {

    public float[] frequencies;
    public float[] velocities;
    public boolean[] exceedsLimit;

    /**
     * Constructor
     */
    public DominantFrequencies(float[] frequencies, float[] velocities, boolean[] exceedsLimit) {
        this.frequencies = frequencies;
        this.velocities = velocities;
        this.exceedsLimit = exceedsLimit;
    }

    /**
     * Checks if this object exceeds any limits
     *
     * @return True if we exceed limits
     */
    public boolean isExceedingLimit() {
        for (boolean b : exceedsLimit) {
            if (b) {
                return true;
            }
        }

        return false;
    }

}