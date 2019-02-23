package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.graphics.Bitmap;
import android.icu.util.Measure;

import java.io.Serializable;

/**
 * Class used to hold a measurements
 * TODO Implement a popup to set a name if there is none?
 */
public class Measurement implements Serializable {

    private String name;
    private String datetime;
    private String location;
    private String description;
    private Bitmap photo;

    /**
     * Constructor for this class
     * TODO Remove this default data
     */
    public Measurement(String name) {
        this.name = name;
        this.datetime = "15-02-2019 20:23";
        this.location = "Grote Markt, Delft";
        this.description = "Een hele mooie omschrijving jawel!";
        this.photo = null;
    }

    //All getters and setters, nice and compact #codingconventions
    public void SetName(String name) { this.name = name; }
    public void SetDateTime(String datetime) { this.datetime = datetime; }
    public void SetLocation(String location) { this.location = location; }
    public void SetDescription(String description) { this.description = description; }

    public String GetName() { return this.name; }
    public String GetDateTime() { return this.datetime; }
    public String GetLocation() { return this.location; }
    public String GetDescription() { return this.description; }

    /**
     * Add a photo to the measurement
     * @param photo The photo to add
     */
    public void UpdatePhoto(Bitmap photo) {
        this.photo = photo;
    }

    /**
     * Returns the photo
     * The "no picture present" handling is NOT done by this class
     * @return A photo in Bitmap format, null if there is no photo
     */
    public Bitmap GetPhoto() {
        if (photo == null) {
            System.out.println("Photo is null, fix this.");
            return null;
        }

        return photo;
    }

}
