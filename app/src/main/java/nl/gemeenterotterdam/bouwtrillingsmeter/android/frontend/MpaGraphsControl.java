package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataHandler;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataInterval;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataIntervalClosedListener;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.LimitConstants;

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
        graphs[0] = new MpaGraphCombined(title[0], axisHorizontal[0], axisVertical[0],
                true, false, namesXYZ, colorsXYZ, false, 0.001f);
        graphs[1] = new MpaGraphBar(title[1], axisHorizontal[1], axisVertical[1],
                true, false, namesXYZ, colorsXYZ, 0.001f);
        graphs[2] = new MpaGraphBar(title[2], axisHorizontal[2], axisVertical[2],
                true, false, namesXYZ, colorsXYZ, 0.001f);
        graphs[3] = new MpaGraphCombined(title[3], axisHorizontal[3], axisVertical[3],
                false, true, namesXYZ, colorsXYZ, false, 1);
        graphs[4] = new MpaGraphCombined(title[4], axisHorizontal[4], axisVertical[4],
                false, false, namesDominant, colorsDominant, true, 1);

        // Set limits separately
        graphs[0].setSizeConstants(3, 5, 0, 0);
        graphs[1].setSizeConstants(30, 500, 0, 0);
        graphs[2].setSizeConstants(30, 500, 0, 0);
        graphs[3].setSizeConstants(0, 0, 0, 100);
        graphs[4].setSizeConstants(0, 0, 0, 100);

        // Add the constant line to our dominant frequency plot
        ((MpaGraphCombined)graphs[4]).addConstantLine(
                LimitConstants.getLimitAsEntries(),
                Utility.resources.getString(R.string.graph_legend_limitline_name),
                R.color.graph_dominant_constant_line
        );
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
        graphs[0].sendNewDataToChart(dataInterval.getDataPoints3DAcceleration());
        graphs[1].sendNewDataToChart(dataInterval.getVelocitiesAbsMaxAsDataPoints());
        graphs[2].sendNewDataToChart(dataInterval.getDominantFrequenciesAsDataPoints());
        graphs[3].sendNewDataToChart(dataInterval.getFrequencyAmplitudes());
        graphs[4].sendNewDataToChart(dataInterval.getExceedingAsDataPoints());
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

    /**
     * Kills this process.
     */
    void forceFinish() {
        DataHandler.removeDataIntervalClosedListener(this);
        graphs = null;
    }

}
