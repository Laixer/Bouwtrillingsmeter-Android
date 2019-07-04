package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * Uses the Google location API to extract
 * the location of the device.
 */
class LocationExtractor {

    private FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Constructor.
     * <p>
     * Throws an exception if we can't access the
     * fused location provider client.
     */
    LocationExtractor() throws SecurityException {
        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(Backend.applicationContext);
        try {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Location location) -> {
               /* Do nothing */
            });
            fusedLocationProviderClient.getLastLocation();
            return;

        } catch (SecurityException e) {
            /* Do nothing */
        }

        throw new SecurityException("Error while calling required " +
                "methods for FusedLocationProvider");
    }

    /**
     * This attempts to fetch the location. Any
     * subscribed measurements will receive a
     * call.
     */
    void callForLocation() {
        try {
            fusedLocationProviderClient.getLastLocation();
        } catch (SecurityException e) {
            System.out.println(e);
        }
    }

    /**
     * Subscribe a measurement to be assigned the next
     * discovered location.
     *
     * @param measurement The measurement
     */
    void subscribeForLocation(Measurement measurement) {
        assert measurement != null;

        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener((Location location) -> {
                        if (location != null) {
                            onLocationFetched(location, measurement);
                        }
                    });
        } catch (SecurityException e) {
            System.out.println(e);
        }
    }

    /**
     * This gets called when we find our location.
     *
     * @param location    The location
     * @param measurement The measurement to which the
     *                    location must be saved
     */
    private void onLocationFetched(Location location, Measurement measurement) {
        assert location != null;
        assert measurement != null;
        measurement.setLocation(location);
    }

}
