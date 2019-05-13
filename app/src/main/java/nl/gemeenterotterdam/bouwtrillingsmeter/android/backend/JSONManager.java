package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;

/**
 * This class compiles JSON object for us.
 * TODO Error handling
 * TODO String door user -> is dit veilig?
 */
public class JSONManager {

    /**
     * Converts our user id to a JSON object.
     *
     * @return The JSON object
     */
    public static JSONObject compileUserUID() {
        JSONObject object = new JSONObject();

        try {
            object.put("userUID", Backend.getUserUID());
        } catch (JSONException e) {
            //
        }

        return object;
    }

    /**
     * Converts our measurement to a JSON object.
     *
     * @param measurement The measurement to convert.
     * @return The JSON object
     */
    public static JSONObject compileMeasurement(Measurement measurement) {
        JSONObject object = new JSONObject();

        try {
            object.put("userUID", Backend.getUserUID());
            object.put("UID", measurement.getUID());
            object.put("dateStart", measurement.getDateStart());
            object.put("dateEnd", measurement.getDateEnd());
            object.put("longitude", Double.valueOf(measurement.getLocationLongitude()));
            object.put("latitude", Double.valueOf(measurement.getLocationLatitude()));
            object.put("locationAccuracy", Double.valueOf(measurement.getLocationAccuracy()));
            object.put("description", measurement.getDescription());
            object.put("settings", compileSettings(measurement.getSettings()));
        } catch (JSONException e) {
            System.out.println("Error while compiling measurement");
        }

        return object;
    }

    /**
     * Converts our measurement to a JSON object.
     *
     * @param dataIntervalEssentials The measurement to convert.
     * @return The JSON object
     */
    public static JSONObject compileDataIntervalEssentials(DataIntervalEssentials dataIntervalEssentials) {
        JSONObject object = new JSONObject();

        try {
            object.put("userUID", Backend.getUserUID());
            object.put("", );
            object.put("", );
            object.put("", );
            object.put("", );
            object.put("", );
            object.put("", );
        } catch (JSONException e) {
            System.out.println("Error while compiling dataIntervalEssentials");
        }

        return object;
    }

    /**
     * Converts our measurement to a JSON object.
     *
     * @param dataInterval The measurement to convert.
     * @return The JSON object
     */
    public static JSONObject compileDataInterval(DataInterval dataInterval) {
        JSONObject object = new JSONObject();

        try {
            object.put("userUID", Backend.getUserUID());
        } catch (JSONException e) {
            //
        }

        return object;
    }

    /**
     * Converts our measurement to a JSON object.
     *
     * @param settings The measurement to convert.
     * @return The JSON object
     */
    private static JSONObject compileSettings(Settings settings) {
        JSONObject object = new JSONObject();

        try {
            object.put("buildingCategory", settings.getBuildingCategory().toString());
            object.put("vibrationCategory", settings.getVibrationCategory().toString());
            object.put("vibrationSensitive", Boolean.valueOf(settings.isVibrationSensitive()));
            object.put("yt", Float.valueOf(settings.getYt()));
            object.put("yv", Float.valueOf(settings.getYv()));
        } catch (JSONException e) {
            System.out.println("Error while compiling settings");
        }

        return object;
    }

    /**
     * Converts our measurement to a JSON object.
     *
     * @param dominantFrequencies The measurement to convert.
     * @return The JSON object
     */
    private static JSONObject compileDominantFrequencies(DominantFrequencies dominantFrequencies) {
        JSONObject object = new JSONObject();

        try {
            object.put("frequencies", compileToJSONArray(ArrayUtils.toObject(dominantFrequencies.getFrequencies())));
            object.put("velocities", compileToJSONArray(ArrayUtils.toObject(dominantFrequencies.getVelocities())));
            object.put("exceedsLimit", compileToJSONArray(ArrayUtils.toObject(dominantFrequencies.getExceedsLimit())));
        } catch (JSONException e) {
            System.out.println("Error while compiling dominantFrequencies");
        }

        return object;
    }

    /**
     * Creates an array compatible with JSON.
     *
     * @param values The values
     * @param <T>    The type of the values
     * @return Array JSON object
     */
    private static <T> JSONArray compileToJSONArray(T[] values) {
        JSONArray array = new JSONArray();

        for (T value : values) {
            array.put(value);
        }

        return array;
    }

}
