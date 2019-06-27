package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This enum indicates the state of the backend.
 */
public enum BackendState {
    INITIALIZING,
    AWAITING_HARDWARE_VALIDATION,
    BROWSING_APP,
    PREPARING_MEASUREMENT,
    AWAITING_START,
    MEASURING,
    UNSUPPORTED_HARDWARE
}
