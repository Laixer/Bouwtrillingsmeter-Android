package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.io.Serializable;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class is used to store a dominant frequency found in a {@link DataInterval}.
 * The created instance is also stored in said {@link DataInterval}.
 * It's nothing more than an elegant way of storing data.
 */
public class DominantFrequencies implements Serializable {

    private float[] frequencies;
    private float[] velocities;
    private boolean[] exceedsLimit;

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

    public float[] getFrequencies() {
        return frequencies;
    }

    public float[] getVelocities() {
        return velocities;
    }

    public boolean[] getExceedsLimit() {
        return exceedsLimit;
    }
}