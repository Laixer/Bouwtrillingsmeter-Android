package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class contains our backend constants.
 */
public class Constants {

    /**
     * This indicates how long one interval of measuring will take.
     * The required value is 1 second.
     */
    public static final int intervalInMilliseconds = 1000;

    /**
     * When set to true, we delete datapoints in our {@link DataInterval} which do not
     * correspond to any exceeding events. We can set the amount of data saved using
     * {@link #saveDataIntervalsBeforeExceeding} and {@link #saveDataIntervalsAfterExceeding}.
     * When set to false, all data is saved.
     */
    public static final boolean clearAbundantDataPoints = false;

    /**
     * When we exceed a limit, this value indicates how many intervals before that moment will have their datapoints saved.
     * All other data is deleted.
     */
    public static final int saveDataIntervalsBeforeExceeding = 3;

    /**
     * When we exceed a limit, this value indicates how many intervals after that moment will have their datapoints saved.
     * All other data is deleted.
     */
    public static final int saveDataIntervalsAfterExceeding = 1;


}
