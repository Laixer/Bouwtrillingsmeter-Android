package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

/**
 * Instance of a line graph.
 */
class GraphCombined extends Graph {

    private ArrayList<Entry>[] entries;
    private IBarLineScatterCandleBubbleDataSet[] graphDataSets;
    private CombinedData combinedData;
    private boolean useAsPoints;
    private int constantLineColor;

    /**
     * Creates an instance of a line graph.
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
     * @param useAsPoints  True if this should behave as a point chart
     *                     for our dynamic set(s)
     */
    GraphCombined(String title, String xAxisLabel, String yAxisLabel, boolean scrolling, boolean refreshing, String[] dataSetNames, int[] colors, boolean useAsPoints, float xMultiplier) {
        super(title, xAxisLabel, yAxisLabel, scrolling, refreshing, dataSetNames, colors, xMultiplier);
        this.useAsPoints = useAsPoints;

        // Prepare entries
        entries = new ArrayList[dataSetNames.length];
        for (int i = 0; i < dataSetNames.length; i++) {
            entries[i] = new ArrayList<>();
        }

        // Prepare line data sets
        // Do this generic
        combinedData = new CombinedData();
        if (useAsPoints) {
            graphDataSets = new ScatterDataSet[dataSetNames.length];
            for (int i = 0; i < graphDataSets.length; i++) {
                graphDataSets[i] = new ScatterDataSet(entries[i], dataSetNames[i]);
                styleScatterDataSet((ScatterDataSet) graphDataSets[i], colors[i]);
                combinedData.addDataSet(graphDataSets[i]);
            }
        } else {
            graphDataSets = new LineDataSet[dataSetNames.length];
            for (int i = 0; i < graphDataSets.length; i++) {
                graphDataSets[i] = new LineDataSet(entries[i], dataSetNames[i]);
                styleLineDataSet((LineDataSet) graphDataSets[i], colors[i]);
                combinedData.addDataSet(graphDataSets[i]);
            }
        }
    }

    /**
     * This creates a chart that fits to the type.
     * Manually add this to a container.
     *
     * @param context The context from which we call this
     * @return The created chart
     */
    @Override
    Chart createChart(Context context) {
        chart = new CombinedChart(context);
        chart.setData(combinedData);
        ((CombinedChart) chart).setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.LINE,
                CombinedChart.DrawOrder.SCATTER
        });
        chart.invalidate();
        styleChart();

        ((CombinedChart) chart).getAxisRight().setEnabled(false);
        return chart;
    }

    /**
     * This adds a constant line to our graph.
     *
     * @param entries The entries
     * @param name    The name
     * @param color   The color as resource int
     */
    void addConstantLine(ArrayList<Entry> entries, String name, int color) {
        constantLineColor = color;
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        styleLineDataSet(lineDataSet, color);
        constantLineDataSets.add(lineDataSet);
    }

    /**
     * If we are not {@link #scrolling} then all chart data must
     * be reset to refresh the entire graph.
     */
    @Override
    protected void resetChartData() {
        for (IBarLineScatterCandleBubbleDataSet graphDataSet : graphDataSets) {
            graphDataSet.clear();
        }

        for (int i = 0; i < entries.length; i++) {
            entries[i] = new ArrayList<>();
        }
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
        for (DataPoint3D<T> dataPoint3D : dataPoints3D) {
            for (int i = 0; i < dataSetNames.length; i++) {
                Entry entry = new Entry(
                        dataPoint3D.xAxisValueAsFloat() * xMultiplier,
                        dataPoint3D.values[i]);
                entries[i].add(entry);
            }
        }
    }

    /**
     * This appends our entry to the graph. This is done differently
     * for bar and line graphs.
     */
    @Override
    protected void pushToChart() {
        CombinedData combinedData = new CombinedData();
        LineData lineData = new LineData();
        ScatterData scatterData = new ScatterData();

        // Add our variable lines and scatters
        for (int i = 0; i < dataSetNames.length; i++) {
            if (useAsPoints) {
                ScatterDataSet scatterDataSet = new ScatterDataSet(entries[i], dataSetNames[i]);
                Collections.sort(entries[i], new EntryXComparator());
                styleScatterDataSet(scatterDataSet, colors[i]);
                scatterData.addDataSet(scatterDataSet);
            } else {
                LineDataSet lineDataSet = new LineDataSet(entries[i], dataSetNames[i]);
                styleLineDataSet(lineDataSet, colors[i]);
                lineData.addDataSet(lineDataSet);
            }
        }

        // Add our constant lines
        for (LineDataSet lineDataSet : constantLineDataSets) {
            styleLineDataSet(lineDataSet, lineDataSet.getColor());
            lineData.addDataSet(lineDataSet);
        }

        combinedData.setData(scatterData);
        combinedData.setData(lineData);

        chart.setData(combinedData);
        chart.invalidate();

        if (entries[0].size() > 0) {
            forceAxisMinMAx(entries[0].get(0).getX(),
                    entries[0].get(entries[0].size() - 1).getX());
        }

    }
}
