package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * This class extracts our current location.
 */
class LocationHandler implements LocationListener {


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
    void getLocation() {
        // Get the location manager if we havent already
        if (locationManager == null) {
            locationManager = (LocationManager) Backend.applicationContext.getSystemService(Context.LOCATION_SERVICE);
        }

        // Request new location
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            System.out.println("No permission to save location. Handle this.");
        }
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
}
