package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class holds a reference to all {@link DataPoint3D}s within a given time interval.
 * This also holds all result values of calculations (fft etc)
 */
public class DataInterval implements Serializable {

    // Gaat naar de server
    private final String measurementUID;
    private final int index;
    private final long millisRelativeStart;
    private long millisRelativeEnd;
    private ArrayList<DataPoint3D<Long>> dataPoints3DAcceleration;
    private ArrayList<DataPoint3D<Long>> velocities;
    private DataPoint3D<Long> velocitiesAbsoluteMax;
    private ArrayList<DataPoint3D<Double>> frequencyAmplitudes;
    private DominantFrequencies dominantFrequencies;

    // Gaat niet naar de server
    private final long millisStart;
    private boolean lockedByThread;

    /**
     * Constructor
     */
    DataInterval(String measurementUID, int index) {
        this.measurementUID = measurementUID;
        this.index = index;

        dataPoints3DAcceleration = new ArrayList<DataPoint3D<Long>>();
        millisStart = Calendar.getInstance().getTimeInMillis();
        millisRelativeStart = millisStart - MeasurementControl.getCurrentMeasurement().getStartTimeInMillis();
        lockedByThread = false;
    }

    /**
     * Adds a datapoint to our arraylist
     *
     * @param dataPoint3DTime The datapoint to add
     */
    void addDataPoint3D(DataPoint3D<Long> dataPoint3DTime) {
        dataPoints3DAcceleration.add(dataPoint3DTime);
    }

    /**
     * This gets called when the interval is over.
     * This class does not call this function by itself.
     * This function gets called by the DataHandler.
     */
    void onIntervalEnd() {
        millisRelativeEnd = Calendar.getInstance().getTimeInMillis() - millisStart;
    }

    /**
     * Checks if we have exceeded any limits within our interval.
     * The checks are done on the {@link #dominantFrequencies}.
     *
     * @return True if we have exceeded any limits.
     */
    public boolean isExceedingLimit() {
        for (boolean bool : dominantFrequencies.exceedsLimit) {
            if (bool) {
                return true;
            }
        }

        return false;
    }

    /**
     * This locks our object when the calculations thread is working on it.
     */
    void onThreadCalculationsStart() {
        lockedByThread = true;
    }

    /**
     * This unlocks our object when the calculation thread is done with it.
     */
    void onThreadCalculationsEnd() {
        lockedByThread = false;
    }

    /**
     * This will clear our {@link #dataPoints3DAcceleration} array.
     * A clear is only performed if we are not {@link #lockedByThread}.
     * This is done to save memory and prevent sending large files across the internet.
     */
    boolean attemptDeleteDataPoints() {
        if (lockedByThread) {
            return false;
        } else {
            dataPoints3DAcceleration.clear();
            return true;
        }
    }

    /**
     * This transforms our dominant frequency velocities into an arraylist of 1 datapoint
     *
     * @return The arraylist
     */
    public ArrayList<DataPoint3D<Long>> getDominantFrequenciesAsDataPoints() {
        ArrayList<DataPoint3D<Long>> result = new ArrayList<DataPoint3D<Long>>();
        result.add(new DataPoint3D<Long>(millisRelativeStart, dominantFrequencies.velocities));
        return result;
    }

    /**
     * This transforms our exceeding frequencies into datapoints.
     * This is a workaround to conserve the graph abstract methods.
     * If a dimension does not exceed any limits we set its x and y value to -1.
     * TODO Maybe implement a non-workaround method.
     *
     * @return An arraylist containing these datapoints.
     */
    public ArrayList<DataPoint3D<Double>> getExceedingAsDataPoints() {
        ArrayList<DataPoint3D<Double>> result = new ArrayList<DataPoint3D<Double>>();

        for (int dimension = 0; dimension < 3; dimension++) {
            double frequency = -1;
            float[] velocities = new float[]{-1, -1, -1};

            // If we exceed we create a datapoint
            if (dominantFrequencies.exceedsLimit[dimension]) {
                frequency = dominantFrequencies.frequencies[dimension];
                velocities[0] = dominantFrequencies.velocities[dimension];
            }

            result.add(new DataPoint3D<Double>(frequency, velocities));
        }

        // Return result
        return result;
    }

    /**
     * Converts our max velocity datapoint3d to an arraylist of datapoints3d with size 1.
     * This is implemented for consistent graph usage.
     *
     * @return The "arraylist"
     */
    public ArrayList<DataPoint3D<Long>> getVelocitiesAbsMaxAsDataPoints() {
        ArrayList<DataPoint3D<Long>> result = new ArrayList<DataPoint3D<Long>>(1);
        velocitiesAbsoluteMax.xAxisValue = millisRelativeStart;
        result.add(velocitiesAbsoluteMax);
        return result;
    }

    void setVelocities(ArrayList<DataPoint3D<Long>> velocities) {
        if (this.velocities == null) {
            this.velocities = velocities;
        } else {
            throw new IllegalArgumentException("Cannot change velocities once they have been set.");
        }
    }

    void setVelocitiesAbsoluteMax(DataPoint3D<Long> velocitiesAbsoluteMax) {
        if (this.velocitiesAbsoluteMax == null) {
            this.velocitiesAbsoluteMax = velocitiesAbsoluteMax;
        } else {
            throw new IllegalArgumentException("Cannot change absolute max velocities once they have been set.");
        }
    }

    void setFrequencyAmplitudes(ArrayList<DataPoint3D<Double>> frequencyAmplitudes) {
        if (this.frequencyAmplitudes == null) {
            this.frequencyAmplitudes = frequencyAmplitudes;
        } else {
            throw new IllegalArgumentException("Cannot change frequency amplitudes once they have been set.");
        }
    }

    void setDominantFrequencies(DominantFrequencies dominantFrequencies) {
        if (this.dominantFrequencies == null) {
            this.dominantFrequencies = dominantFrequencies;
        } else {
            throw new IllegalArgumentException("Cannot change dominant frequencies once they have been set.");
        }
    }

    public String getMeasurementUID() {
        return measurementUID;
    }

    public int getIndex() {
        return index;
    }

    public long getMillisStart() {
        return millisStart;
    }

    public long getMillisRelativeStart() {
        return millisRelativeStart;
    }

    public long getMillisRelativeEnd() {
        return millisRelativeEnd;
    }

    public ArrayList<DataPoint3D<Long>> getDataPoints3DAcceleration() {
        return dataPoints3DAcceleration;
    }

    public ArrayList<DataPoint3D<Long>> getVelocities() {
        return velocities;
    }

    public DataPoint3D<Long> getVelocitiesAbsoluteMax() {
        return velocitiesAbsoluteMax;
    }

    public ArrayList<DataPoint3D<Double>> getFrequencyAmplitudes() {
        return frequencyAmplitudes;
    }

    public DominantFrequencies getDominantFrequencies() {
        return dominantFrequencies;
    }
}
