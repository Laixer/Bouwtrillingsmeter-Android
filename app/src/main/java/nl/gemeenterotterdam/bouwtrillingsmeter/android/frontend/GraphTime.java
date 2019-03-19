package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * TODO Doc
 */
public class GraphTime extends Graph {

    private int maxHorizontalRange;
    private double lowestValue = 0;
    private double highestValue = 0;

    /**
     * This holds our previous time value, to prevent points from overlapping.
     * TODO This is a lot of extra comparison. This might be solvable in a more elegant way.
     */
    private double[] lastTimeValue;

    public GraphTime(String name, String textAxisHorizontal, String textAxisVertical) {
        // Call super
        super(name, textAxisHorizontal, textAxisVertical);

        // Get constants
        maxHorizontalRange = Utility.Resources.getInteger(R.integer.graphs_time_horizontal_axis_range_ms);

        // Initialize variables
        lastTimeValue = new double[3];
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
        if (lowestValue == 0) {
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
            setHorizontalRange(Math.max(lowestValue, highestValue - maxHorizontalRange), serie.getHighestValueX());
        }
    }

}
