package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Connection type enum
 */
enum ConnectionType {
    NONE,
    WIFI,
    WIFI_AND_G,
    G
}

/**
 * This class handles all our connections to WIFI and 3G/4G/5G.
 * It communicates with the {@link SyncManager} class.
 * TODO Implement feedback that indicates if we have successfully pushed something.
 */
class SyncConnectionManager {

    /**
     * Initializes the instance
     */
    public static void initialize() {

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
        return true;
    }

    /**
     * Tells the server that our measurement was aborted.
     *
     * @param measurement The measurement
     * @return True if successful
     */
    static boolean pushMeasurementAborted(Measurement measurement) {
        return true;
    }

    /**
     * Push data interval essentials
     *
     * @param measurementUID            The measurement UID
     * @param allDataIntervalEssentials All the data interval essentials
     * @return True if successful
     */
    static boolean pushDataIntervalEssentialsList(String measurementUID, ArrayList<DataIntervalEssentials> allDataIntervalEssentials) {
        return true;
    }

    /**
     * Push all dataintervals that belong to a measurement
     *
     * @param dataIntervals The dataintervals to send
     * @return True if successful
     */
    static boolean pushDataIntervalsList(ArrayList<DataInterval> dataIntervals) {
        return true;
    }

    /**
     * This gets our type of connection, being one of {@link ConnectionType}.
     * TODO This uses depricated methods.
     *
     * @return Result
     */
    static ConnectionType getConnectionType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Backend.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            return ConnectionType.NONE;
        }

        boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        boolean g = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;

        if (wifi && g) {
            return ConnectionType.WIFI_AND_G;
        } else if (wifi && !g) {
            return ConnectionType.WIFI;
        } else if (!wifi && g) {
            return ConnectionType.G;
        } else {
            return ConnectionType.NONE;
        }
    }

}
