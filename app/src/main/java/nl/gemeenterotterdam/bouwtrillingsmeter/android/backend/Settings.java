package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class holds the settings for our current measurement.
 * These settings are determined in the settings widget, containing:
 * {@link BuildingCategory}, {@link VibrationCategory} and a boolean to indicate if we are {@link #vibrationSensitive}.
 * When checking if we exceed the norms we use these settings in our {@link LimitValueTable} lookup table.
 */
public class Settings {

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
     * Creates a settings file based on the category settings page
     * Accessible via Settings.settings
     *
     * @param buildingCategory   The building category as enum
     * @param vibrationCategory  The vibration category as enum
     * @param vibrationSensitive A boolean indicating if we are dealing with a vibration sensitive building
     */
    public void overwriteSettingsFromCategoryPage(BuildingCategory buildingCategory, VibrationCategory vibrationCategory, boolean vibrationSensitive) {
        buildingCategory = buildingCategory;
        vibrationCategory = vibrationCategory;
        vibrationSensitive = vibrationSensitive;
    }

    /**
     * Creates a settings file based on the outcome of a widget
     * Accessible via Settings.settings
     * TODO Implement this
     *
     * @param answers A boolean array representing the answers to each widget page
     */
    public void overwriteSettingsFromWidget(boolean[] answers) {
        boolean betonMetselwerkGevelbekleding = answers[0];
        boolean monumentBeschermdStadsgezicht = answers[1];
        boolean afkomstigVanGegevenBronnen = answers[2];
        boolean funderingHoutOfStaal = answers[3];
    }


}