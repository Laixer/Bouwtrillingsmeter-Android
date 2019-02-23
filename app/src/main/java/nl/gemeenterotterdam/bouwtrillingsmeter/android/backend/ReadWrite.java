package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.icu.util.Measure;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend.Utility;

/**
 * This class reads and writes files to and from the phone memory
 */
public class ReadWrite {

    /**
     * This attempts to write a measurement to the internal storage
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
     * @param key The key
     * @return The measurement if succesful, null if not
     */
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
        Measurement writeMeasurement = new Measurement("measurement 1");
        String key = "lalalalala";

        TryWriteMeasurement(Utility.ApplicationContext, writeMeasurement, key);

        Measurement readMeasurement = TryReadMeasurement(Utility.ApplicationContext, key);

        System.out.println("Hi there");
    }

}
