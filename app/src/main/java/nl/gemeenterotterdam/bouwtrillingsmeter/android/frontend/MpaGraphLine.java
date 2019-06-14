package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

/**
 * Instance of a line graph.
 */
class MpaGraphLine extends MpaGraph {

    /**
     * Creates an instance of a line graph.
     *
     * @param title        The graph title
     * @param xAxisLabel   The x axis label
     * @param yAxisLabel   The y axis label
     * @param scrolling    If set to true we append data to the right,
     *                     if set to false we refresh the graph each
     *                     iteration
     * @param dataSetNames The names of all data sets,
     *                     this also indicates their count
     */
    public MpaGraphLine(String title, String xAxisLabel, String yAxisLabel, boolean scrolling, ArrayList<String> dataSetNames) {
        super(title, xAxisLabel, yAxisLabel, scrolling, dataSetNames);


    }

    /**
     * If we are not {@link #scrolling} then all chart data must
     * be reset to refresh the entire graph.
     */
    @Override
    protected void resetChartData() {

    }

    /**
     * This appends the data points to our entry lists. This is
     * abstract because we have to implement this differently for
     * bar and line graphs.
     *
     * @param dataPoints3D The data to append
     */
    @Override
    protected <T> void appendDataToEntries(ArrayList<DataPoint3D<T>> dataPoints3D) {

    }

    /**
     * This appends our entry to the graph. This is done differently
     * for bar and line graphs.
     */
    @Override
    protected void pushToChart() {

    }
}
