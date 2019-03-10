package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class is used to store a dominant frequency found in a {@link DataInterval}.
 * The created instance is also stored in said {@link DataInterval}.
 * It's nothing more than an elegant way of storing data.
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

    /**
     * This is used because our calculator is an idiot.
     * TODO Remove this.
     *
     * @return A dominant frequencies object with fake random values.
     */
    public static  DominantFrequencies getMockDominantFrequencies() {
        int[] frequencies = new int[]{(int)(Math.random() * 25), (int)(Math.random() * 25), (int)(Math.random() * 25)};
        float[] velocities = new float[]{(float)Math.random(), (float)Math.random(), (float)Math.random()};
        boolean[] exceedsLimit = new boolean[3];
        for (int i = 0; i < 3; i++) {
            exceedsLimit[i] = false; //(Math.random() < 0.05) ? true : false;
        }

        return new DominantFrequencies(frequencies, velocities, exceedsLimit);
    }

}