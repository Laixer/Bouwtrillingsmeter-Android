package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BuildingCategory;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.VibrationCategory;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class generates a {@link nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings Settings} file.
 * This is done in the frontend for long term consistency purposes.
 * This class then calls the {@link Backend#onGeneratedNewSettings(Settings)} function.
 */
public class SettingsGenerator {

    private static Settings previousSettings;
    private static Settings currentSettings;

    private static void setCurrentSettings(Settings settings) {
        previousSettings = currentSettings;
        currentSettings = settings;
        Backend.onGeneratedNewSettings(currentSettings);
    }

    /**
     * This gets our current settings object.
     *
     * @return Our current settings object.
     */
    public static Settings getCurrentSettings() {
        return currentSettings;
    }

    /**
     * Overwrites our current settings to a settings file based on the {@link SettingsPageActivity} outcome.
     * This calls the {@link nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend} to attempt to overwrite the settings.
     *
     * @param buildingCategory   The building category as enum
     * @param vibrationCategory  The vibration category as enum
     * @param vibrationSensitive A boolean indicating if we are dealing with a vibration sensitive building
     */
    public static void createSettingsFromCategoryPage(BuildingCategory buildingCategory, VibrationCategory vibrationCategory, boolean vibrationSensitive) {
        setCurrentSettings(new Settings(buildingCategory, vibrationCategory, vibrationSensitive));
    }

    /**
     * This calls the {@link nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend} to attempt to overwrite the settings.
     * TODO Implement this. Remove placeholder Settings parameters.
     *
     * @param answers A boolean array representing the answers to each widget page.
     */
    public static void overwriteSettingsFromWidget(boolean[] answers) {
        boolean betonMetselwerkGevelbekleding = answers[0];
        boolean monumentBeschermdStadsgezicht = answers[1];
        boolean afkomstigVanGegevenBronnen = answers[2];
        boolean funderingHoutOfStaal = answers[3];

        setCurrentSettings(new Settings(BuildingCategory.CATEGORY_1, VibrationCategory.CONTINUOUS, true));
    }

    /**
     * Used by our widget, if we discard our widget changes.
     */
    public static void restoreToPreviousSettings() {
        currentSettings = previousSettings;
    }

}
