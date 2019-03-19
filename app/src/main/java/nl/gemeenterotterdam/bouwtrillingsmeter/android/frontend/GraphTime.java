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

    /**
     * This has to be an ArrayList, since our LineGraphSeries is generic.
     * Java does not support arrays of generic types.
     */
    private ArrayList<LineGraphSeries<DataPoint>> series;

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
        series = new ArrayList<LineGraphSeries<DataPoint>>();
        for (int i = 0; i < 3; i++) {
            series.add(new LineGraphSeries<DataPoint>());
        }
        lastTimeValue = new double[3];
    }

    /**
     * This links our graphView view object to this instance.
     * This also sets a bunch of graph layout properties.
     *
     * @param graphView The graphview in our view.
     */
    @Override
    public void onCreatedGraphView(GraphView graphView) {
        // Call super
        super.onCreatedGraphView(graphView);

        // Style series
        addAndStyleSeries(series.get(0), R.color.graph_series_color_x);
        addAndStyleSeries(series.get(1), R.color.graph_series_color_y);
        addAndStyleSeries(series.get(2), R.color.graph_series_color_z);
    }

    /**
     * Adds data to the series.
     * This skipps any data points that are overlapping with previous points.
     * TODO This can be done more efficiently? Maybe series has an append range function?
     *
     * @param dataPoints A time based array of datapoints.
     * @param dimension  Indicates x y or z. x=0, y=1, z=2
     * @throws IllegalArgumentException If our dimension is incorrect.
     */
    @Override
    public void addDataToSeries1D(DataPoint[] dataPoints, int dimension) {
        if (dimension < 0 || dimension > 2) {
            throw new IllegalArgumentException("The dimension parameter must be x=0, y=1 or z=2!");
        }

        LineGraphSeries serie = series.get(dimension);
        for (DataPoint dataPoint : dataPoints) {
            if (dataPoint.getX() > lastTimeValue[dimension]) {
                serie.appendData(dataPoint, true, maxHorizontalRange);
                lastTimeValue[dimension] = dataPoint.getX();
            }
        }

        // If we are the last update
        if (dimension == 2) {
            setHorizontalRange(Math.max(serie.getLowestValueX(), serie.getHighestValueX() - maxHorizontalRange), serie.getHighestValueX());
        }
    }

}
