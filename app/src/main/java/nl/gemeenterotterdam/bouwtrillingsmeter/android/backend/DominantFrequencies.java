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

        // TODO Remove this debug
        this.exceedsLimit[2] = true;
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