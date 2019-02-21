package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings;

/**
 * This is used to generate settings based on questions
 */
public class SettingsControl {

    public static Settings CreateSettingsFromWidget(boolean[] answers) {
        Settings settings = new Settings();

        boolean betonMetselwerkGevelbekleding = answers[0];
        boolean monumentBeschermdStadsgezicht = answers[1];
        boolean afkomstigVanGegevenBronnen = answers[2];
        boolean funderingHoutOfStaal = answers[3];

        return settings;
    }

}
