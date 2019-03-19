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

    private int maxWidthInMs;

    private LineGraphSeries<DataPoint>[] series;

    public GraphTime(String name, String textAxisHorizontal, String textAxisVertical) {
        // Call super
        super(name, textAxisHorizontal, textAxisVertical);

        // Get constants
        maxWidthInMs = Utility.Resources.getInteger(R.integer.graphs_time_bound_max_width_ms);

        // Initialize variables
        for (int i = 0; i < 2; i++) {
            series[i] = new LineGraphSeries<DataPoint>();
        }
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
        addAndStyleSeries(series[0], R.color.graph_series_color_x);
        addAndStyleSeries(series[1], R.color.graph_series_color_y);
        addAndStyleSeries(series[2], R.color.graph_series_color_z);

        // Do time specific stuff here
    }

    /**
     * Adds data to the series.
     * TODO This can be done more efficiently? Maybe series has an append range function?
     *
     * @param dataPoints3D A time based array of datapoints in 3 dimensions. This array should have length 3 or we crash.
     */
    public void addToSeries(ArrayList<DataPoint>[] dataPoints3D) {
        // Edge cases
        if (dataPoints3D.length != 3) {
            throw new IllegalArgumentException("dataPoints3D must be in X, Y, Z. Dimension count is incorrect!");
        }

        // Append data
        for (int i = 0; i < dataPoints3D.length; i++) {
            ArrayList<DataPoint> dataPoints = dataPoints3D[i];
            for (DataPoint dataPoint : dataPoints) {
                series[i].appendData(dataPoint, true, maxWidthInMs);
            }
        }
    }

}
