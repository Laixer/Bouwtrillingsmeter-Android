package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class reads and writes files to
 * and from the phones internal storage.
 */
public class StorageControl {

    /**
     * This is the location of our images.
     */
    private static final String DIRECTORY_IMAGES = "images";

    /**
     * This is the location of our lists.
     */
    private static final String DIRECTORY_UTILITY_LISTS = "lists";

    /**
     * This is the location of our measurements.
     * Data intervals are not stored in here.
     */
    private static final String DIRECTORY_MEASUREMENTS = "measurements";

    /**
     * This is the location of our data intervals.
     */
    private static final String DIRECTORY_DATA_INTERVALS = "data_intervals";


    /**
     * Initializes our folder structure.
     */
    static void initialize() {
        getDirectory(DIRECTORY_IMAGES);
        getDirectory(DIRECTORY_UTILITY_LISTS);
        getDirectory(DIRECTORY_MEASUREMENTS);
        getDirectory(DIRECTORY_DATA_INTERVALS);
    }

    /**
     * This removes all our internal storage.
     *
     * @throws StorageWriteException If we fail
     */
    public static void removeAllInternalStorage() throws StorageWriteException {
        try {

            ArrayList<File> folders = new ArrayList<File>();
            folders.add(getDirectory(DIRECTORY_DATA_INTERVALS));
            folders.add(getDirectory(DIRECTORY_MEASUREMENTS));
            folders.add(getDirectory(DIRECTORY_IMAGES));
            folders.add(getDirectory(DIRECTORY_UTILITY_LISTS));

            for (File folder : folders) {
                for (File file : folder.listFiles()) {
                    Backend.applicationContext.deleteFile(file.getName());
                }
            }

            return;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        throw new StorageWriteException("Could not remove all internal storage");
    }

    /**
     * This retrieves all saved measurements.
     *
     * @return All retrieved non-null measurements
     * @throws StorageReadException If we fail
     */
    static ArrayList<Measurement> retrieveAllSavedMeasurements() throws StorageReadException {
        File folderMeasurements = getDirectory(DIRECTORY_MEASUREMENTS);
        File folderDataIntervals = getDirectory(DIRECTORY_DATA_INTERVALS);

        // TODO Remove this debug bit

        System.out.println();

        for (File file : folderDataIntervals.listFiles()) {
            System.out.println(file.getName());
        }

        System.out.println();

        // Get all measurements
        ArrayList<Measurement> measurements = new ArrayList<>();
        for (File file : folderMeasurements.listFiles()) {
            System.out.println(file.getName());
            try {

                Measurement measurement = readAndConvertFile(file);

                // Extract data intervals
                File fileDataIntervals = new File(folderDataIntervals, measurement.getUID());
                if (fileDataIntervals.exists()) {
                    ArrayList<DataInterval> dataIntervals = StorageControl.readAndConvertArrayList(fileDataIntervals);
                    measurement.setDataIntervalsFromStorage(dataIntervals);
                }

                measurements.add(measurement);

            } catch (StorageReadException e) {
                System.out.println(e.toString());
            }
        }

        return measurements;
    }

    /**
     * Writes all measurement metadata to the storage.
     * This ignores the image.
     * This ignores the data intervals.
     *
     * @param measurement The measurement
     * @throws StorageWriteException If we fail
     */
    static void writeMeasurementMetaData(Measurement measurement) throws StorageWriteException {
        try {

            File file = new File(getDirectory(DIRECTORY_MEASUREMENTS), measurement.getUID());
            writeObject(measurement, file);
            return;

        } catch (StorageWriteException e) {
            System.out.println(e.getMessage());
        }

        throw new StorageWriteException("Could not write measurement metadata with UID = " + measurement);
    }

    /**
     * Writes our data intervals to the storage.
     *
     * @param measurement The measurement to which the
     *                    data intervals belong
     * @throws StorageWriteException If we fail
     */
    static void writeMeasurementDataIntervals(Measurement measurement) throws StorageWriteException {
        try {

            File file = new File(getDirectory(DIRECTORY_DATA_INTERVALS), measurement.getUID());
            writeObject(measurement.getDataIntervals(), file);
            return;

        } catch (StorageWriteException e) {
            System.out.println(e.getMessage());
        }

        throw new StorageWriteException("Could not write data intervals for measurement with UID = " + measurement.getUID());
    }

    /**
     * Writes an array list to our storage.
     *
     * @param arrayList           The array list
     * @param fileNameWithoutPath The name of the array list
     * @param <T>                 The type of the arraylist,
     *                            ArrayList<T>
     * @throws StorageWriteException If we fail
     */
    static <T> void writeArrayList(ArrayList<T> arrayList, String fileNameWithoutPath) throws StorageWriteException {
        String fileName = "";

        try {

            File file = new File(getDirectory(DIRECTORY_UTILITY_LISTS), fileNameWithoutPath);
            fileName = file.getName();
            writeObject(arrayList, file);
            return;

        } catch (StorageWriteException e) {
            System.out.println(e.getMessage());
        }

        throw new StorageWriteException("Could not write arraylist to " + fileName);
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

            File image = new File(getDirectory(DIRECTORY_IMAGES), fileName);
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

            File file = new File(getDirectory(DIRECTORY_IMAGES), fileName);
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

    /* All internal functions */

    /**
     * Attempts to convert an object.
     *
     * @param file The file
     * @param <T>  The type
     * @return The converted object
     * @throws StorageReadException If the type doesn't match
     */
    private static <T> T readAndConvertFile(File file) throws StorageReadException {
        try {

            Object object = readObject(file);
            return (T) object;

        } catch (StorageReadException | ClassCastException e) {
            System.out.println(e.getMessage());
        }

        throw new StorageReadException("Could not convert object");
    }

    /**
     * Attempts to convert an array list.
     *
     * @param file The file
     * @param <T>  The arraylist type,
     *             ArrayList<T>
     * @return The converted array list
     * @throws StorageReadException If the type doesn't match
     */
    private static <T> ArrayList<T> readAndConvertArrayList(File file) throws StorageReadException {
        try {

            Object object = readObject(file);
            return (ArrayList<T>) object;

        } catch (StorageReadException | ClassCastException e) {
            System.out.println(e.getMessage());
        }

        throw new StorageReadException("Could not convert arraylist");
    }

    /**
     * Reads an object from the internal storage.
     *
     * @param file The file
     * @return Null if no object is found
     */
    private static Object readObject(File file) throws StorageReadException {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;

        try {

            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);
            Object object = objectInputStream.readObject();
            fileInputStream.close();
            objectInputStream.close();
            return object;

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (fileInputStream != null) try {
                fileInputStream.close();
            } catch (IOException e) {
                /* Do nothing */
            }

            if (objectInputStream != null) try {
                objectInputStream.close();
            } catch (IOException e) {
                /* Do nothing */
            }
        }

        throw new StorageReadException("Could not read object from file ");
    }

    /**
     * This writes an object to the desired file location.
     * Implement the root directory in the file before
     * sending it as parameter to this function.
     * <p>
     * This function always overwrites.
     *
     * @param object The object
     */
    private static void writeObject(Object object, File file) throws StorageWriteException {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {

            fileOutputStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            fileOutputStream.close();
            return;

        } catch (IOException e) {
            /* Do nothing */
        } finally {
            if (fileOutputStream != null) try {
                fileOutputStream.close();
            } catch (IOException e) {
                /* Do nothing */
            }

            if (objectOutputStream != null) try {
                objectOutputStream.close();
            } catch (IOException e) {
                /* Do nothing */
            }
        }

        throw new StorageWriteException("Could not write object to file " + file.getName());
    }

    /**
     * Gets our image directory, located in internal storage.
     *
     * @param directoryName One of the static variables, being:
     *                      - {@link #DIRECTORY_IMAGES}
     *                      - {@link #DIRECTORY_UTILITY_LISTS}
     *                      - {@link #DIRECTORY_MEASUREMENTS}
     *                      - {@link #DIRECTORY_DATA_INTERVALS}
     * @return The directory as a file
     */
    private static File getDirectory(String directoryName) {
        if (!directoryName.equals(DIRECTORY_IMAGES)
                && !directoryName.equals(DIRECTORY_UTILITY_LISTS)
                && !directoryName.equals(DIRECTORY_MEASUREMENTS)
                && !directoryName.equals(DIRECTORY_DATA_INTERVALS)) {
            throw new IllegalArgumentException("Pick a constant for the directory name, not " + directoryName);
        }

        ContextWrapper contextWrapper = new ContextWrapper(Backend.applicationContext);
        return contextWrapper.getDir(directoryName, Context.MODE_PRIVATE);
    }

}
