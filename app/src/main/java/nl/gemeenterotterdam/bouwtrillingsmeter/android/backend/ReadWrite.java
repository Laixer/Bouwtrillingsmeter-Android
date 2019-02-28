package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;

import org.jetbrains.annotations.Nullable;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend.Utility;

/**
 * This class reads and writes files to and from the phone memory
 */
class ReadWrite {

    /**
     * This attempts to write a measurement to the internal storage
     * This always overwrites existing objects
     *
     * @param context     The context from which we are calling
     * @param measurement The measurement
     * @param key         The key by which we write and retrieve this
     * @return True if succesful, false if not
     */
    public static boolean TryWriteMeasurement(Context context, Measurement measurement, String key) {
        try {
            InternalStorage.writeObject(context, key, measurement);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Attempts to read a measurement based on a key
     *
     * @param context The context from which we are calling
     * @param key     The key
     * @return The measurement if successful, null if not
     */
    @Nullable
    public static Measurement TryReadMeasurement(Context context, String key) {
        try {
            Measurement result = (Measurement) InternalStorage.readObject(context, key);
            return result;
        } catch (Exception e) {
            return null;
        }
    }



    /**
     * Used for debug purposes
     * TODO Remove this
     */
    public static void DebugFunction() {
        String key = "lalalalala";

        Measurement writeMeasurement = new Measurement("measurement 1");
        TryWriteMeasurement(Utility.ApplicationContext, writeMeasurement, key);
        Measurement readMeasurement = TryReadMeasurement(Utility.ApplicationContext, key);

        System.out.println("Hi there");

        writeMeasurement = new Measurement("measurement 2");
        TryWriteMeasurement(Utility.ApplicationContext, writeMeasurement, key);
        readMeasurement = TryReadMeasurement(Utility.ApplicationContext, key);

        System.out.println("Hi there again");
    }

}
