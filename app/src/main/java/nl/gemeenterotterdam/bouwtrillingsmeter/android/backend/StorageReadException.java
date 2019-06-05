package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * Used by {@link StorageControl}.
 */
public class StorageReadException extends Exception {

    /**
     * Create a new instance of this exception.
     *
     * @param message The message to display
     */
    public StorageReadException(String message) {
        super(message);
    }

}
