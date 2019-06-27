package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * Thrown when we don't have sufficient hardware.
 */
public class UnsupportedHardwareException extends Exception {

    public UnsupportedHardwareException(String message) {
        super(message);
    }

}
