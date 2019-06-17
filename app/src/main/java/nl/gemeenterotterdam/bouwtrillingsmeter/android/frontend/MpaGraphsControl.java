package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import java.util.ArrayList;
import java.util.List;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataInterval;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataIntervalClosedListener;

/**
 * This holds our graphs. This is separated because
 * we want to append graphs even when the activity
 * isn't loaded.
 * <p>
 * This gets created by our {@link MeasuringActivity}.
 * <p>
 * TODO Setup a way to make this stop receiving data intervals when we are no longer measuring?
 * Gaat automatisch? Check dit!
 */
class MpaGraphsControl implements DataIntervalClosedListener {

    /**
     * All graphs.
     */
    private MpaGraph[] graphs;

    /**
     * Create all graphs. Reset what we already have.
     * <p>
     * Graph 1: Acceleration // time (line)
     * Graph 2: Highest velocity // time (bar)
     * Graph 3: Dominant frequency // time (bar)
     * Graph 4: Amplitude // frequency (line)
     * Graph 5: Dominant frequency // frequency (point)
     */
    void createAllGraphs() {
        // Get constants
        String[] title = Utility.resources.
                getStringArray(R.array.graph_title);
        String[] axisHorizontal = Utility.resources.
                getStringArray(R.array.graph_axis_horizontal);
        String[] axisVertical = Utility.resources.
                getStringArray(R.array.graph_axis_vertical);
        String[] namesXYZ = Utility.resources.
                getStringArray(R.array.graph_legend_xyz_names);
        String[] namesAmplitde = new String[]{
                Utility.resources.getString(R.string.graph_legend_fourier)};
        String[] namesDominant = new String[]{
                Utility.resources.getString(R.string.graph_legend_exceeding_name)};

        // Create graphs
        graphs = new MpaGraph[5];
        graphs[0] = new MpaGraphLine(title[0], axisHorizontal[0],
                axisVertical[0], true, namesXYZ);
        graphs[1] = new MpaGraphBar(title[1], axisHorizontal[1],
                axisVertical[1], true, namesXYZ);
        graphs[2] = new MpaGraphBar(title[2], axisHorizontal[2],
                axisVertical[2], true, namesXYZ);
        graphs[3] = new MpaGraphLine(title[3], axisHorizontal[3],
                axisVertical[3], false, namesAmplitde);
        graphs[4] = new MpaGraphLine(title[4], axisHorizontal[4],
                axisVertical[4], false, namesDominant);
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
    MpaGraph[] getGraphs() {
        return graphs;
    }

    /**
     * Used to check how many graphs we have.
     *
     * @return The graphs count
     */
    int getGraphsCount() {
        return graphs.length;
    }

}
