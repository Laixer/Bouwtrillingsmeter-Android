package nl.gemeenterotterdam.bouwtrillingsmeter.android;

import android.graphics.Bitmap;
import android.icu.util.Measure;

/**
 * Class used to hold a measurements
 */
public class Measurement {

    private String name;
    private Bitmap photo;

    /**
     * Constructor for this class
     */
    public Measurement() {
        this.name = "My measurement";
        this.photo = null;
    }

    /**
     * Overloaded constructor for this class
     * TODO Do we need this? This was created for debug purposes
     */
    public Measurement(String name, Bitmap photo) {
        this.name = name;
        this.photo = photo;
    }

    /**
     * Overwrite the current name
     * @param name The new name
     */
    public void SetName(String name) {
        this.name = name;
    }

    /**
     * Returns the name
     * TODO Implement a popup to set a name if there is none?
     * @return
     */
    public String GetName() {
        return name;
    }

    /**
     * Add a photo to the measurement
     * @param photo The photo to add
     */
    public void AddPhoto(Bitmap photo) {
        this.photo = photo;
    }

    /**
     * Returns the photo
     * TODO Implement default picture or a way to show an empty picture
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
