package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;

import java.util.ArrayList;
import java.util.List;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

/**
 * Base for a graph.
 */
class GraphFullyFunctional extends GraphFullyFunctionalBase {

    // Styling
    private CombinedChart chart;
    private final String title;
    private final String xAxisLabel;
    private final String yAxisLabel;

    // Scrolling properties
    private final boolean scrolling;
    private final float xMultiplier;
    private float minimumWidth;
    private float maximumWidth;
    private float xMin;
    private float xMax;

    // Entries
    private List<List<Entry>> entriesLine;
    private List<List<Entry>> entriesLineConstant;
    private List<List<Entry>> entriesScatter;
    private List<List<BarEntry>> entriesBar;

    // DataSets
    private List<LineDataSet> dataSetsLine;
    private List<LineDataSet> dataSetsLineConstant;
    private List<ScatterDataSet> dataSetsScatter;
    private List<BarDataSet> dataSetsBar;

    // Colors
    private List<Integer> colorsLine;
    private List<Integer> colorsLineConstant;
    private List<Integer> colorsScatter;
    private List<Integer> colorsBar;

    /**
     * Constructor, call by calling super().
     *
     * @param title       The graph title
     * @param xAxisLabel  The x axis label
     * @param yAxisLabel  The y axis label
     * @param scrolling   If set to true we append data to the right,
     *                    if set to false we refresh the graph each
     *                    iteration
     * @param xMultiplier The multiplier for the x values
     */
    GraphFullyFunctional(String title, String xAxisLabel, String yAxisLabel,
                                   boolean scrolling, float xMultiplier) {

        // Assign all variables
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.scrolling = scrolling;
        this.xMultiplier = xMultiplier;

        // Initialize all our array lists
        initializeEntryLists(true);

        // Data sets
        dataSetsLine = new ArrayList<>();
        dataSetsLineConstant = new ArrayList<>();
        dataSetsBar = new ArrayList<>();
        dataSetsScatter = new ArrayList<>();

        // Colors
        colorsLine = new ArrayList<>();
        colorsLineConstant = new ArrayList<>();
        colorsScatter = new ArrayList<>();
        colorsBar = new ArrayList<>();
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
     * This creates a chart that fits to the type.
     * Manually add this to a container.
     *
     * @param context The context from which we call this
     * @return The created chart
     */
    CombinedChart createChart(Context context) {
        chart = new CombinedChart(context);
        chart.setData(new CombinedData());
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.LINE,
                CombinedChart.DrawOrder.SCATTER,
                CombinedChart.DrawOrder.BAR
        });
        chart.invalidate();
        styleChart();

