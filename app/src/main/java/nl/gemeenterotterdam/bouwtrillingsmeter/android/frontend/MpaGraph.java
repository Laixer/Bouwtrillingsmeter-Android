package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.ChartData;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

/**
 * Base for a graph.
 */
abstract class MpaGraph {

    protected String title;
    protected String xAxisLabel;
    protected String yAxisLabel;
    protected String[] dataSetNames;
    protected int[] colors;

    protected boolean scrolling;

    protected ArrayList<ChartData> chartDataConstant;
    protected ArrayList<ChartData> chartDataVariable;

    protected TextView textViewTitle;
    protected Chart chart;

    /**
     * Constructor, call by calling super().
     *
     * @param title        The graph title
     * @param xAxisLabel   The x axis label
     * @param yAxisLabel   The y axis label
     * @param scrolling    If set to true we append data to the right,
     *                     if set to false we refresh the graph each
     *                     iteration
     * @param dataSetNames The names of all data sets,
     *                     this also indicates their count
     * @param colors       The color integer for each
     *                     data set
     */
    protected MpaGraph(String title, String xAxisLabel, String yAxisLabel, boolean scrolling, String[] dataSetNames, int[] colors) {
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.dataSetNames = dataSetNames;
        this.colors = colors;

        this.scrolling = scrolling;

        chartDataConstant = new ArrayList<>();
        chartDataVariable = new ArrayList<>();
    }

    /**
     * Call this when the text view for our title is created.
     *
     * @param textViewTitle The text view
     */
    void onTextViewTitleCreated(TextView textViewTitle) {
        this.textViewTitle = textViewTitle;
        textViewTitle.setText(title);
    }

    /**
     * This creates a chart that fits to the type.
     * Manually add this to a container.
     *
     * @param context The context from which we call this
     * @return The created chart
     */
    abstract Chart createChart(Context context);

    /**
     * Call this to add a constant line to the graph.
     *
     * @param chartData The chart data to add
     */
    void addConstantChartData(ChartData chartData) {
        chartDataConstant.add(chartData);
    }

    /**
     * This will append new data to the series. This will always
     * push to the data set but will not always update to the view.
     *
     * @param dataPoints3D The datapoints to add
     * @param <T>          The type of datapoint
     */
    protected <T> void sendNewDataToSeries(ArrayList<DataPoint3D<T>> dataPoints3D) {
        // If we are not scrolling we should reset our view
        if (!scrolling) {
            resetChartData();
        }

        // Append the data
        appendDataToEntries(dataPoints3D);

        // Push if we should
        if (shouldRender()) {
            pushToChart();
        }
    }

    /**
     * If we are not {@link #scrolling} then all chart data must
     * be reset to refresh the entire graph.
     */
    protected abstract void resetChartData();

    /**
     * This appends the data points to our entry lists. This is
     * abstract because we have to implement this differently for
     * bar and line graphs.
     *
     * @param dataPoints3D The data to append
     * @param <T>          The type of datapoint
     */
    protected abstract <T> void appendDataToEntries(ArrayList<DataPoint3D<T>> dataPoints3D);

    /**
     * This appends our entry to the graph. This is done differently
     * for bar and line graphs.
     */
    protected abstract void pushToChart();

    /**
     * This determines whether or not our UI should attempt to
     * push data to the graph. Currently this only checks if
     * we actually have a graph, but in the future this might
     * change for performance optimization.
     *
     * @return True if we should render
     */
    private boolean shouldRender() {
        return chart != null;
    }

}
