package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import org.jetbrains.annotations.Nullable;

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

    private BuildingCategory buildingCategory;
    private VibrationCategory vibrationCategory;
    private Boolean vibrationSensitive;
    private double yv;
    private double yt;

    /**
     * Constructor
     */
    public Settings() {
        buildingCategory = BuildingCategory.NONE;
        vibrationCategory = VibrationCategory.NONE;
        vibrationSensitive = null;
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

    void setBuildingCategory(BuildingCategory buildingCategory) {
        if (this.buildingCategory == BuildingCategory.NONE) {
            this.buildingCategory = buildingCategory;
        } else {
            throw new IllegalArgumentException("Can't change building category once it has been set.");
        }
    }

    void setVibrationCategory(VibrationCategory vibrationCategory) {
        if (this.vibrationCategory == VibrationCategory.NONE) {
            this.vibrationCategory = vibrationCategory;
        } else {
            throw new IllegalArgumentException("Can't change vibration category once it has been set.");
        }
    }

    void setVibrationSensitive(Boolean vibrationSensitive) {
        if (this.vibrationSensitive == null) {
            this.vibrationSensitive = vibrationSensitive;
        } else {
            throw new IllegalArgumentException("Can't change vibration sensitivity once it has been set.");
        }
    }

    void setYv(double yv) {
        this.yv = yv;
    }

    void setYt(double yt) {
        this.yt = yt;
    }

    public BuildingCategory getBuildingCategory() {
        return buildingCategory;
    }

    public VibrationCategory getVibrationCategory() {
        return vibrationCategory;
    }

    public boolean isVibrationSensitive() {
        return vibrationSensitive;
    }

    public double getYv() {
        return yv;
    }

    public double getYt() {
        return yt;
    }
}