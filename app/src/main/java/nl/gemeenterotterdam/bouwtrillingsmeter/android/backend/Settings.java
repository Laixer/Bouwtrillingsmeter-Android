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

    /**
     * Constructor
     */
    public Settings() {
        buildingCategory = BuildingCategory.NONE;
        vibrationCategory = VibrationCategory.NONE;
        vibrationSensitive = false;
    }

    /**
     * Constructor
     */
    public Settings(BuildingCategory buildingCategory, VibrationCategory vibrationCategory, boolean vibrationSensitive) {
        this.buildingCategory = buildingCategory;
        this.vibrationCategory = vibrationCategory;
        this.vibrationSensitive = vibrationSensitive;
    }

}