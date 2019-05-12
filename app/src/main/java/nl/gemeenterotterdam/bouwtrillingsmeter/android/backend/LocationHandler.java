package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class extracts our current location.
 */
public class LocationHandler {

    /**
     * Static instance of the location manager.
     */
    private static LocationManager locationManager;

    private static Geocoder geocoder;

    /**
     * Loads the instance.
     */
    LocationHandler() {
        locationManager = (LocationManager) Backend.applicationContext.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(Backend.applicationContext, Locale.getDefault());
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
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * This gets our current device location using the GPS.
     * TODO Implement exception handling.
     */
    void fetchCurrentLocation() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    System.out.println("Got location!");
                    MeasurementControl.onNewLocationFetched(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            System.out.println("Requested");
        } catch (SecurityException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Converts a location to an address.
     *
     * @param longitude The longitude
     * @param latitude The latitude
     * @return The address. Null if error or none found
     */
    public static Address coordinatesToAddress(double longitude, double latitude) {
        List<Address> addresses = new ArrayList<Address>();
        try {
            addresses = geocoder.getFromLocation(longitude, latitude, 1);
        } catch (IOException e) {
            System.out.println("Network / io problem: " + e.toString());
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

        Address address = addresses.get(0);
        return address;
    }
}
