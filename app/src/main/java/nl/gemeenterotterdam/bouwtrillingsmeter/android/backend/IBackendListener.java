package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

public interface IBackendListener {

    /**
     * Gets called when the state of the backend changes.
     *
     * @param newBackendState The new state
     */
    void onBackendStateChanged(BackendState newBackendState);

    /**
     * Gets called when a limit is exceeded.
     */
    void onExceededLimit();
}
