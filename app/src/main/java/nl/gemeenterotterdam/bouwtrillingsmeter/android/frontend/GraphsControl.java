package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
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
class GraphsControl implements DataIntervalClosedListener {

    private static final float MULTIPLIER_MS_TO_S = 0.001f;

    /**
     * All graphs.
     */
    private GraphFullyFunctional[] graphs;

    /**
     * Constructor for this class.
     * This subscribes as listener.
     */
    GraphsControl() {
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
        String[] titles = Utility.resources.
                getStringArray(R.array.graph_title);
        String[] axisHorizontal = Utility.resources.
                getStringArray(R.array.graph_axis_horizontal);
        String[] axisVertical = Utility.resources.
                getStringArray(R.array.graph_axis_vertical);
        String[] namesXYZ = Utility.resources.
                getStringArray(R.array.graph_legend_xyz_names);

        // Get legend names
        int[] colorsXYZ = Utility.getXYZColorsArray();
        int[] colorsDominant = new int[]{
                Utility.resources.getColor(R.color.graph_series_color_point)};

        // Create graphs
        graphs = new GraphFullyFunctional[5];

        graphs[0] = new GraphFullyFunctional(titles[0], axisHorizontal[0], axisVertical[0],
                false, MULTIPLIER_MS_TO_S);
        graphs[1] = new GraphFullyFunctional(titles[1], axisHorizontal[1], axisVertical[1],
                true, MULTIPLIER_MS_TO_S);
        graphs[2] = new GraphFullyFunctional(titles[2], axisHorizontal[2], axisVertical[2],
                true, MULTIPLIER_MS_TO_S);
        graphs[3] = new GraphFullyFunctional(titles[3], axisHorizontal[3], axisVertical[3],
                false, 1);
        graphs[4] = new GraphFullyFunctional(titles[4], axisHorizontal[4], axisVertical[4],
                false, 1);

        // Set limits separately
        graphs[0].setSizeConstants(3, 5, 0, 0);
        graphs[1].setSizeConstants(30, 500, 0, 0);
        graphs[2].setSizeConstants(30, 500, 0, 0);
        graphs[3].setSizeConstants(0, 0, 0, 100);
        graphs[4].setSizeConstants(0, 0, 0, 100);

        // Add the constant line to our dominant frequency plot
        graphs[4].addConstantLine(
                LimitConstants.getLimitAsEntries(),
                Utility.resources.getString(R.string.graph_legend_limitline_name),
                Utility.resources.getColor(R.color.graph_dominant_constant_line)
        );
    }

    /**
     * Triggers all before functions.
     */
    void beforeAppendingData() {
        for (GraphFullyFunctional graphFullyFunctional : graphs) {
            graphFullyFunctional.beforeAppendingData();
        }
    }

    /**
     * Triggers all after functions.
     */
    void afterAppendingData() {
        for (GraphFullyFunctional graphFullyFunctional : graphs) {
            graphFullyFunctional.afterAppendingData();
        }
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

        beforeAppendingData();

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
        graphs[4].sendNewDataToChart(dataInterval.getAllDominantFrequenciesAsDataPoints());

        afterAppendingData();
    }

    /**
     * Return all graphs
     *
     * @return All created graphs, null is possible
     */
    Graph[] getGraphs() {
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
