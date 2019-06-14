package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

public abstract class MpaGraph {

    private String title;
    private String xAxisLabel;
    private String yAxisLabel;

    private boolean scrolling;

    private ArrayList<Entry>[] entries;
    private ChartData[] chartDataConstant;
    private ChartData[] chartDataVariable;

    private TextView textViewTitle;
    private Chart chart;


    MpaGraph(String title, String xAxisLabel, String yAxisLabel, boolean scrolling, int variableChartCount) {
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.scrolling = scrolling;

        chartDataVariable = new ChartData[variableChartCount];
    }

    /**
     * This initializes our variables based on the type
     * of graph, being line or bar.
     */
    protected abstract void createArraysAndLists();

    /**
     * Call this when the activity is live and we actually
     * have a chart to push to.
     *
     * @param chart The chart object
     */
    void onChartCreated(Chart chart, TextView textViewTitle) {
        this.textViewTitle = textViewTitle;
        this.chart = chart;

        textViewTitle.setText(title);
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
