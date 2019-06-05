package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * Used by {@link StorageControl}.
 */
public class StorageWriteException extends Exception {

    /**
     * Create a new instance of this exception.
     *
     * @param message The message to display
     */
    public StorageWriteException(String message) {
        super(message);
    }

}
