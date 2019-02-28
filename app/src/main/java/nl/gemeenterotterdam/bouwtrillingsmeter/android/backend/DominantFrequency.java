package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * DominantFrequency class
 *
 * This class is used to store a dominant frequency found in a measurement.
 * Nothing more than an elegant way of storing data.
 */
class DominantFrequency {

    private int frequency;
    private float velocity;
    private boolean exceedsLimit;

    /**
     * Constructor
     */
    public DominantFrequency(int frequency, float velocity, boolean exceedsLimit) {
        this.frequency = frequency;
        this.velocity = velocity;
        this.exceedsLimit = exceedsLimit;
    }

    public int getFrequency() {
        return frequency;
    }

    public float getVelocity() {
        return velocity;
    }

    public boolean isExceedsLimit() {
        return exceedsLimit;
    }

}
