package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.graphics.Bitmap;

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
 * <p>
 * This class can be sent to the database for analysis.
 * TODO Implement bitmap load & save
 */
public class Measurement implements Serializable {

    public Settings settings;
    public ArrayList<DataInterval> dataIntervals;

    // TODO ISO 8601
    private Date dateStart;
    private Date dateEnd;

    private String uid;
    private String name;
    private String datetime;
    private String location;
    // TODO Location
    private double[] locationCoordinates;
    private double locationPrecision;
    private String description;
    private transient Bitmap bitmap;

    private boolean closed;

    /**
     * Simplified constructor for this class
     */
    public Measurement() {
        this("default name");
    }

    /**
     * Constructor for this class
     * TODO Remove this default data
     *
     * @param name The name for this measurement
     */
    public Measurement(String name) {
        // Generate a random uid for this measurement
        uid = UUID.randomUUID().toString();

        // Link all data
        // Also create default data
        this.name = name;
        datetime = "15-02-2019 20:23";
        location = "Grote Markt, Delft";
        description = "Een hele mooie omschrijving jawel!";
        bitmap = null;

        // Create public variables
        settings = new Settings();
        dataIntervals = new ArrayList<DataInterval>();

        closed = false;
    }

    /**
     * Adds a data point to our datapoint arraylist
     *
     * @param dataInterval A measured DataInterval object, containing datapoints and all calculations
     */
    public void addDataInterval(DataInterval dataInterval) {
        if (closed == true) {
            throw new IllegalStateException("Current measurement is already closed! No more data can be added.");
        }

        dataIntervals.add(dataInterval);
    }

    /**
     * Saves our current start time.
     */
    public void onStartMeasuring() {
        dateStart = Calendar.getInstance().getTime();
    }

    /**
     * Called when we stop the measuring.
     * This saves our end time.
     */
    public void onStopMeasuring() {
        dateEnd = Calendar.getInstance().getTime();
        closed = true;
    }

    /**
     * Checks if we have already closed our measurement.
     *
     * @return True if the measurement has already been closed.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * This will get us the start time in millis since Jan 1 1970.
     *
     * @return The start time in millis.
     */
    public long getStartTimeInMillis() {
        return this.dateStart.getTime();
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

    public void setLeName(String name) {
        this.name = name;
        onMetadataChanged();
    }

    public void setDateTime(String datetime) {
        this.datetime = datetime;
        onMetadataChanged();
    }

    public void setLocation(String location) {
        this.location = location;
        onMetadataChanged();
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

    public String getDateTime() {
        return this.datetime;
    }

    public String getLocation() {
        return this.location;
    }

    public String getDescription() {
        return this.description;
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
        StorageControl.writeObject(this, uid);
    }
}
