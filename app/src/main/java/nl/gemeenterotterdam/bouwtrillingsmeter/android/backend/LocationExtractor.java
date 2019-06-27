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
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class extracts our current location.
 */
public class LocationExtractor {

    private static LocationExtractor locationExtractor;

    private LocationManager locationManager;

    private Geocoder geocoder;

    /**
     * Loads the instance.
     */
    LocationExtractor() {
        locationExtractor = this;
        locationManager = (LocationManager) Backend.applicationContext.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(Backend.applicationContext, Locale.getDefault());
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

        // Separate thread because we need a looper.
        Thread thread = new Thread(() -> {

            // Only prepare the looper if we need to
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            try {
                if (locationManager == null) {
                    System.out.println("Location manager is null!");
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            } catch (SecurityException e) {
                System.out.println("Security exception: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected exception: " + e.getMessage());
            }

        });

        // Launch the thread
        thread.start();
    }

    /**
     * @return internal getter for the location manager.
     */
    private static LocationManager getLocationManager() {
        return locationExtractor.locationManager;
    }

    /**
     *
     * @return internal getter for the geocoder.
     */
    private static Geocoder getGeocoder() {
        return locationExtractor.geocoder;
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
     * @param latitude The latitude
     * @return The address. Null if error or none found
     */
    static Address coordinatesToAddress(double longitude, double latitude) {
        List<Address> addresses = new ArrayList<Address>();
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
