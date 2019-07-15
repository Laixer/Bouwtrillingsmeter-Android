package gemeenterotterdam.trillingmeterapp.backend;

import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import kotlin.text.Charsets;

/**
 * This class handles all our connections to WIFI and 3G/4G/5G.
 * It communicates with the {@link SyncManager} class.
 * TODO Implement feedback
 * TODO Make one thread
 * TODO This needs to recheck connection every once in a while
 * TODO Dit maakt geen onderscheid tussen waar het proces fout gaat op het moment.
 * Intern gebeurt dit wel, maar extern krijg je alleen een false terug.
 */
class SyncConnectionManager {

    private static final boolean USING_MOCK = true;
    private static final String URL_API_MOCK = "http://192.168.0.40:3000/api/";
    private static final String URL_API = "";

    private static final String ENDPOINT_USER_UID = "user-uid/register";
    private static final String ENDPOINT_MEASUREMENT = "measurement/push";
    private static final String ENDPOINT_DATA_INTERVAL_ESSENTIALS = "data-interval-essentials/push";
    private static final String ENDPOINT_DATA_INTERVALS = "data-intervals/push";
    private static final String ENDPOINT_IMAGE = "image/push";

    private static final String RESPONSE_SUCCESS = "true";

    /**
     * Push our registered user uid to the API.
     * This should only be done once.
     *
     * @param userUID The user UID
     * @return True if successful
     */
    boolean pushUserUID(String userUID) {
        JSONObject object = JSONCompiler.compileUserUID(userUID);
        if (object != null) {
            return post(ENDPOINT_USER_UID, object);
        } else {
            return false;
        }
    }

    /**
     * Push a measurements metadata.
     * This overwrites anything existing on the server, so we can
     * use this as an update function.
     *
     * @param measurement The measurement
     * @return True if successful
     */
    boolean pushMeasurementMetadata(Measurement measurement) {
        JSONObject object = JSONCompiler.compileMeasurement(measurement);
        if (object != null) {
            return post(ENDPOINT_MEASUREMENT, object);
        } else {
            return false;
        }
    }

    /**
     * Push data interval essentials
     *
     * @param measurementUID             The measurement UID
     * @param dataIntervalEssentialsList All the data interval essentials
     * @return True if successful
     */
    boolean pushDataIntervalEssentialsList(String measurementUID, ArrayList<DataIntervalEssentials> dataIntervalEssentialsList) {
        JSONObject object = JSONCompiler.compileDataIntervalEssentialsList(dataIntervalEssentialsList);
        if (object != null) {
            return post(ENDPOINT_DATA_INTERVAL_ESSENTIALS, object);
        } else {
            return false;
        }
    }

    /**
     * Push all dataintervals that belong to a measurement
     *
     * @param dataIntervalList The dataintervals to send
     * @return True if successful
     */
    boolean pushDataIntervalsList(ArrayList<DataInterval> dataIntervalList) {
        JSONObject object = JSONCompiler.compileDataIntervalList(dataIntervalList);
        if (object != null) {
            return post(ENDPOINT_DATA_INTERVALS, object);
        } else {
            return false;
        }
    }

    /**
     * Push an image to the API.
     *
     * @param measurementUID The measurement UID that belongs to the image
     * @param image          The image
     * @return True if successful
     */
    boolean pushImage(String measurementUID, Image image) {
        JSONObject object = JSONCompiler.compileImage(measurementUID, image);
        if (object != null) {
            return post(ENDPOINT_IMAGE, object);
        } else {
            return false;
        }
    }

    /**
     * This gets our type of connection, being one of {@link SyncConnectionType}.
     * TODO This uses depricated methods.
     *
     * @return Result
     */
    SyncConnectionType getConnectionType() {
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

    /**
     * This posts to the server.
     * TODO Uses the mock api now
     *
     * @param endpoint   Relative url
     * @param jsonObject The JSON object to send
     */
    private boolean post(String endpoint, JSONObject jsonObject) {
        HttpURLConnection connection = getConnection(endpoint);
        if (connection == null) {
            return false;
        }

        String response = readResponse(connection);
        if (response == null) {
            return false;
        } else {
            return response.equals(RESPONSE_SUCCESS);
        }
    }

    /**
     * Creates a connection.
     *
     * @param endpoint The endpoint as string
     * @return The connection, null if failed
     */
    private HttpURLConnection getConnection(String endpoint) {
        try {
            URL url = new URL((USING_MOCK ? URL_API_MOCK : URL_API) + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            return connection;
        } catch (MalformedURLException e) {
            System.out.println(String.format("URL is malformed. Aborting POST. Error message: %s", e.getMessage()));
            return null;
        } catch (IOException e) {
            System.out.println(String.format("Could not connect. Error message: %s", e.getMessage()));
            return null;
        }
    }

    /**
     * Attempts to read a response.
     *
     * @param connection The connection
     * @return The response as a string, null if failed
     */
    private String readResponse(HttpURLConnection connection) {
        if (connection == null) {
            return null;
        }

        try {
            // Read response
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), Charsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // Build response
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = bufferedReader.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // Return response
            System.out.println("Response read: " + response.toString());
            return response.toString();
        } catch (IOException e) {
            System.out.println("Could not read connection response.");
            return null;
        }
    }

}
