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

    private Context context;
    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        this.context = context;
        fetchSharedPreferences();
    }

    /**
     * Loads everything from the phone
     */
    private void fetchSharedPreferences() {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_first_visit), Context.MODE_PRIVATE);
    }

    /**
     * This saves that we have opened the app at least once
     */
    public void writePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_first_visit), "INIT_OK");
        editor.commit();
    }

    /**
     * This checks if it is our first visit or not
     *
     * @return False if we have not opened our app at least once
     */
    public boolean checkPreference() {
        boolean result = true;

        if (sharedPreferences.getString(context.getString(R.string.pref_first_visit), "null").equals("null")) {
            result = false;
        }

        return result;
    }

    /**
     * This clears all the preferences
     */
    public void clearPreference() {
        sharedPreferences.edit().clear().commit();
    }

}
