package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

/**
 * Instance of a bar graph.
 */
class MpaGraphBar extends MpaGraph {

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
    public MpaGraphBar(String title, String xAxisLabel, String yAxisLabel, boolean scrolling, ArrayList<String> dataSetNames) {
        super(title, xAxisLabel, yAxisLabel, scrolling, dataSetNames);
    }

    /**
     * This links our chart. Each implementation must
     * convert the chart type.
     *
     * @param chart The chart
     */
    @Override
    void linkChart(Chart chart) {
        this.chart = (BarChart) chart;
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
            for (int dimension = 0; dimension < 3; dimension++) {
                entries[dimension].add(new BarEntry((float) dataPoint3D.xAxisValue, dataPoint3D.values[dimension]));
            }
        }
    }

    /**
     * This appends our entry to the graph. This is done differently
     * for bar and line graphs.
     */
    @Override
    protected void pushToChart() {

    }
}
