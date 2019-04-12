package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

/**
 * TODO Doc
 * TODO Optimize by saving our own data list
 * TODO Optimize by only writing if we are visible
 */
public class GraphTimeBar extends Graph {

    private int maxHorizontalRange = Utility.Resources.getInteger(R.integer.graphs_time_bar_horizontal_axis_range_max_s);
    private int minHorizontalRange = Utility.Resources.getInteger(R.integer.graphs_time_bar_horizontal_axis_range_min_s);
    private int maxDataPointCount = Utility.Resources.getInteger(R.integer.graphs_bar_max_datapoint_count);
    private double marginMultiplier = Utility.Resources.getInteger(R.integer.graphs_axis_margins_multiplier_percentage) * 0.01;

    private ArrayList<ArrayList<DataPoint>> dataPointsXYZ;
    private ArrayList<BarGraphSeries<DataPoint>> barSeries;

    /**
     * Constructor
     *
     * @param name               The graph name as displayed
     * @param textAxisHorizontal Horizontal axis text
     * @param textAxisVertical   Vertical axis text
     */
    public GraphTimeBar(String name, String textAxisHorizontal, String textAxisVertical) {
        // Call super
        super(name, textAxisHorizontal, textAxisVertical);

        // Initialize variables
        dataPointsXYZ = new ArrayList<ArrayList<DataPoint>>(3);
        barSeries = new ArrayList<BarGraphSeries<DataPoint>>(3);

        // Fill
        for (int dimension = 0; dimension < 3; dimension++) {
            dataPointsXYZ.add(new ArrayList<DataPoint>());
            barSeries.add(new BarGraphSeries<DataPoint>());
        }
    }

    /**
     * This method sends datapoints3D to our graph.
     * They get split and passed on to {@Link appendDataToList}.
     *
     * @param dataPoints3D The arraylist.
     */
    @Override
    public <Long> void sendNewDataToSeries(ArrayList<DataPoint3D<Long>> dataPoints3D) {
        ArrayList<ArrayList<DataPoint>> graphPoints = new ArrayList<ArrayList<DataPoint>>();
        for (int dimension = 0; dimension < 3; dimension++) {
            graphPoints.add(new ArrayList<DataPoint>());
            for (int j = 0; j < dataPoints3D.size(); j++) {
                Long value = dataPoints3D.get(j).xAxisValue;
                double x = (long) value / 1000.0;
                graphPoints.get(dimension).add(new DataPoint(x, dataPoints3D.get(j).values[dimension]));
            }
        }
        appendDataToList(graphPoints);
    }

    /**
     * Adds data to the series.
     * This skipps any data points that are overlapping with previous points.
     * TODO Dit checkt nu niet voor overlap!
     *
     * @param graphPoints A time based arraylist with datapoints in 3 dimensions
     * @throws IllegalArgumentException If our dimension is incorrect.
     */
    @Override
    protected void appendDataToList(ArrayList<ArrayList<DataPoint>> graphPoints) {
        for (int dimension = 0; dimension < 3; dimension++) {

            // Remove some datapoints if we have too many
            ArrayList<DataPoint> currentList = dataPointsXYZ.get(dimension);
            if (currentList.size() + graphPoints.get(dimension).size() > maxDataPointCount) {
                int removeCount = graphPoints.get(dimension).size() - (maxDataPointCount - currentList.size());
                dataPointsXYZ.set(dimension, new ArrayList<DataPoint>(currentList.subList(removeCount - 1, currentList.size())));
            }

            // Append
            dataPointsXYZ.get(dimension).addAll(graphPoints.get(dimension));
        }

        // Push data to graph in the final dimension
        pushToGraph();
    }

    /**
     * This crops our datapoints list if required
     * Then gets all minima and maxima
     * Then pushes everything to our graphview
     * TODO VerticalMinMax kan effectiever
     */
    protected void pushToGraph() {
        // Edge case
        if (graphView == null) {
            return;
        }

        // Clear
        graphView.removeAllSeries();

        // Get total datapoint count
        int dataPointTotalCount = dataPointsXYZ.get(0).size();

        // Find minima and maxima (GraphView does this super inefficient.......)
        double horizontalMin = dataPointsXYZ.get(0).get(0).getX();
        double horizontalMax = dataPointsXYZ.get(0).get(dataPointTotalCount - 1).getX();
        double verticalMin = 0;
        double verticalMax = Double.MIN_VALUE;
        horizontalMin = Math.max(horizontalMin, horizontalMax - maxHorizontalRange);

        // Apply minimum range
        double range = horizontalMax - horizontalMin;
        if (range < minHorizontalRange) {
            horizontalMax += (minHorizontalRange - range);
        }

        // Get starting index
        // TODO Nu hard coded op 1 seconde interval tijd
        int startIndex = 0;
        if (dataPointsXYZ.get(0).size() > maxHorizontalRange) {
            startIndex = Math.max(0, (int)horizontalMax - maxHorizontalRange);
        }

        // Iterate trough each dimension
        for (int dimension = 0; dimension < 3; dimension++) {
            for (int i = startIndex; i < dataPointTotalCount; i++) {
                DataPoint dataPoint = dataPointsXYZ.get(dimension).get(i);

                // Vertical
                if (dataPoint.getY() > verticalMax) {
                    verticalMax = dataPoint.getY();
                }
            }

            // Get indexes
            int dataPointShowCount = Math.min(dataPointsXYZ.get(0).size(), maxHorizontalRange);
            int indexFrom = Math.max(0, dataPointTotalCount - maxHorizontalRange);

            // Transfer sublist
            DataPoint[] d = new DataPoint[dataPointShowCount];
            d = dataPointsXYZ.get(dimension).subList(indexFrom, dataPointTotalCount).toArray(d);
            barSeries.set(dimension, new BarGraphSeries<DataPoint>(d));

            // Add to the graphview
            if (graphView != null) {
                graphView.addSeries(barSeries.get(dimension));
                int colorResourceAsInteger = Utility.getColorResourceFromDimension(dimension);
                barSeries.get(dimension).setColor(Utility.ApplicationContext.getResources().getColor(colorResourceAsInteger));
            }
        }

        // Set the ranges we calculated with margins
        setHorizontalRange(horizontalMin, horizontalMax);
        setVerticalRange(verticalMin, verticalMax, true, true);
    }
}