        chart.getAxisRight().setEnabled(false);
        return chart;
    }

    /**
     * Call this when the text view for our title is created.
     *
     * @param textViewTitle The text view
     */
    void onTextViewsCreated(TextView textViewTitle,
                            TextView textViewAxisX,
                            TextView textViewAxisY) {
        textViewTitle.setText(title);
        textViewAxisX.setText(xAxisLabel);
        textViewAxisY.setText(yAxisLabel);
    }

    /**
     * This clears all non-constant data sets.
     */
    private void resetAllChartData() {

        ArrayList<IBarLineScatterCandleBubbleDataSet> dataSets = new ArrayList<>();
        dataSets.addAll(dataSetsLine);
        dataSets.addAll(dataSetsBar);
        dataSets.addAll(dataSetsScatter);

        for (IBarLineScatterCandleBubbleDataSet dataSet : dataSets) {
            dataSet.clear();
        }

        initializeEntryLists(false);

    }

    /**
     * This clears all entry array lists by initializing
     * them.
     *
     * @param clearConstant Set to true if this should also
     *                      initialize constant entry lists
     */
    private void initializeEntryLists(boolean clearConstant) {
        entriesLine = new ArrayList<>();
        entriesBar = new ArrayList<>();
        entriesScatter = new ArrayList<>();

        if (clearConstant) {
            entriesLineConstant = new ArrayList<>();
        }
    }

    void createLines(String[] names, int[] colors) {
        for (int i = 0; i < names.length; i++) {
            dataSetsLine.add(new LineDataSet(
                    new ArrayList<>(), names[i]));
            colorsLine.add(colors[i]);
            entriesLine.add(new ArrayList<>());
        }
    }

    void createLinesConstant(String[] names, int[] colors) {
        for (int i = 0; i < names.length; i++) {
            dataSetsLineConstant.add(new LineDataSet(
                    new ArrayList<>(), names[i]));
            colorsLineConstant.add(colors[i]);
            entriesLineConstant.add(new ArrayList<>());
        }
    }

    void createScatters(String[] names, int[] colors) {
        for (int i = 0; i < names.length; i++) {
            dataSetsScatter.add(new ScatterDataSet(
                    new ArrayList<>(), names[i]));
            colorsScatter.add(colors[i]);
            entriesScatter.add(new ArrayList<>());
        }
    }

    void createBars(String[] names, int[] colors) {
        for (int i = 0; i < names.length; i++) {
            dataSetsBar.add(new BarDataSet(
                    new ArrayList<>(), names[i]));
            colorsBar.add(colors[i]);
            entriesBar.add(new ArrayList<>());
        }
    }

    /**
     * This adds a constant line to our graph.
     *
     * @param entries The entries
     * @param name    The name
     * @param color   The color as resource int
     */
    void addConstantLine(ArrayList<Entry> entries, String name, int color) {
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        colorsLineConstant.add(color);
        styleLineDataSet(lineDataSet, color);
        dataSetsLineConstant.add(lineDataSet);
    }

    /**
     * This should be called before we append new data.
     */
    void beforeAppendingData() {
        if (!scrolling) {
            resetAllChartData();
        }
    }

    <T> void appendDataLines(ArrayList<DataPoint3D<T>> dataPoints3D) {
        for (DataPoint3D<T> dataPoint3D : dataPoints3D) {
            for (int i = 0; i < entriesLine.size(); i++) {
                Entry entry = new Entry(
                        dataPoint3D.xAxisValueAsFloat() * xMultiplier,
                        dataPoint3D.values[i]);
                entriesLine.get(i).add(entry);
            }
        }
    }

    <T> void appendDataLinesConstant(ArrayList<DataPoint3D<T>> dataPoints3D) {
        for (DataPoint3D<T> dataPoint3D : dataPoints3D) {
            for (int i = 0; i < entriesLineConstant.size(); i++) {
                Entry entry = new Entry(
                        dataPoint3D.xAxisValueAsFloat() * xMultiplier,
                        dataPoint3D.values[i]);
                entriesLineConstant.get(i).add(entry);
            }
        }
    }

    <T> void appendDataBar(ArrayList<DataPoint3D<T>> dataPoints3D) {
        for (DataPoint3D<T> dataPoint3D : dataPoints3D) {
            for (int i = 0; i < entriesBar.size(); i++) {
                BarEntry barEntry = new BarEntry(
                        dataPoint3D.xAxisValueAsFloat() * xMultiplier,
                        dataPoint3D.values[i]);
                entriesBar.get(i).add(barEntry);
            }
        }
    }

    <T> void appendDataScatter(ArrayList<DataPoint3D<T>> dataPoints3D) {
        for (DataPoint3D<T> dataPoint3D : dataPoints3D) {
            for (int i = 0; i < entriesScatter.size(); i++) {
                Entry entry = new Entry(
                        dataPoint3D.xAxisValueAsFloat() * xMultiplier,
                        dataPoint3D.values[i]);
                entriesScatter.get(i).add(entry);
            }
        }
    }

    /**
     * This should be called after we append new data.
     */
    void afterAppendingData() {
        if (shouldRender()) {
            pushAllToChart();
        }
    }

    /**
     * This pushes all our data sets to the chart.
     */
    private void pushAllToChart() {
        CombinedData combinedData = new CombinedData();
        LineData lineData = new LineData();
        ScatterData scatterData = new ScatterData();
        BarData barData = new BarData();

        // Add variable data sets
        for (int i = 0; i < dataSetsLine.size(); i++) {
            dataSetsLine.set(i, new LineDataSet(
                    entriesLine.get(i), dataSetsLine.get(i).getLabel()));
            styleLineDataSet(dataSetsLine.get(i), colorsLine.get(i));
            lineData.addDataSet(dataSetsLine.get(i));
        }

        for (int i = 0; i < dataSetsBar.size(); i++) {
            dataSetsBar.set(i, new BarDataSet(
                    entriesBar.get(i), dataSetsBar.get(i).getLabel()));
            styleBarDataSet(dataSetsBar.get(i), colorsBar.get(i));
            barData.addDataSet(dataSetsBar.get(i));
        }

        for (int i = 0; i < dataSetsScatter.size(); i++) {
            dataSetsScatter.set(i, new ScatterDataSet(
                    entriesScatter.get(i), dataSetsScatter.get(i).getLabel()));
            styleScatterDataSet(dataSetsScatter.get(i), colorsScatter.get(i));
            scatterData.addDataSet(dataSetsScatter.get(i));
        }

        // Add constant data sets
        for (int i = 0; i < dataSetsLineConstant.size(); i++) {
            LineDataSet lineDataSet = dataSetsLineConstant.get(i);
            styleLineDataSet(lineDataSet, colorsLineConstant.get(i));
            lineData.addDataSet(lineDataSet);
        }
    }

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

    /**
     * This triggers the styling of our chart.
     */
    private void styleChart() {
        chart.setDescription(null);

        if (!scrolling) {
            chart.getXAxis().setAxisMinimum(xMin);
            chart.getXAxis().setAxisMaximum(xMax);
        }
    }

    /**
     * This assigns a lowest and highest value
     * to our axis.
     *
     * @param xLowest  The lowest value
     * @param xHighest The highest value
     */
    protected void forceAxisMinMax(float xLowest, float xHighest) {
        if (scrolling) {
            chart.getXAxis().setAxisMinimum(Math.max(xLowest, xHighest - maximumWidth));
            chart.getXAxis().setAxisMaximum(xHighest);
        }
    }

    /**
     * Styles a line data set.
     *
     * @param lineDataSet The line data set
     * @param color       The color as hex
     */
    private void styleLineDataSet(LineDataSet lineDataSet, int color) {
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setLineWidth(1);
        lineDataSet.setDrawValues(false);

        lineDataSet.setColor(color);
        lineDataSet.setHighLightColor(color);
    }

    /**
     * Styles a scatter data set.
     *
     * @param scatterDataSet The scatter data set
     * @param color          The color as hex
     */
    private void styleScatterDataSet(ScatterDataSet scatterDataSet, int color) {
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        scatterDataSet.setScatterShapeSize(10);

        scatterDataSet.setColors(color);
        scatterDataSet.setDrawValues(false);
    }

    /**
     * Styles a bar data set.
     *
     * @param barDataSet The bar data set
     * @param color      The color as hex
     */
    private void styleBarDataSet(BarDataSet barDataSet, int color) {
        barDataSet.setColor(color);
        barDataSet.setDrawValues(false);
    }

}
