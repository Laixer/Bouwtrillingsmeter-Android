package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import pl.edu.icm.jlargearrays.Utilities;

/**
 * This class extracts our current location.
 */
public class LocationHandler {

    /**
     * Static instance of the location manager.
     */
    private static LocationManager locationManager;

    /**
     * Loads the instance.
     */
    LocationHandler() {
        locationManager = (LocationManager) Backend.applicationContext.getSystemService(Context.LOCATION_SERVICE);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);
            System.out.println("Requested");
        } catch (SecurityException e) {
            System.out.println(e.toString());
        }
    }
}
