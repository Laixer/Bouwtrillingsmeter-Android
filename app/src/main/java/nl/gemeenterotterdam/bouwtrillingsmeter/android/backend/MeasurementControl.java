package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;
import java.util.List;

public class MeasurementControl {

    private ArrayList<Measurement> allMeasurements;

    /**
     * Constructor for this class
     * TODO Implement saving and loading
     */
    public MeasurementControl() {
        allMeasurements = new ArrayList<Measurement>();
    }

    /**
     * Returns the entire list of all measurements
     * @return The arraylist containing all measurements
     */
    public ArrayList<Measurement> getAllMeasurements() {
        return allMeasurements;
    }

    /**
     * Returns a measurement based on its index in the arraylist
     * @param index
     * @return The desired measurement
     */
    public Measurement getMeasurementByIndex(int index) throws IndexOutOfBoundsException {
        int count = allMeasurements.size();
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException();
        }

        return allMeasurements.get(index);
    }

    /**
     * Used to create a list of 5 measurements, used for debugging
     * TODO Remove this (debug)
     * @return
     */
    public void setDebugMeasurementsList() {
        allMeasurements = new ArrayList<Measurement>();

        allMeasurements.add(new Measurement("Measurement 1"));
        allMeasurements.add(new Measurement("Measurement 2"));
        allMeasurements.add(new Measurement("Measurement 3"));
        allMeasurements.add(new Measurement("Measurement 4"));
        allMeasurements.add(new Measurement("Measurement 5"));
    }

}
