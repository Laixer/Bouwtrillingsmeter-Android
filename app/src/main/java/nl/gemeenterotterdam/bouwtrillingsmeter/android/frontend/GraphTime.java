package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
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
    private double lowestValue = 0;
    private double highestValue = 0;

    private ArrayList<DataPoint> dataPointsX;
    private ArrayList<DataPoint> dataPointsY;
    private ArrayList<DataPoint> dataPointsZ;

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
        dataPointsX = new ArrayList<DataPoint>();
        dataPointsY = new ArrayList<DataPoint>();
        dataPointsZ = new ArrayList<DataPoint>();
    }

    /**
     * Adds data to the series.
     * This skipps any data points that are overlapping with previous points.
     * TODO This can be done more efficiently? Maybe series has an append range function?
     * TODO Maybe not make this check every iteration for the lowest point
     * TODO Maybe make this not check every iteration for updating our scale
     *
     * @param dataPoints A time based array of datapoints.
     * @param dimension  Indicates x y or z. x=0, y=1, z=2
     * @throws IllegalArgumentException If our dimension is incorrect.
     */
    @Override
    public void sendNewDataToSeries(DataPoint[] dataPoints, int dimension) {
        /**
         * New version
         * Seems to have no lagging issues
         * We only display the latest data
         * Y scaling does not work atm (this can be added in O(n) time)
         */

        if (graphView == null) {
            return;
        }

        LineGraphSeries serie = series.get(dimension);
        if (graphView.getSeries().contains(serie)) {
            graphView.removeSeries(serie);
        }
        series.set(dimension, new LineGraphSeries<DataPoint>(dataPoints));

        for (int dimension = 0; dimension < 3; dimension++) {
            if (getDataPoints(dimension).size() + )
        }

        // Push data to graph in the final dimension
        if (dimension == 2) {
            pushToGraph();
        }
    }

    /**
     * This crops our datapoints list if required
     * Then gets all minima and maxima
     * Then pushes everything to our graphview
     */
    private void pushToGraph() {
        // Find minima and maxima (GraphView does this super inefficient.......)
        double[] horizontalMin = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        double[] horizontalMax = new double[]{Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
        double[] verticalMin = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        double[] verticalMax = new double[]{Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
        for (int dimension = 0; dimension < 3; dimension++) {
            for (DataPoint dataPoint : getDataPoints(dimension)) {
                // Horizontal
                if (dataPoint.getX() < horizontalMin[dimension]) {
                    horizontalMin[dimension] = dataPoint.getX();
                } else if (dataPoint.getX() > horizontalMax[dimension]){
                    horizontalMax[dimension] = dataPoint.getX();
                }

                // Vertical
                if (dataPoint.getY() < verticalMin[dimension]) {
                    verticalMin[dimension] = dataPoint.getY();
                } else if (dataPoint.getY() > verticalMax[dimension]){
                    verticalMax[dimension] = dataPoint.getY();
                }
            }
        }

        addAndStyleSeries(series.get(0), R.color.graph_series_color_x);
        addAndStyleSeries(series.get(1), R.color.graph_series_color_y);
        addAndStyleSeries(series.get(2), R.color.graph_series_color_z);
    }


    /**
     * This is the old variant
     * Here we had serious lagging issues
     */

    /*if (lowestValue == 0) {
        lowestValue = dataPoints[0].getX();
    }

    LineGraphSeries serie = series.get(dimension);
    for (DataPoint dataPoint : dataPoints) {
        if (dataPoint.getX() > lastTimeValue[dimension]) {
            serie.appendData(dataPoint, true, Utility.Resources.getInteger(R.integer.graphs_max_datapoint_count));
            lastTimeValue[dimension] = dataPoint.getX();
        }
    }

    // If we are the last update
    if (dimension == 2) {
        highestValue = dataPoints[dataPoints.length - 1].getX();
        double left = Math.max(lowestValue, highestValue - maxHorizontalRange);
        double right = serie.getHighestValueX();
        setHorizontalRange(left, right);
    }*/

    /**
     * Gets our arraylist of datapoints
     *
     * @param dimension The dimension. X=0, Y=1, Z=2.
     * @return The arraylist.
     */
    private ArrayList<DataPoint> getDataPoints(int dimension) {
        switch (dimension) {
            case 0:
                return dataPointsX;
            case 1:
                return dataPointsY;
            case 2:
                return dataPointsZ;
        }

        throw new IllegalArgumentException("Dimension can only be x=0, y=1, z=2.");
    }

}
