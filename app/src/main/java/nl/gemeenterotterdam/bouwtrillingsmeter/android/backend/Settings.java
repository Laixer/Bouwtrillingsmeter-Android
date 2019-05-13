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

    private final BuildingCategory buildingCategory;
    private final VibrationCategory vibrationCategory;
    private final Boolean vibrationSensitive;
    private final Float yv;
    private final Float yt;

    /**
     * Constructor
     */
    public Settings(BuildingCategory buildingCategory, VibrationCategory vibrationCategory, boolean vibrationSensitive) {
        this.buildingCategory = buildingCategory;
        this.vibrationCategory = vibrationCategory;
        this.vibrationSensitive = vibrationSensitive;
        this.yv = LimitConstants.getYvFromSettings(this);
        this.yt = LimitConstants.getYtFromSettings(this);
    }

    /**
     * Return true if we can work with these settings.
     * @return True if valid
     */
    public boolean isValid() {
        return !(vibrationCategory == VibrationCategory.NONE || buildingCategory == BuildingCategory.NONE || vibrationSensitive == null || yv == null || yt == null);
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

    public float getYv() {
        return yv;
    }

    public float getYt() {
        return yt;
    }
}