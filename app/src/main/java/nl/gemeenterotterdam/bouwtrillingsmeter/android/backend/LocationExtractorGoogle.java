package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * Uses the Google location API to extract
 * the location of the device.
 */
public class LocationExtractorGoogle {

    private FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Constructor.
     * <p>
     * Throws an exception if we can't access the
     * fused location provider client.
     */
    public LocationExtractorGoogle() throws SecurityException {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Backend.applicationContext);

        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener((Location location) -> {
                        if (location != null) {
                            onLocationFetched(location);
                        }
                    });
        } catch (SecurityException e) {
            System.out.println(e);
        }
    }

    /**
     * This attempts to fetch the location.
     */
    public void callForLocation() {
        try {
            fusedLocationProviderClient.getLastLocation();
        } catch (SecurityException e) {
            System.out.println(e);
        }
    }

    /**
     * This gets called when we find our location.
     * This will call our measurement control.
     *
     * @param location The discovered location
     */
    private void onLocationFetched(Location location) {
        assert location != null;

        MeasurementControl.onNewLocationFetched(location);
    }

}
