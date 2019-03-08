package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BuildingCategory;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.VibrationCategory;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class generates a {@link nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings Settings} file.
 * This is done in the frontend for long term consistency purposes.
 */
public class SettingsGenerator {

    /**
     * Creates a settings file based on the {@link CategoryPageActivity} outcome.
     * This calls the {@link nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend} to attempt to overwrite the settings.
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
     * Creates a settings file based on the outcome our {@link WidgetControl}.
     * This calls the {@link nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend} to attempt to overwrite the settings.
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
