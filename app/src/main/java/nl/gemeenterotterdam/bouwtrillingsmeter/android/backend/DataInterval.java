package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import com.jjoe64.graphview.series.DataPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class holds a reference to all {@link DataPoint3D}s within a given time interval.
 * This also holds all result values of calculations (fft etc)
 */
public class DataInterval implements Serializable {

    public int index;
    public ArrayList<DataPoint3D<Long>> dataPoints3DAcceleration;
    public Date dateStart;
    public Date dateEnd;

    public ArrayList<DataPoint3D<Long>> velocities;
    public DataPoint3D<Long> velocitiesAbsMax;
    public ArrayList<DataPoint3D<Double>> frequencyAmplitudes;
    public DominantFrequencies dominantFrequencies;

    private boolean isLockedByThread;

    /**
     * Constructor
     */
    public DataInterval(int index) {
        this.index = index;

        dataPoints3DAcceleration = new ArrayList<DataPoint3D<Long>>();
        dateStart = Calendar.getInstance().getTime();
        isLockedByThread = false;
    }

    /**
     * Adds a datapoint to our arraylist
     *
     * @param dataPoint3DTime The datapoint to add
     */
    public void addDataPoint3D(DataPoint3D<Long> dataPoint3DTime) {
        dataPoints3DAcceleration.add(dataPoint3DTime);
    }

    /**
     * This gets called when the interval is over.
     * This class does not call this function by itself.
     * This function gets called by the DataHandler.
     */
    public void onIntervalEnd() {
        dateEnd = Calendar.getInstance().getTime();
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
    public void onThreadCalculationsStart() {
        isLockedByThread = true;
    }

    /**
     * This unlocks our object when the calculation thread is done with it.
     */
    public void onThreadCalculationsEnd() {
        isLockedByThread = false;
    }

    /**
     * This will clear our {@link #dataPoints3DAcceleration} array.
     * A clear is only performed if we are not {@link #isLockedByThread}.
     * This is done to save memory and prevent sending large files across the internet.
     */
    public boolean attemptDeleteDataPoints() {
        if (isLockedByThread) {
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
        long timeValue = dateStart.getTime() - MeasurementControl.getCurrentMeasurement().getStartTimeInMillis();
        result.add(new DataPoint3D<Long>(timeValue, dominantFrequencies.velocities));
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
        long timeValue = dateStart.getTime() - MeasurementControl.getCurrentMeasurement().getStartTimeInMillis();
        velocitiesAbsMax.xAxisValue = timeValue;
        result.add(velocitiesAbsMax);
        return result;
    }

}
