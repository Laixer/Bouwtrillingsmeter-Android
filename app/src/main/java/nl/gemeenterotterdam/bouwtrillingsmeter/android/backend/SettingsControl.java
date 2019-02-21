package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings;

/**
 * This is used to generate settings based on questions
 */
public class SettingsControl {

    /**
     * Creates a settings file based on the category settings page
     * @param buildingCategory The building category as enum
     * @param vibrationCategory The vibration category as enum
     * @param vibrationSensitive A boolean indicating if we are dealing with a vibration sensitive building
     * @return Settings file with these settings
     */
    public static Settings CreateSettingsFromCategoryPage(BuildingCategory buildingCategory, VibrationCategory vibrationCategory, boolean vibrationSensitive) {
        Settings settings = new Settings();

        settings.buildingCategory = buildingCategory;
        settings.vibrationCategory = vibrationCategory;
        settings.vibrationSensitive = vibrationSensitive;

        return settings;
    }

    /**
     * Creates a settings file based on the outcome of a widget
     * TODO Implement this
     * @param answers A boolean array representing the answers to each widget page
     * @return
     */
    public static Settings CreateSettingsFromWidget(boolean[] answers) {
        Settings settings = new Settings();

        boolean betonMetselwerkGevelbekleding = answers[0];
        boolean monumentBeschermdStadsgezicht = answers[1];
        boolean afkomstigVanGegevenBronnen = answers[2];
        boolean funderingHoutOfStaal = answers[3];

        return settings;
    }

}
