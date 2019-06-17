package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

/**
 * Instance of a line graph.
 */
class MpaGraphLine extends MpaGraph {

    private ArrayList<Entry>[] entries;

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
    MpaGraphLine(String title, String xAxisLabel, String yAxisLabel, boolean scrolling, String[] dataSetNames) {
        super(title, xAxisLabel, yAxisLabel, scrolling, dataSetNames);

        entries = new ArrayList[dataSetNames.length];
        for (int i = 0; i < dataSetNames.length; i++) {
            entries[i] = new ArrayList<>();
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
        return chart;
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
        for (DataPoint3D<T> dataPoint3D : dataPoints3D) {
            for (int dimension = 0; dimension < dataSetNames.length; dimension++) {
                entries[dimension].add(new Entry(
                        dataPoint3D.xAxisValueAsFloat() / 1000,
                        dataPoint3D.values[dimension]));
            }
        }
    }

    /**
     * This appends our entry to the graph. This is done differently
     * for bar and line graphs.
     */
    @Override
    protected void pushToChart() {
        LineDataSet[] lineDataSets = new LineDataSet[entries.length];
        LineData lineData = new LineData();
        for (int i = 0; i < lineDataSets.length; i++) {
            lineDataSets[i] = new LineDataSet(entries[i], dataSetNames[i]);
            lineData.addDataSet(lineDataSets[i]);
        }

        chart.setData(lineData);
        chart.invalidate();
    }
}
