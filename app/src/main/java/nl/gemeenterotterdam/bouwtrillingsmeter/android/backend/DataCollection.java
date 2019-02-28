package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * TODO Do we need this? I don't think so
 *
 *
 * DataCollection class
 * <p>
 * This class holds all data we collect during a period of parsing sensor data.
 * Not all this data will (probably) be stored in our measurement class.
 * Upon creation this class will consider itself to be in a data measuring state.
 */
class DataCollection {

    private ArrayList<Integer> data;
    private ArrayList<DominantFrequency> dominantFrequencies;
    private Date timeStart;
    private Date timeEnd;
    private boolean hasFinishedCollectingData;

    /**
     * Constructor
     * Saves the current time as starting time
     */
    public DataCollection() {
        data = new ArrayList<Integer>();
        dominantFrequencies = new ArrayList<DominantFrequency>();
        timeStart = Calendar.getInstance().getTime();
        hasFinishedCollectingData = false;
    }

    /**
     * This gets called when we wish to stop measuring data.
     * This will disable this data collection from having more datapoints added to it.
     * This will also save the end time.
     */
    public void onStopCollectionOfData() {
        if (!hasFinishedCollectingData) {
            hasFinishedCollectingData = false;
            timeEnd = Calendar.getInstance().getTime();
        }
    }

    /**
     * This will add datapoint to data if allowed
     * @param dataPoint The datapoint to add
     * @return True if adding the data was successful, false if this datacollection is already closed
     */
    public boolean addDataPoint(int dataPoint) {
        if (!hasFinishedCollectingData) {
            data.add(dataPoint);
            return true;
        }

        return false;
    }

    /**
     * This will add a dominant frequency to dominanctFrequencies if allowed
     * @param dominantFrequency The dominant frequency to add
     * @return True if adding the dominant frequency was successful, false if this datacollection is already closed
     */
    public boolean addDominantFrequency(DominantFrequency dominantFrequency) {
        if (!hasFinishedCollectingData) {
            dominantFrequencies.add(dominantFrequency);
            return true;
        }

        return false;
    }


    public ArrayList<Integer> getData() {
        return data;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public boolean isCurrentlyCollectingData() {
        return !hasFinishedCollectingData;
    }
}
