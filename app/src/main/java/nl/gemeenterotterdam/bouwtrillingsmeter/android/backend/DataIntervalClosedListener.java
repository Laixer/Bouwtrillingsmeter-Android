package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This is used to implement a frontend listener for when a datainterval is closed.
 * This can be used to update graphs etc.
 */
public interface DataIntervalClosedListener {

    /**
     * This gets called when the {@link DataInterval} is closed.
     * All listeners get called from the {@link DataHandler} class.
     * Do not forget to add the @Override tag to this function!
     *
     * @param dataInterval
     */
    public void onDataIntervalClosed(DataInterval dataInterval);

}
