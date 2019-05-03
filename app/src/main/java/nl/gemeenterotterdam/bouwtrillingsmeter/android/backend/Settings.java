package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.io.Serializable;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class holds the settings for our current measurement.
 * These settings are determined in the settings widget, containing:
 * {@link BuildingCategory}, {@link VibrationCategory} and a boolean to indicate if we are {@link #vibrationSensitive}.
 */
public class Settings implements Serializable {

    public static Settings settings;

    public BuildingCategory buildingCategory;
    public VibrationCategory vibrationCategory;
    public boolean vibrationSensitive;

    // TODO yv yt
    public double yv;
    public double yt;

    /**
     * Constructor
     */
    public Settings() {
        buildingCategory = BuildingCategory.NONE;
        vibrationCategory = VibrationCategory.NONE;
        vibrationSensitive = false;
        yv = -1;
        yt = -1;
    }

    /**
     * Constructor
     */
    public Settings(BuildingCategory buildingCategory, VibrationCategory vibrationCategory, boolean vibrationSensitive) {
        this.buildingCategory = buildingCategory;
        this.vibrationCategory = vibrationCategory;
        this.vibrationSensitive = vibrationSensitive;
    }

    /**
     * Return true if we can work with these settings.
     * @return True if valid
     */
    public boolean isValid() {
        return !(vibrationCategory == VibrationCategory.NONE || buildingCategory == BuildingCategory.NONE);
    }


}