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
 * These {@link DataInterval}s contains all {@link DataPoint}s.
 * All calculated values (done by our {@link Calculator}) are also stored in said {@link DataInterval}.
 * <p>
 * This class can be sent to the database for analysis.
 */
public class Measurement implements Serializable {

    public Settings settings;
    public ArrayList<DataInterval> dataIntervals;
    public Date dateStart;
    public Date dateEnd;

    private String uid;
    private String name;
    private String datetime;
    private String location;
    private String description;
    private Bitmap photo;

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
        photo = null;

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

        // Set starttime if this is our first data interval
        if (dataIntervals.size() == 0) {
            dateStart = Calendar.getInstance().getTime();
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
        }

        else {
            return null;
        }

    }

    /**
     * All getters and setters in one place.
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setDateTime(String datetime) {
        this.datetime = datetime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
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
     * Add a photo to the measurement
     *
     * @param photo The photo to add
     */
    public void updatePhoto(Bitmap photo) {
        this.photo = photo;
    }

    /**
     * Returns the photo
     * The "no picture present" handling is NOT done by this class
     *
     * @return A photo in Bitmap format, null if there is no photo
     */
    public Bitmap getPhoto() {
        if (photo == null) {
            System.out.println("Photo is null, fix this.");
            return null;
        }

        return photo;
    }

}
