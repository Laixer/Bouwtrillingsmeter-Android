package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataInterval;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataIntervalClosedListener;

/**
 * This holds our graphs. This is separated because
 * we want to append graphs even when the activity
 * isn't loaded.
 */
class MpaGraphControl implements DataIntervalClosedListener {

    /**
     * All graphs.
     */
    private MpaGraph[] graphs;

    /**
     * Create all graphs. Reset what we already have.
     */
    void createAllGraphs() {
        graphs = new MpaGraph[5];
    }

    /**
     * This gets called when a data interval is closed.
     *
     * @param dataInterval The data interval that was closed
     */
    @Override
    public void onDataIntervalClosed(DataInterval dataInterval) {
        if (graphs == null) {
            return;
        }

        /*
         * Graph 1: Acceleration // time (line)
         * Graph 2: Highest velocity // time (block)
         * Graph 3: Dominant frequency // time (block)
         * Graph 4: Amplitude // frequency (line)
         * Graph 5: Dominant frequency // frequency (point)
         */
        graphs[0].sendNewDataToSeries(dataInterval.getDataPoints3DAcceleration());
        graphs[1].sendNewDataToSeries(dataInterval.getVelocitiesAbsMaxAsDataPoints());
        graphs[2].sendNewDataToSeries(dataInterval.getDominantFrequenciesAsDataPoints());
        graphs[3].sendNewDataToSeries(dataInterval.getFrequencyAmplitudes());
        graphs[4].sendNewDataToSeries(dataInterval.getExceedingAsDataPoints());
    }

    /**
     * Return all graphs
     *
     * @return All created graphs, null is possible
     */
    Graph[] getGraphs() {
        return graphs;
    }

}
