package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.content.SharedPreferences;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class handles our preferences, which are saved on the phone.
 * Currently this isn't properly implemented.
 * <p>
 * TODO Restructure this one
 */
public class PreferenceManager {

    private static Context context;
    private static SharedPreferences sharedPreferences;

    private static final String defaultTrue = "TRUE";
    private static final String defaultFalse = "FALSE";
    private static final String defaultNull = "NULL";

    /**
     * Loads everything from the phone
     */
    public static void fetchSharedPreferences(Context _context) {
        context = _context;
        sharedPreferences = context.getSharedPreferences(_context.getString(R.string.pref_has_visited_before), Context.MODE_PRIVATE);
    }

    /**
     * Saves a boolean preference
     *
     * @param resourceID The resource ID
     * @param value      True or false
     */
    public static void writeBooleanPreference(int resourceID, boolean value) {
        if (sharedPreferences == null || context == null) {
            throw new IllegalArgumentException("Our preference manager was never initialized!");
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(resourceID), value ? defaultTrue : defaultFalse);
        editor.apply();
    }

    /**
     * Checks a boolean preference
     *
     * @param resourceID The resource ID
     * @return The result as a boolean
     */
    public static boolean readBooleanPreference(int resourceID) {
        if (sharedPreferences == null || context == null) {
            throw new IllegalArgumentException("Our preference manager was never initialized!");
        }

        String preference = sharedPreferences.getString(context.getString(R.string.pref_has_visited_before), defaultNull);
        if (preference.equals(defaultNull) || preference.equals(defaultFalse)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * This clears all the preferences
     */
    public static void clearAllPreferences() {
        if (sharedPreferences == null || context == null) {
            throw new IllegalArgumentException("Our preference manager was never initialized!");
        }

        sharedPreferences.edit().clear().apply();
    }

}
