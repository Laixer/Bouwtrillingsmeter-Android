package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.content.SharedPreferences;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class handles our preferences, which are saved on the phone.
 */
class PreferenceManager {

    private static final String defaultTrue = "TRUE";
    private static final String defaultFalse = "FALSE";
    private static final String defaultNull = "NULL";
    
    private static SharedPreferences sharedPreferences;

    /**
     * Loads everything from the phone
     */
    static void fetchSharedPreferences() {
        sharedPreferences = Utility.applicationContext.getSharedPreferences(Utility.resources.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    }

    /**
     * Saves a boolean preference
     *
     * @param resourceID The resource ID
     * @param value      True or false
     */
    static void writeBooleanPreference(int resourceID, boolean value) {
        String stringValue = value ? defaultTrue : defaultFalse;
        writeStringPreference(resourceID, stringValue);
    }

    /**
     * Checks a boolean preference
     *
     * @param resourceID The resource ID
     * @return The result as a boolean
     */
    static boolean readBooleanPreference(int resourceID) {
        String preference = readStringPreference(resourceID);
        return !(preference == null || preference.equals(defaultNull) || preference.equals(defaultFalse));
    }

    /**
     * Writes a string to the user preferences.
     *
     * @param resourceID The preference resource ID
     * @param value      The string to write
     */
    static void writeStringPreference(int resourceID, String value) {
        if (sharedPreferences == null || Utility.applicationContext == null) {
            System.out.println("Our preference manager was never initialized!");
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Utility.applicationContext.getString(resourceID), value);
        editor.apply();
    }

    /**
     * Checks a boolean preference
     *
     * @param resourceID The resource ID
     * @return The result as a boolean
     */
    static String readStringPreference(int resourceID) {
        if (sharedPreferences == null || Utility.applicationContext == null) {
            System.out.println("Our preference manager was never initialized.");
            return null;
        }

        return sharedPreferences.getString(Utility.applicationContext.getString(resourceID), defaultNull);
    }

    /**
     * This clears all the preferences
     */
    static void clearAllPreferences() {
        if (sharedPreferences == null || Utility.applicationContext == null) {
            System.out.println("Our preference manager was never initialized.");
            return;
        }

        sharedPreferences.edit().clear().apply();
    }

}
