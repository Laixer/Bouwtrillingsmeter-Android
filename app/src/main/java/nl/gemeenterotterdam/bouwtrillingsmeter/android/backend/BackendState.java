package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This enum indicates the state of the backend.
 */
public enum BackendState {
    NONE,
    BROWSING_APP,
    PREPARING_MEASUREMENT,
    AWAITING_PHONE_FLAT,
    MEASURING,
    FINISHED_MEASUREMENT,
    UNSUPPORTED_HARDWARE
}
