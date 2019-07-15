package gemeenterotterdam.trillingmeterapp.backend;

import android.content.Context;
import android.content.SharedPreferences;

import gemeenterotterdam.trillingmeterapp.R;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class handles our preferences, which are saved on the phone.
 */
public class PreferenceManager {

    private static final String DEFAULT_TRUE = "TRUE";
    private static final String DEFAULT_FALSE = "FALSE";
    private static final String DEFAULT_NULL = "NULL";

    private static SharedPreferences sharedPreferences;

    /**
     * Loads everything from the phone
     */
    static void fetchSharedPreferences() {
        sharedPreferences = Backend.applicationContext.getSharedPreferences(Backend.resources.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    }

    /**
     * Saves a boolean preference
     *
     * @param resourceID The resource ID
     * @param value      True or false
     */
    public static void writeBooleanPreference(int resourceID, boolean value) {
        String stringValue = value ? DEFAULT_TRUE : DEFAULT_FALSE;
        writeStringPreference(resourceID, stringValue);
    }

    /**
     * Checks a boolean preference
     *
     * @param resourceID The resource ID
     * @return The result as a boolean
     */
    public static boolean readBooleanPreference(int resourceID) {
        String preference = readStringPreference(resourceID);
        return !(preference == null || preference.equals(DEFAULT_FALSE));
    }

    /**
     * Writes a string to the user preferences.
     *
     * @param resourceID The preference resource ID
     * @param value      The string to write
     */
    public static void writeStringPreference(int resourceID, String value) {
        if (sharedPreferences == null || Backend.applicationContext == null) {
            System.out.println("Our preference manager was never initialized!");
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Backend.applicationContext.getString(resourceID), value);
        editor.apply();
    }

    /**
     * Checks a boolean preference.
     *
     * @param resourceID The resource ID
     * @return The result as a boolean,
     * null if not found
     */
    public static String readStringPreference(int resourceID) {
        if (sharedPreferences == null || Backend.applicationContext == null) {
            System.out.println("Our preference manager was never initialized.");
            return null;
        }

        String stored = sharedPreferences.getString(Backend.applicationContext.getString(resourceID), DEFAULT_NULL);
        return (stored.equals(DEFAULT_NULL)) ? null : stored;
    }

    /**
     * This clears all the preferences.
     * This saves the user UID.
     */
    public static void clearAllPreferences() {
        if (sharedPreferences == null || Backend.applicationContext == null) {
            System.out.println("Our preference manager was never initialized.");
            return;
        }

        String storedUserUID = readStringPreference(R.string.pref_user_uid);
        sharedPreferences.edit().clear().apply();
        writeStringPreference(R.string.pref_user_uid, storedUserUID);
    }

}
