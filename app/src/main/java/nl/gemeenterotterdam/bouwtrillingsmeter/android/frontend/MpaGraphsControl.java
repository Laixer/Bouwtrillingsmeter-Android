package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataHandler;
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
     * Constructor for this class.
     * This subscribes as listener.
     */
    MpaGraphsControl() {
        DataHandler.addDataIntervalClosedListener(this);
    }

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
        String[] namesDominant = new String[]{
                Utility.resources.getString(R.string.graph_legend_exceeding_name)};

        // Get legend names
        int[] colorsXYZ = Utility.getXYZColorsArray();
        int[] colorsDominant = new int[]{R.color.graph_series_color_point};

        // Create graphs
        graphs = new MpaGraph[5];
        graphs[0] = new MpaGraphLine(title[0], axisHorizontal[0], axisVertical[0],
                true, false, namesXYZ, colorsXYZ, false);
        graphs[1] = new MpaGraphBar(title[1], axisHorizontal[1], axisVertical[1],
                true, false, namesXYZ, colorsXYZ);
        graphs[2] = new MpaGraphBar(title[2], axisHorizontal[2], axisVertical[2],
                true, false, namesXYZ, colorsXYZ);
        graphs[3] = new MpaGraphLine(title[3], axisHorizontal[3], axisVertical[3],
                false, true, namesXYZ, colorsXYZ, false);
        graphs[4] = new MpaGraphLine(title[4], axisHorizontal[4], axisVertical[4],
                false, false, namesDominant, colorsDominant, true);

        // Set limits separately
        //graphs[0].setSizeConstants(3, 10, 0, 0);
        //graphs[1].setSizeConstants(30, 100, 0, 0);
        //graphs[2].setSizeConstants(30, 100, 0, 0);
        //graphs[3].setSizeConstants(0, 0, 0, 100);
        //graphs[4].setSizeConstants(0, 0, 0, 100);

        // TODO Add constant line
    }

    /**
     * This gets called when a data interval is closed.
     *
     * @param dataInterval The data interval that was closed
     */
    @Override
    public void onDataIntervalClosed(DataInterval dataInterval) {
        if (graphs == null || dataInterval == null) {
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
