package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * TODO Doc
 * TODO Optimize by saving our own data list
 * TODO Optimize by only writing if we are visible
 */
public class GraphTime extends Graph {

    private int maxHorizontalRange = Utility.Resources.getInteger(R.integer.graphs_time_horizontal_axis_range_s);
    private int maxDataPointCount = Utility.Resources.getInteger(R.integer.graphs_max_datapoint_count);
    private double marginMultiplier = Utility.Resources.getInteger(R.integer.graphs_axis_margins_multiplier_percentage) * 0.01;

    private ArrayList<ArrayList<DataPoint>> dataPointsXYZ;

    /**
     * Constructor
     *
     * @param name               The graph name as displayed
     * @param textAxisHorizontal Horizontal axis text
     * @param textAxisVertical   Vertical axis text
     */
    public GraphTime(String name, String textAxisHorizontal, String textAxisVertical) {
        // Call super
        super(name, textAxisHorizontal, textAxisVertical);

        // Initialize variables
        dataPointsXYZ = new ArrayList<ArrayList<DataPoint>>();
        for (int dimension = 0; dimension < 3; dimension++) {
            dataPointsXYZ.add(new ArrayList<DataPoint>());
        }
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
    public void sendNewDataToSeries(ArrayList<ArrayList<DataPoint>> graphPoints) {
        for (int dimension = 0; dimension < 3; dimension++) {
            // Check for overlap
            ArrayList<DataPoint> currentList = dataPointsXYZ.get(dimension);
            while (currentList.size() > 0 && graphPoints.get(dimension).get(0).getX() <= currentList.get(currentList.size() - 1).getX()) {
                System.out.println("Removed overlapping points!");
                currentList.remove(currentList.size() - 1);
            }

            // Remove some datapoints if we have too many
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
     * TODO HorizontalMinMax kan effectiever
     */
    private void pushToGraph() {
        // Find minima and maxima (GraphView does this super inefficient.......)
        double horizontalMin = Double.MAX_VALUE;
        double horizontalMax = Double.MIN_VALUE;
        double verticalMin = Double.MAX_VALUE;
        double verticalMax = Double.MIN_VALUE;
        for (int dimension = 0; dimension < 3; dimension++) {
            for (DataPoint dataPoint : dataPointsXYZ.get(dimension)) {
                // Horizontal
                if (dataPoint.getX() < horizontalMin) {
                    horizontalMin = dataPoint.getX();
                } else if (dataPoint.getX() > horizontalMax) {
                    horizontalMax = dataPoint.getX();
                }

                // Vertical
                if (dataPoint.getY() < verticalMin) {
                    verticalMin = dataPoint.getY();
                } else if (dataPoint.getY() > verticalMax) {
                    verticalMax = dataPoint.getY();
                }
            }
        }


        // Create new series
        for (int dimension = 0; dimension < 3; dimension++) {
            DataPoint[] d = dataPointsXYZ.get(dimension).toArray(new DataPoint[dataPointsXYZ.get(dimension).size()]);
            series.set(dimension, new LineGraphSeries<DataPoint>(d));
        }

        // Remove any existing series and add the new ones
        graphView.removeAllSeries();
        addAndStyleSeries(series.get(0), R.color.graph_series_color_x);
        addAndStyleSeries(series.get(1), R.color.graph_series_color_y);
        addAndStyleSeries(series.get(2), R.color.graph_series_color_z);

        // Set the ranges we calculated with margins
        horizontalMin = Math.max(horizontalMin, horizontalMax - maxHorizontalRange);
        double range = Math.abs(verticalMax - verticalMin);
        double margin = range * marginMultiplier;
        verticalMin -= margin;
        verticalMax += margin;
        setHorizontalRange(horizontalMin, horizontalMax);
        setVerticalRange(verticalMin, verticalMax);
    }
}
