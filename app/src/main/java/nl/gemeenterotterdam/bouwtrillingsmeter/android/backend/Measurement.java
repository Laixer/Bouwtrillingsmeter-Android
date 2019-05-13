package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.graphics.Bitmap;
import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class holds all (meta)data for a measurement made by the user.
 * It has an arraylist of {@link DataInterval}s.
 * These {@link DataInterval}s contains all {@link DataPoint3D}s.
 * All calculated values (done by our {@link Calculator}) are also stored in said {@link DataInterval}.
 * TODO ISO 8601 implementeren
 */
public class Measurement implements Serializable {

    // Dit gaat mee naar de server
    private final String uid;
    private String name;
    private String dateStart;
    private String dateEnd;
    private double longitude;
    private double latitude;
    private double locationAccuracy;
    private String description;
    private Settings settings;
    private int dataIntervalCount;

    private ArrayList<DataInterval> dataIntervals;
    private transient Bitmap bitmap;

    // Dit gaat niet mee naar de server
    private Date dateStartObject;
    private boolean open;
    private boolean closed;

    /**
     * Simplified constructor for this class
     */
    Measurement() {
        this("default name");
    }

    /**
     * Constructor for this class
     * TODO Remove this default data
     *
     * @param name The name for this measurement
     */
    Measurement(String name) {
        // Generate a random uid for this measurement
        uid = UUID.randomUUID().toString();

        // Link all data
        // TODO Remove default debug data
        this.name = name;
        description = "";
        bitmap = null;
        longitude = Double.MAX_VALUE;
        latitude = Double.MAX_VALUE;

        // Create variables
        settings = null;
        dataIntervals = new ArrayList<DataInterval>();

        open = false;
        closed = false;
    }

    /**
     * Adds a data point to our datapoint arraylist
     *
     * @param dataInterval A measured DataInterval object, containing datapoints and all calculations
     */
    void addDataInterval(DataInterval dataInterval) {
        if (closed) {
            throw new IllegalStateException("Current measurement is already closed! No more data can be added.");
        }

        dataIntervals.add(dataInterval);
        dataIntervalCount = dataIntervals.size();
    }

    /**
     * Saves our current start time.
     */
    void onStartMeasuring() {
        dateStartObject = Calendar.getInstance().getTime();
        dateStart = dateStartObject.toString();
        open = true;
    }

    /**
     * Called when we stop the measuring.
     * This saves our end time.
     */
    void onStopMeasuring() {
        dateEnd = Calendar.getInstance().getTime().toString();
        closed = true;
    }

    /**
     * Checks if we have already started our measurement.
     *
     * @return True if the measurement has been started.
     */
    boolean isOpen() {
        return open;
    }

    /**
     * Checks if we have already closed our measurement.
     *
     * @return True if the measurement has already been closed.
     */
    boolean isClosed() {
        return closed;
    }

    /**
     * This will get us the start time in millis since Jan 1 1970.
     *
     * @return The start time in millis.
     */
    long getStartTimeInMillis() {
        return this.dateStartObject.getTime();
    }

    /**
     * Checks all our dataintervals to see if we have exceeded any limits.
     * Also returns said dataintervals.
     *
     * @return An arraylist of all exceeding dataintervals. Null if we did not exceed any limits.
     */
    public ArrayList<DataInterval> getAllExceedingDataIntervals() {
        ArrayList<DataInterval> result = new ArrayList<DataInterval>();
        for (DataInterval dataInterval : dataIntervals) {
            if (dataInterval.isExceedingLimit()) {
                result.add(dataInterval);
            }
        }

        if (result.size() > 0) {
            return result;
        } else {
            return null;
        }

    }

    public void overwriteSettings(Settings settings) {
        if (!open && !closed) {
            this.settings = settings;
        } else {
            throw new IllegalArgumentException("Cannot overwrite settings on a measurement that is already started or closed.");
        }
    }

    public void setName(String name) {
        this.name = name;
        onMetadataChanged();
    }

    void setLocation(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        locationAccuracy = location.getAccuracy();
    }

    public void setDescription(String description) {
        this.description = description;
        onMetadataChanged();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        onMetadataChanged();
    }

    public String getUID() {
        return this.uid;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public double getLocationLongitude() {
        return this.longitude;
    }

    public double getLocationLatitude() {
        return this.latitude;
    }

    public double getLocationAccuracy() {
        return this.locationAccuracy;
    }

    public Settings getSettings() {
        return settings;
    }

    public ArrayList<DataInterval> getDataIntervals() {
        return dataIntervals;
    }

    public int getDataIntervalCount() {
        return dataIntervalCount;
    }

    public String getDateStart() {
        return dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    /**
     * Returns the bitmap
     * The "no picture present" handling is NOT done by this class
     *
     * @return A bitmap in Bitmap format, null if there is no bitmap
     */
    public Bitmap getBitmap() {
        if (bitmap == null) {
            System.out.println("Photo is null, fix this.");
            return null;
        }

        return bitmap;
    }

    /**
     * This writes the new metadata to disk
     * TODO Potential problem: this can take very long with big data sets
     */
    private void onMetadataChanged() {
        StorageControl.writeMeasurement(this);
    }
}
