package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

/**
 * Instance of a line graph.
 */
class MpaGraphLine extends MpaGraph {

    private ArrayList<Entry>[] entries;
    private LineDataSet[] lineDataSets;
    private LineData lineData;

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
     * @param colors       The color integer for each
     *                     data set
     */
    MpaGraphLine(String title, String xAxisLabel, String yAxisLabel, boolean scrolling, String[] dataSetNames, int[] colors) {
        super(title, xAxisLabel, yAxisLabel, scrolling, dataSetNames, colors);

        // Prepare entries
        entries = new ArrayList[dataSetNames.length];
        for (int i = 0; i < dataSetNames.length; i++) {
            entries[i] = new ArrayList<>();
        }

        // Prepare line data sets
        resetLineDataSets();

        // Prepare graph data
        lineData = new LineData(lineDataSets);
    }

    private void resetLineDataSets() {
        lineDataSets = new LineDataSet[dataSetNames.length];
        for (int i = 0; i < lineDataSets.length; i++) {
            lineDataSets[i] = new LineDataSet(entries[i], dataSetNames[i]);
            lineDataSets[i].setColor(colors[i]);
            lineDataSets[i].setDrawCircles(false);
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
        chart.setData(lineData);
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
            for (int dimension = 0; dimension < dataSetNames.length; dimension++) {

                Entry entry = new Entry(
                        dataPoint3D.xAxisValueAsFloat() / 1000,
                        dataPoint3D.values[dimension]);

                // Failsafe for the ordering
                /*if (entries[dimension].size() > 0) {
                    float previousX = entries[dimension]
                            .get(entries[dimension].size() - 1).getX();
                    if (entry.getX() < previousX) {
                        continue;
                    }
                }*/

                entries[dimension].add(entry);
                //lineDataSets[dimension].addEntry(entry);
            }
        }
    }

    /**
     * This appends our entry to the graph. This is done differently
     * for bar and line graphs.
     */
    @Override
    protected void pushToChart() {
        //sortEntries();
        //resetLineDataSets();
        //chart.setData(lineData);
        //chart.invalidate();
    }

    private void sortEntries() {
        for (ArrayList<Entry> list : entries) {
            Collections.sort(list, new EntryXComparator());
        }
    }
}
