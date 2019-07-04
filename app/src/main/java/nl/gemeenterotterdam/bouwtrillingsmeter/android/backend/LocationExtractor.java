package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * Uses the Google location API to extract
 * the location of the device.
 */
public class LocationExtractor {

    private FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Constructor.
     * <p>
     * Throws an exception if we can't access the
     * fused location provider client.
     */
    public LocationExtractor() throws SecurityException {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Backend.applicationContext);


    }

    /**
     * This attempts to fetch the location. Any
     * subscribed measurements will receive a
     * call.
     */
    public void callForLocation() {
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
    public void subscribeForLocation(Measurement measurement) {
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
        measurement.setLocation(location);
    }

}
