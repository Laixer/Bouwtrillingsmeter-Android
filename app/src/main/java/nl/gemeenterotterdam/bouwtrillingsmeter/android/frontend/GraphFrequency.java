package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * TODO Doc
 */
public class GraphFrequency extends Graph {

    /**
     * This holds our previous time value, to prevent points from overlapping.
     * TODO This is a lot of extra comparison. This might be solvable in a more elegant way.
     * TODO Do we need this in the frequency graph?
     */
    private double[] lastFrequencyValue;
    private int axisMin = Utility.Resources.getInteger(R.integer.graphs_frequency_bound_min);
    private int axisMax = Utility.Resources.getInteger(R.integer.graphs_frequency_bound_max);

    public GraphFrequency(String name, String textAxisHorizontal, String textAxisVertical) {
        // Call super
        super(name, textAxisHorizontal, textAxisVertical);

        // Initialize variables
        lastFrequencyValue = new double[3];
    }

    /**
     * This links our graphView view object to this instance.
     * This also sets a bunch of graph layout properties.
     *
     * @param graphView The graphview in our view.
     */
    @Override
    public void onCreatedGraphView(GraphView graphView, boolean addSeriesXYZ) {
        // Call super
        super.onCreatedGraphView(graphView, true);

        // Fix axis permanently
        setHorizontalRange(axisMin, axisMax);

        // Set some additional grid stuff
        graphView.getGridLabelRenderer().setNumHorizontalLabels(11);
    }

    /**
     * Replace all data in full
     *
     * @param dataPoints The datapoints
     * @param dimension x=0, y=1, z=2
     */
    @Override
    public void sendNewDataToSeries(DataPoint[] dataPoints, int dimension) {
        // Set the frequency values back to 0
        lastFrequencyValue = new double[3];

        // Add all the points
        LineGraphSeries serie = new LineGraphSeries<DataPoint>();
        for (DataPoint dataPoint : dataPoints) {
            if (dataPoint.getX() > lastFrequencyValue[dimension]) {
                serie.appendData(dataPoint, true, Utility.Resources.getInteger(R.integer.graphs_max_datapoint_count));
                lastFrequencyValue[dimension] = dataPoint.getX();
            }
        }

        // Get the color
        // TODO Ontbeun
        int resource = -1;
        switch (dimension) {
            case 0:
                resource = R.color.graph_series_color_x;
                break;
            case 1:
                resource = R.color.graph_series_color_y;
                break;
            case 2:
                resource = R.color.graph_series_color_z;
                break;
        }

        // We overwrite the series in this case
        if (graphView != null && graphView.getSeries().contains(series.get(dimension))) {
            graphView.removeSeries(series.get(dimension));
        }
        series.set(dimension, serie);
        addAndStyleSeries(serie, resource);
    }
}
