package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

/**
 * This manages a {@link com.jjoe64.graphview.GraphView} with time on the x-axis and some value on the y-axis.
 * It contains a line graph.
 */
public class GraphTime extends Graph {

    private int maxHorizontalRange = Utility.resources.getInteger(R.integer.graphs_time_line_horizontal_axis_range_max_s);
    private int maxDataPointCount = Utility.resources.getInteger(R.integer.graphs_line_max_datapoint_count);
    private double marginMultiplier = Utility.resources.getInteger(R.integer.graphs_axis_margins_multiplier_percentage) * 0.01;

    /**
     * Constructor
     *
     * @param name               The graph name as displayed
     * @param textAxisHorizontal Horizontal axis text
     * @param textAxisVertical   Vertical axis text
     */
    GraphTime(String name, String textAxisHorizontal, String textAxisVertical) {
        // Call super
        super(name, textAxisHorizontal, textAxisVertical);

        // Initialize variables
        dataPointsXYZ = new ArrayList<ArrayList<DataPoint>>();
        for (int dimension = 0; dimension < 3; dimension++) {
            dataPointsXYZ.add(new ArrayList<DataPoint>());
        }
    }

    /**
     * This method sends datapoints3D to our graph.
     * They get split and passed on to {@link #appendDataToList(ArrayList)}.
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
    protected void pushToGraph() {
        // Edge case
        if (graphView == null) {
            return;
        }

        // Clear
        graphView.removeAllSeries();

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

            // Create new series
            DataPoint[] d = new DataPoint[dataPointsXYZ.get(dimension).size()];
            d = dataPointsXYZ.get(dimension).toArray(d);
            series.set(dimension, new LineGraphSeries<DataPoint>(d));
            addAndStyleSeries(series.get(dimension), Utility.getColorResourceFromDimension(dimension));
        }

        // Set the ranges we calculated with margins
        horizontalMin = Math.max(horizontalMin, horizontalMax - maxHorizontalRange);
        setHorizontalRange(horizontalMin, horizontalMax);
        setVerticalRange(verticalMin, verticalMax, true, true);
    }
}
