package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * This class extracts our current location.
 */
class LocationHandler implements LocationListener, OnSuccessListener {


    /**
     * Static instance of the location service.
     */
    private static LocationManager locationManager;


    void initialize() {

    }

    /**
     * This gets our current device location using the GPS.
     * TODO Implement exception handling.
     */
    void fetchCurrentLocation() {
        /*
        // Attempt
        try {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Backend.applicationContext);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this);
        } catch (SecurityException e) {
            //
        }*/


        // New attempt
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        System.out.println(String.format("Location @ %s, %s", longitude, latitude));
                    }
                }
            }
        };

        try {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Backend.applicationContext);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException e) {
            //
        }


        // Get the location manager if we havent already
        /*if (locationManager == null) {
            locationManager = (LocationManager) Backend.applicationContext.getSystemService(Context.LOCATION_SERVICE);
        }

        // Request new location
        try {
            Criteria criteria = new Criteria();
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setAltitudeRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setBearingRequired(false);
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        } catch (SecurityException e) {
            System.out.println("No permission to save location. Handle this.");
        }*/
    }

    /**
     * Called when the location has changed.
     *
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        int i = 0;
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        //
    }

    public void onProviderEnabled(String provider) {
        //
    }

    public void onProviderDisabled(String provider) {
        //
    }

    @Override
    public void onSuccess(Object o) {
        int i = 0;
    }
}
