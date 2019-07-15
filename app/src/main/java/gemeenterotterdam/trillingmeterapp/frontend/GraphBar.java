package gemeenterotterdam.trillingmeterapp.frontend;

import android.content.Context;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import gemeenterotterdam.trillingmeterapp.backend.DataPoint3D;

/**
 * Instance of a bar graph.
 */
class GraphBar extends Graph {

    private ArrayList<BarEntry>[] entries;
    private BarDataSet[] barDataSets;

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
     */
    GraphBar(String title, String xAxisLabel, String yAxisLabel, boolean scrolling, boolean refreshing, String[] dataSetNames, int[] colors, float xMultiplier) {
        super(title, xAxisLabel, yAxisLabel, scrolling, refreshing, dataSetNames, colors, xMultiplier);

        entries = new ArrayList[dataSetNames.length];
        for (int i = 0; i < dataSetNames.length; i++) {
            entries[i] = new ArrayList<>();
        }

        barDataSets = new BarDataSet[entries.length];
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
        chart = new BarChart(context);
        styleChart();

        chart.setHorizontalScrollBarEnabled(true);
        ((BarChart) chart).getAxisRight().setEnabled(false);

        return chart;
    }

    /**
     * If we are not {@link #scrolling} then all chart data must
     * be reset to refresh the entire graph.
     */
    @Override
    protected void resetChartData() {
        for (BarDataSet barDataSet : barDataSets) {
            barDataSet.clear();
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
                BarEntry barEntry = new BarEntry(
                        dataPoint3D.xAxisValueAsFloat() * xMultiplier,
                        dataPoint3D.values[i]);
                entries[i].add(barEntry);
            }
        }
    }

    /**
     * This appends our entry to the graph. This is done differently
     * for bar and line graphs.
     */
    @Override
    protected void pushToChart() {
        BarData barData = new BarData();
        for (int i = 0; i < barDataSets.length; i++) {
            barDataSets[i] = new BarDataSet(entries[i], dataSetNames[i]);
            styleBarDataSet(barDataSets[i], colors[i]);
            barData.addDataSet(barDataSets[i]);
        }

        chart.setData(barData);
        ((BarChart) chart).groupBars(0, 0.1f, 0.01f);
        chart.invalidate();

        if (entries[0].size() > 0) {
            forceAxisMinMax(entries[0].get(0).getX(),
                    entries[0].get(entries[0].size() - 1).getX());
        }
    }

}
