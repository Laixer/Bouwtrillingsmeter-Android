package gemeenterotterdam.trillingmeterapp.backend;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * This class is used for location utility purposes.
 */
public class LocationUtility {

    private static LocationManager locationManager;
    private static Geocoder geocoder;

    /**
     * @return internal getter for the location manager.
     */
    private static LocationManager getLocationManager() {
        if (locationManager == null) {
            locationManager = (LocationManager) Backend.applicationContext.getSystemService(Context.LOCATION_SERVICE);
        }

        return locationManager;
    }

    /**
     * @return internal getter for the geocoder.
     */
    private static Geocoder getGeocoder() {
        if (geocoder == null) {
            geocoder = new Geocoder(Backend.applicationContext, Locale.getDefault());

        }

        return geocoder;
    }

    /**
     * Check if we have the proper location permissions.
     *
     * @param activity The activity from which to check.
     * @return True if we have
     */
    public static boolean hasPermissionToFetchLocation(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if GPS is enabled.
     *
     * @return True if enabled
     */
    public static boolean isLocationEnabled() {
        return getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Converts a location to an address.
     * TODO This should not return null values.
     *
     * @param longitude The longitude
     * @param latitude  The latitude
     * @return The address. Null if error or none found
     */
    static Address coordinatesToAddress(double longitude, double latitude) {
        List<Address> addresses;
        try {
            addresses = getGeocoder().getFromLocation(longitude, latitude, 1);
        } catch (IOException e) {
            System.out.println("Network / io problem: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.out.println("Our location object contains invalid values.");
            return null;
        }


        if (addresses == null) {
            System.out.println("No address list was created.");
            return null;
        }

        if (addresses.size() == 0) {
            System.out.println("No address could be found for our location object.");
            return null;
        }

        return addresses.get(0);
    }

}
