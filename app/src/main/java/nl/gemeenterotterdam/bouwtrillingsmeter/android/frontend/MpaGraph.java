package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
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
    protected boolean refreshing;
    protected float xMultiplier;

    protected float minimumWidth;
    protected float maximumWidth;
    protected float xMin;
    protected float xMax;

    protected ArrayList<LineDataSet> constantLineDataSets;
    protected ArrayList<ChartData> chartDataVariable;

    protected TextView textViewTitle;
    protected TextView textViewAxisX;
    protected TextView textViewAxisY;
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
     * @param refreshing   If set to true all data will be refreshed
     *                     upon appending new data
     * @param dataSetNames The names of all data sets,
     *                     this also indicates their count
     * @param colors       The color integer for each
     *                     data set
     * @param xMultiplier  The multiplier for the x values
     */
    protected MpaGraph(String title, String xAxisLabel, String yAxisLabel,
                       boolean scrolling, boolean refreshing, String[] dataSetNames,
                       int[] colors, float xMultiplier) {
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.dataSetNames = dataSetNames;
        this.colors = colors;

        this.scrolling = scrolling;
        this.refreshing = refreshing;
        this.xMultiplier = xMultiplier;

        constantLineDataSets = new ArrayList<>();
        chartDataVariable = new ArrayList<>();
    }

    /**
     * This setups some constants.
     *
     * @param minimumWidth Used if we scroll
     * @param maximumWidth Used if we scroll
     * @param xMin         Used if we don't scroll
     * @param xMax         Used if we don't scroll
     */
    void setSizeConstants(float minimumWidth, float maximumWidth, float xMin, float xMax) {
        this.minimumWidth = minimumWidth;
        this.maximumWidth = maximumWidth;
        this.xMin = xMin;
        this.xMax = xMax;
    }

    /**
     * Call this when the text view for our title is created.
     *
     * @param textViewTitle The text view
     */
    void onTextViewsCreated(TextView textViewTitle,
                            TextView textViewAxisX,
                            TextView textViewAxisY) {
        this.textViewTitle = textViewTitle;
        this.textViewAxisX = textViewAxisX;
        this.textViewAxisY = textViewAxisY;

        textViewTitle.setText(title);
        textViewAxisX.setText(xAxisLabel);
        textViewAxisY.setText(yAxisLabel);
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
     * This will append new data to the series. This will always
     * push to the data set but will not always update to the view.
     *
     * @param dataPoints3D The datapoints to add
     * @param <T>          The type of datapoint
     */
    <T> void sendNewDataToChart(ArrayList<DataPoint3D<T>> dataPoints3D) {
        // If we are not scrolling we should reset our view
        if (refreshing) {
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

    protected void styleChart() {
        chart.setDescription(null);

        if (!scrolling) {
            chart.getXAxis().setAxisMinimum(xMin);
            chart.getXAxis().setAxisMaximum(xMax);
        }
    }

    protected void forceAxisMinMAx(float xLowest, float xHighest) {
        if (scrolling) {
            chart.getXAxis().setAxisMinimum(Math.max(xLowest, xHighest - maximumWidth));
            chart.getXAxis().setAxisMaximum(xHighest);
        }
    }

    protected void styleLineDataSet(LineDataSet lineDataSet, int color) {
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setColor(color);
        lineDataSet.setHighLightColor(color);
        lineDataSet.setLineWidth(1);
        lineDataSet.setDrawValues(false);
    }

    protected void styleScatterDataSet(ScatterDataSet scatterDataSet, int color) {
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        scatterDataSet.setColor(R.color.graph_series_color_x);
        scatterDataSet.setHighLightColor(R.color.graph_series_color_x);
        scatterDataSet.setScatterShapeSize(10);
        scatterDataSet.setDrawValues(false);
    }

    protected void styleBarDataSet(BarDataSet barDataSet, int color) {
        barDataSet.setColor(color);
        // barDataSet.setHighLightColor(color);
        // barDataSet.setHighLightAlpha(160);
        barDataSet.setDrawValues(false);
    }

}
