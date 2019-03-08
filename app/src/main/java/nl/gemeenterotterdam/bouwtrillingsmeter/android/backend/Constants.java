package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class contains our constants.
 * This might not be the best structure.
 * <p>
 * TODO Rethink this structure, this is temporary
 */
public class Constants {

    /**
     * This indicates how long one interval of measuring will take.
     * The required value is 1 second.
     */
    public static final int intervalInMilliseconds = 1000;

    /**
     * When we exceed a limit, this value indicates how many intervals before that moment will have their datapoints saved.
     * All other data is deleted.
     */
    public static final int saveDataIntervalsBeforeExceeding = 10;

    /**
     * When we exceed a limit, this value indicates how many intervals after that moment will have their datapoints saved.
     * All other data is deleted.
     */
    public static final int saveDataIntervalsAfterExceeding = 2;


}
