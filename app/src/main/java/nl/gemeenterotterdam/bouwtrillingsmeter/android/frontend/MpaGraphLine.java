package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

/**
 * Instance of a line graph.
 */
class MpaGraphLine extends MpaGraph {

    private ArrayList<Entry>[] entries;
    private LineDataSet[] lineDataSets;
    private LineData lineData;
    private boolean useAsPoints;

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
    MpaGraphLine(String title, String xAxisLabel, String yAxisLabel, boolean scrolling, boolean refreshing, String[] dataSetNames, int[] colors, boolean useAsPoints, float xMultiplier) {
        super(title, xAxisLabel, yAxisLabel, scrolling, refreshing, dataSetNames, colors, xMultiplier);
        this.useAsPoints = useAsPoints;

        // Prepare entries
        entries = new ArrayList[dataSetNames.length];
        for (int i = 0; i < dataSetNames.length; i++) {
            entries[i] = new ArrayList<>();
        }

        lineData = new LineData();
        lineDataSets = new LineDataSet[dataSetNames.length];
        for (int i = 0; i < lineDataSets.length; i++) {
            lineDataSets[i] = new LineDataSet(entries[i], dataSetNames[i]);
            styleLineDataSet(lineDataSets[i], colors[i]);
            lineData.addDataSet(lineDataSets[i]);
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
        chart = new LineChart(context);
        styleChart();

        return chart;
    }

    /**
     * If we are not {@link #scrolling} then all chart data must
     * be reset to refresh the entire graph.
     */
    @Override
    protected void resetChartData() {
        for (LineDataSet lineDataSet : lineDataSets) {
            lineDataSet.clear();
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
            for (int i = 0; i < entries.length; i++) {
                // Create entry
                Entry entry = new Entry(
                        dataPoint3D.xAxisValueAsFloat() * xMultiplier,
                        dataPoint3D.values[i]);

                // Failsafe for the ordering
                // Yes, we actually need this
                if (entries[i].size() > 0) {
                    float previousX = entries[i]
                            .get(entries[i].size() - 1).getX();
                    if (entry.getX() < previousX) {
                        continue;
                    }
                }

                // Add entry to list, we don't add to line data set
                // This might change later because realtime adding doesn't work
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
        lineData = new LineData();
        for (int i = 0; i < entries.length; i++) {
            lineDataSets[i] = new LineDataSet(entries[i], dataSetNames[i]);
            styleLineDataSet(lineDataSets[i], colors[i]);
            lineData.addDataSet(lineDataSets[i]);
        }

        chart.setData(lineData);
        chart.invalidate();

        if (entries[0].size() > 0) {
            forceAxisMinMAx(entries[0].get(0).getX(),
                    entries[0].get(entries[0].size() - 1).getX());
        }
    }
}
