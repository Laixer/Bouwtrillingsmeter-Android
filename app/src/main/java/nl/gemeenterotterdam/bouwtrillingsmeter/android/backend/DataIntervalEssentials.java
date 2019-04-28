package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.io.Serializable;

/**
 * This class contains only those parameters of our {@link DataInterval}
 * that we wish to send to our server as soon as possible.
 */
class DataIntervalEssentials implements Serializable {

    private String measurementUID;
    private int dataIntervalIndex;
    private DataPoint3D<Long> velocitiesAbsMax;
    private DominantFrequencies dominantFrequencies;

    DataIntervalEssentials(String measurementUID, int dataIntervalIndex, DataPoint3D<Long> velocitiesAbsMax, DominantFrequencies dominantFrequencies) {
        this.measurementUID = measurementUID;
        this.dataIntervalIndex = dataIntervalIndex;
        this.velocitiesAbsMax = velocitiesAbsMax;
        this.dominantFrequencies = dominantFrequencies;
    }

    public String getMeasurementUID() {
        return measurementUID;
    }

    public int getDataIntervalIndex() {
        return dataIntervalIndex;
    }

    public DataPoint3D<Long> getVelocitiesAbsMax() {
        return velocitiesAbsMax;
    }

    public DominantFrequencies getDominantFrequencies() {
        return dominantFrequencies;
    }

}
