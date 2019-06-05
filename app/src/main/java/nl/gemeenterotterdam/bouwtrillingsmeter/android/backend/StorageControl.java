package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class reads and writes files to and from the phones internal storage.
 * TODO Make private, we only need this public for now to clear the saved measurements
 * TODO Implement failsafe handling for measurements if we cant save it for some reason.
 * TODO Now all files are in the sample place, might not want to do that
 */
public class StorageControl {

    /**
     * This is the location of our images
     * TODO Rethink this structure
     */
    private static final String DIRECTORY_IMAGES = "images";

    /**
     * Initializes our folder structure
     * TODO Do we need this?
     */
    static void initialize() {
//        File internalStorage = Backend.applicationContext.getFilesDir();
//        File folderMeasurements = new File(internalStorage, folderNameMeasurements);
//        folderMeasurements.mkdir();
//
//        File folderLists = new File(internalStorage, folderNameArrayLists);
//        folderLists.mkdir();
    }

    /**
     * This retrieves all saved measurements.
     *
     * @return All retrieved non-null measurements
     */
    static ArrayList<Measurement> retrieveAllSavedMeasurements() {
        File internalStorage = Backend.applicationContext.getFilesDir();
        File[] files = internalStorage.listFiles();

        ArrayList<Measurement> measurements = new ArrayList<Measurement>(files.length);

        for (File file : files) {
            Object object = readObject(file.getName());

            // Try to convert to measurement
            try {
                Measurement measurement = (Measurement) object;
                if (measurement != null) {
                    measurements.add(measurement);
                }
            } catch (ClassCastException e) {
                System.out.println("Not a measurement but this is not a problem: " + file.getName());
            } catch (Exception e) {
                System.out.println("StorageControl.retrieveAllSavedMeasurements: " + e.toString());
            }
        }

        return measurements;
    }

    /**
     * Retrieves an array list based on a name.
     * This returns an empty arraylist if we could not find the file or if it is incompatiable.
     *
     * @param fileName The filename
     * @param <T>      The type of our arraylist objects
     * @return The arraylist. It's empty if we can't find it or if we get a casting error.
     */
    static <T> ArrayList<T> retrieveArrayList(String fileName) {
        try {
            Object object = readObject(fileName);
            if (object == null) {
                return new ArrayList<T>();
            }
            ArrayList<T> result = (ArrayList<T>) object;
            return result;
        } catch (Exception e) {
            System.out.println("StorageControl.retrieveArrayList: " + e.getMessage());
        }

        return new ArrayList<T>();
    }

    /**
     * Writes a measurement to the internal memory
     *
     * @param measurement The measurement to write
     */
    static void writeMeasurement(Measurement measurement) {
        writeObject(measurement, measurement.getUID());
    }

    /**
     * Writes an arraylist to our internal memory
     *
     * @param arrayList The arraylist
     * @param fileName  The filename
     * @param <T>       The type of the arraylist
     */
    static <T> void writeArrayList(ArrayList<T> arrayList, String fileName) {
        writeObject(arrayList, fileName);
    }

    /**
     * Writes an object to the internal storage.
     * Always overwrites.
     *
     * @param object   The object
     * @param fileName The filename
     */
    private static void writeObject(Object object, String fileName) {
        try {
            FileOutputStream fileOutputStream = Backend.applicationContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(object);

            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            System.out.println("StorageControl.writeObject: " + e.toString());
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
            System.out.println("StorageControl.readObject: " + e.toString());
        }

        return object;
    }

    /**
     * This removes all our internal storage
     * TODO We might never ever need this
     */
    public static void removeAllInternalStorage() {
        System.out.println("StorageControl.removeAllInternalStorage() called");

        File internalStorage = Backend.applicationContext.getFilesDir();
        File[] files = internalStorage.listFiles();

        for (File file : files) {
            Backend.applicationContext.deleteFile(file.getName());
        }
    }

    /**
     * Writes an image to our internal storage.
     *
     * @param fileName The filename,
     *                 no directory,
     *                 include extension
     * @param bitmap   The bitmap
     * @throws StorageWriteException If we fail
     */
    public static void writeImage(@NotNull String fileName, @NotNull Bitmap bitmap) throws StorageWriteException {
        FileOutputStream fileOutputStream = null;

        try {

            File image = new File(getImageDirectory(), fileName);
            fileOutputStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

        } catch (IOException e) {
            throw new StorageWriteException("Could not write image with filename = " + fileName);
        } finally {
            if (fileOutputStream != null) try {
                fileOutputStream.close();
            } catch (IOException e) {
                /* Do nothing */
            }
        }
    }

    /**
     * Reads an image from memory.
     *
     * @param fileName The filename without any dir,
     *                 including extension
     * @return The bitmap, null if we fail
     * @throws StorageReadException If we fail
     */
    public static Bitmap readImage(@NotNull String fileName) throws StorageReadException {
        FileInputStream fileInputStream = null;

        try {
            File file = new File(getImageDirectory(), fileName);
            fileInputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            if (bitmap != null) {
                return bitmap;
            }

        } catch (IOException e) {
            /* Do nothing */
        } finally {
            if (fileInputStream != null) try {
                fileInputStream.close();
            } catch (IOException e) {
                /* Do nothing */
            }
        }

        throw new StorageReadException("Could not read image with filename = " + fileName);
    }

    /**
     * Gets our image directory, located in internal storage.
     *
     * @return The directory as a file
     */
    private static File getImageDirectory() {
        ContextWrapper contextWrapper = new ContextWrapper(Backend.applicationContext);
        return contextWrapper.getDir(DIRECTORY_IMAGES, Context.MODE_PRIVATE);
    }

}
