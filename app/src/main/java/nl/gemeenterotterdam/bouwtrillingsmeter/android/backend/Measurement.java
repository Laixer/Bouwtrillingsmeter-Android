package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Measurement class
 *
 * This class holds all (meta)data for a measurement made by the user.
 * It has an arraylist of dataIntervals, which link to the actual sensor data + calculated values.
 * This class can be sent to the database for analysis.
 *
 * @author Thomas Beckers
 * @since 2019-02-28
 */
class Measurement implements Serializable {

    private String uid;
    private String name;
    private String datetime;
    private String location;
    private String description;
    private Bitmap photo;

    public ArrayList<DataInterval> dataIntervals;

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
        this.uid = UUID.randomUUID().toString();

        // Link all data
        // Also create default data
        this.name = name;
        this.datetime = "15-02-2019 20:23";
        this.location = "Grote Markt, Delft";
        this.description = "Een hele mooie omschrijving jawel!";
        this.photo = null;

        this.dataIntervals = new ArrayList<DataInterval>();
    }

    /**
     * Adds a data point to our datapoint arraylist
     * @param dataInterval A measured DataInterval object, containing datapoints and all calculations
     */
    public void addDataInterval(DataInterval dataInterval) {
        dataIntervals.add(dataInterval);
    }

    //All getters and setters, nice and compact #codingconventions
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
    public void UpdatePhoto(Bitmap photo) {
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
