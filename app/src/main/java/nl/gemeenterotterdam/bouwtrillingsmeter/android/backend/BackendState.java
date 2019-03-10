package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

public enum BackendState {
    NONE,
    BEFORE_MEASURING,
    AWAITING_PHONE_FLAT,
    MEASURING,
    MEASURING_END
}
