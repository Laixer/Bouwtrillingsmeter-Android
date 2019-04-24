package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.os.Environment;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend.Utility;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class reads and writes files to and from the phone memory.
 * TODO Implement Query Free Space
 */
class StorageControl {

    /**
     * This retrieves all saved measurements.
     *
     * @return All retrieved non-null measurements
     */
    static ArrayList<Measurement> retrieveAllSavedMeasurements() {
        File internalStorage = Backend.applicationContext.getFilesDir();
        File[] files = internalStorage.listFiles();

        ArrayList<Measurement> measurements = new ArrayList<Measurement>(files.length);

        System.out.println(String.format("We have %s files.", files.length));
        for (File file : files) {
            System.out.println(file.getName());
            Object object = readObject(file.getName());

            // Try to convert to measurement
            try {
                Measurement measurement = (Measurement) object;
                if (measurement != null) {
                    measurements.add(measurement);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.toString());
            }
        }

        return measurements;
    }

    /**
     * Writes an object to the internal storage.
     * Always overwrites.
     *
     * @param object   The object
     * @param fileName The filename
     */
    static void writeObject(Object object, String fileName) {
        try {
            FileOutputStream fileOutputStream = Backend.applicationContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(object);

            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Reads an object from the internal storage.
     *
     * @param fileName The filename
     * @return Null if no object is found
     */
    private static Object readObject(String fileName) {
        Object object = null;
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            fileInputStream = Backend.applicationContext.openFileInput(fileName);
            objectInputStream = new ObjectInputStream(fileInputStream);
            object = objectInputStream.readObject();
            fileInputStream.close();
            objectInputStream.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return object;
    }

}
