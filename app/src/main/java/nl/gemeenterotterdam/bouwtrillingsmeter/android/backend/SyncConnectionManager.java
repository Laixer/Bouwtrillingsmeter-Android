package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class handles all our connections to WIFI and 3G/4G/5G.
 * It communicates with the {@link SyncManager} class.
 * TODO Implement feedback
 * TODO Make one thread
 */
class SyncConnectionManager {

    /**
     * Contains the base URL of our API.
     */
    private static final String URL_API = "http://192.168.0.40:3000/api/";

    private static final String URL_RELATIVE_USER_UID = "user-uid/register";
    private static final String URL_RELATIVE_MEASUREMENT = "measurement/push";
    private static final String URL_RELATIVE_DATA_INTERVAL_ESSENTIALS = "data-interval-essentials/push";
    private static final String URL_RELATIVE_DATA_INTERVALS = "data-intervals/push";
    private static final String URL_RELATIVE_IMAGE = "image/push";

    /**
     * Initializes the instance.
     */
    static void initialize() {

    }

    /**
     * This posts to the server.
     * TODO Uses the mock api now
     *
     * @param urlRelative Relative url
     * @param jsonObjectString The JSON data as a string
     */
    private static void post(String urlRelative, String jsonObjectString) {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(URL_API + urlRelative);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                byte[] input = jsonObjectString.getBytes();
                outputStream.write(input, 0, input.length);

                // Read response
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = bufferedReader.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                boolean result = (response.toString().equals("true"));
                System.out.println("The request towards " + urlRelative + " success = " + result);

            } catch (MalformedURLException e) {
                System.out.println(e.toString());
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        });
        thread.start();
    }

    /**
     * Push our registered user uid to the API.
     * This should only be done once.
     *
     * @param userUID The user UID
     * @return True if successful
     */
    static boolean pushUserUID(String userUID) {
        JSONObject object = JSONCompiler.compileUserUID(userUID);
        post(URL_RELATIVE_USER_UID, object.toString());
        return true;
    }

    /**
     * Push a measurements metadata.
     * This overwrites anything existing on the server, so we can
     * use this as an update function.
     *
     * @param measurement The measurement
     * @return True if successful
     */
    static boolean pushMeasurementMetadata(Measurement measurement) {
        JSONObject object = JSONCompiler.compileMeasurement(measurement);
        post(URL_RELATIVE_MEASUREMENT, object.toString());
        return true;
    }

    /**
     * Push data interval essentials
     *
     * @param measurementUID             The measurement UID
     * @param dataIntervalEssentialsList All the data interval essentials
     * @return True if successful
     */
    static boolean pushDataIntervalEssentialsList(String measurementUID, ArrayList<DataIntervalEssentials> dataIntervalEssentialsList) {
        JSONArray array = JSONCompiler.compileDataIntervalEssentialsList(dataIntervalEssentialsList);
        post(URL_RELATIVE_DATA_INTERVAL_ESSENTIALS, array.toString());
        return true;
    }

    /**
     * Push all dataintervals that belong to a measurement
     *
     * @param dataIntervalList The dataintervals to send
     * @return True if successful
     */
    static boolean pushDataIntervalsList(ArrayList<DataInterval> dataIntervalList) {
        JSONArray object = JSONCompiler.compileDataIntervalList(dataIntervalList);
        post(URL_RELATIVE_DATA_INTERVALS, object.toString());
        return true;
    }

    /**
     * Push an image to the API.
     *
     * @param measurementUID The measurement UID that belongs to the image
     * @param image          The image
     * @return True if successful
     */
    static boolean pushImage(String measurementUID, Image image) {
        JSONObject object = JSONCompiler.compileImage(measurementUID, image);
        post(URL_RELATIVE_DATA_INTERVAL_ESSENTIALS, object.toString());
        return true;
    }

    /**
     * This gets our type of connection, being one of {@link SyncConnectionType}.
     * TODO This uses depricated methods.
     *
     * @return Result
     */
    static SyncConnectionType getConnectionType() {
        // Get access to our connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager) Backend.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Wifi
        NetworkInfo networkInfoWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean wifi;
        if (networkInfoWifi == null) {
            wifi = false;
        } else {
            wifi = networkInfoWifi.isConnected();
        }

        // G
        NetworkInfo networkInfoG = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean g;
        if (networkInfoG == null) {
            g = false;
        } else {
            g = networkInfoG.isConnected();
        }

        // Return
        if (wifi && g) {
            return SyncConnectionType.WIFI_AND_G;
        } else if (wifi && !g) {
            return SyncConnectionType.WIFI;
        } else if (!wifi && g) {
            return SyncConnectionType.G;
        } else {
            return SyncConnectionType.NONE;
        }
    }

}
