package gemeenterotterdam.trillingmeterapp.backend;

import android.media.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

/**
 * This class compiles JSON object for us.
 * TODO Error handling
 * TODO String door user -> is dit veilig?
 */
class JSONCompiler {

    /**
     * Converts our user uid to a JSON object.
     *
     * @param userUID The user UID
     * @return The JSON object
     */
    static JSONObject compileUserUID(String userUID) {
        JSONObject object = new JSONObject();

        try {
            object.put("userUID", userUID);
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
    static JSONObject compileMeasurement(Measurement measurement) {
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
     * Converts a list of {@link DataIntervalEssentials} to a {@link JSONArray}.
     *
     * @return The JSON array, null if failed
     */
    static JSONObject compileDataIntervalEssentialsList(ArrayList<DataIntervalEssentials> list) {
        JSONArray array = new JSONArray();

        for (DataIntervalEssentials item : list) {
            array.put(compileDataIntervalEssentials(item));
        }

        return arrayToObject(array);
    }

    /**
     * Converts a list of {@link DataInterval} to a {@link JSONArray}.
     *
     * @return The JSON array, null if failed
     */
    static JSONObject compileDataIntervalList(ArrayList<DataInterval> list) {
        JSONArray array = new JSONArray();

        for (DataInterval item : list) {
            array.put(compileDataInterval(item));
        }

        return arrayToObject(array);
    }

    /**
     * Converts our {@link Image} to a JSON object.
     *
     * @param measurementUID The measurement UID corresponding to the image
     * @param image          The {@link Image} to convert
     * @return The JSON object
     */
    static JSONObject compileImage(String measurementUID, Image image) {
        JSONObject object = new JSONObject();

        try {
            object.put("measurementUID", measurementUID);
            object.put("image", "to be determined");
        } catch (JSONException e) {
            System.out.println("Error while compiling image");
        }

        return object;
    }

    /**
     * Converts our {@link DataIntervalEssentials} to a JSON object.
     *
     * @param dataIntervalEssentials The {@link DataIntervalEssentials} to convert.
     * @return The JSON object
     */
    private static JSONObject compileDataIntervalEssentials(DataIntervalEssentials
                                                                    dataIntervalEssentials) {
        JSONObject object = new JSONObject();

        try {
            object.put("measurementUID", dataIntervalEssentials.getMeasurementUID());
            object.put("index", dataIntervalEssentials.getIndex());
            object.put("velocitiesAbsoluteMax", compileToJSONArray(ArrayUtils.toObject(dataIntervalEssentials.getVelocitiesAbsMax().values)));
            object.put("dominantFrequencies", compileDominantFrequencies(dataIntervalEssentials.getDominantFrequencies()));
        } catch (JSONException e) {
            System.out.println("Error while compiling dataIntervalEssentials");
        }

        return object;
    }

    /**
     * Converts our {@link DataInterval} to a JSON object.
     *
     * @param dataInterval The {@link DataInterval} to convert.
     * @return The JSON object
     */
    private static JSONObject compileDataInterval(DataInterval dataInterval) {
        JSONObject object = new JSONObject();

        try {
            object.put("measurementUID", dataInterval.getMeasurementUID());
            object.put("index", Integer.valueOf(dataInterval.getIndex()));
            object.put("millisRelativeStart", Long.valueOf(dataInterval.getMillisRelativeStart()));
            object.put("millisRelativeEnd", Long.valueOf(dataInterval.getMillisRelativeEnd()));
            object.put("dataPoints3DAcceleration", compileDataPoints3D(dataInterval.getDataPoints3DAcceleration()));
            object.put("velocities", compileDataPoints3D(dataInterval.getVelocities()));
            object.put("frequencyAmplitudes", compileDataPoints3D(dataInterval.getFrequencyAmplitudes()));

            object.put("velocitiesAbsoluteMax", compileToJSONArray(ArrayUtils.toObject(dataInterval.getVelocitiesAbsoluteMax().values)));
            object.put("dominantFrequencies", compileDominantFrequencies(dataInterval.getDominantFrequencies()));
        } catch (JSONException e) {
            System.out.println("Error while compiling dataInterval");
        }

        return object;
    }

    /**
     * Converts our {@link Settings} to a JSON object.
     *
     * @param settings The {@link Settings} to convert.
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
     * Converts our {@link DominantFrequencies} to a JSON object.
     *
     * @param dominantFrequencies The {@link DominantFrequencies} to convert.
     * @return The JSON object
     */
    private static JSONObject compileDominantFrequencies(DominantFrequencies
                                                                 dominantFrequencies) {
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
     * Converts our arraylist of {@link DataPoint3D}'s to a {@link JSONObject} containing 4 {@link JSONArray}s.
     *
     * @param dataPoints3D The data points
     * @return The JSON object
     */
    private static <T> JSONObject
    compileDataPoints3D(ArrayList<DataPoint3D<T>> dataPoints3D) {
        JSONObject object = new JSONObject();

        try {
            JSONArray t = new JSONArray();
            JSONArray x = new JSONArray();
            JSONArray y = new JSONArray();
            JSONArray z = new JSONArray();

            for (DataPoint3D<T> dataPoint3D : dataPoints3D) {
                t.put(dataPoint3D.xAxisValue);
                x.put(dataPoint3D.values[0]);
                y.put(dataPoint3D.values[1]);
                z.put(dataPoint3D.values[2]);
            }

            object.put("t", t);
            object.put("x", x);
            object.put("y", y);
            object.put("z", z);
        } catch (JSONException e) {
            System.out.println("Error while compiling dominantFrequencies");
        }

        return object;
    }

    /**
     * Creates a {@link JSONArray} from an array.
     *
     * @param values The values
     * @param <T>    The type of the values
     * @return JSON array object
     */
    private static <T> JSONArray compileToJSONArray(T[] values) {
        JSONArray array = new JSONArray();

        for (T value : values) {
            array.put(value);
        }

        return array;
    }

    /**
     * Converts a JSON array to a JSON Object.
     * The array holds the key "data".
     *
     * @param array The array
     * @return The object, null if failed
     */
    private static JSONObject arrayToObject(JSONArray array) {
        try {
            JSONObject object = new JSONObject();
            object.put("data", array);
            return object;
        } catch (JSONException e) {
            System.out.println("Error while compiling array towards object. " + array.toString());
            return null;
        }
    }

}
