package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * TODO Doc
 * TODO This duplicated the GraphFrequency to some extend, maybe do some trickery
 */
public class GraphFrequencyDominant extends Graph {

    // This might need to become a PQ
    ArrayList<DataPoint> sortedDataPoints;
    LineGraphSeries<DataPoint> constantLine;
    PointsGraphSeries<DataPoint> pointSeries;

    public GraphFrequencyDominant(String name, String textAxisHorizontal, String textAxisVertical) {
        // Call super
        super(name, textAxisHorizontal, textAxisVertical);

        // Create our constant line
        // TODO Implement, this is hard coded now
        constantLine = new LineGraphSeries<DataPoint>();
        constantLine.appendData(new DataPoint(0, 0), true, 10);
        constantLine.appendData(new DataPoint(0, 7.5), true, 10);
        constantLine.appendData(new DataPoint(50, 7.5), true, 10);
        constantLine.appendData(new DataPoint(150, 17.5), true, 10);

        // Instantiate our sorted data points
        sortedDataPoints = new ArrayList<DataPoint>();
        pointSeries = new PointsGraphSeries<DataPoint>();
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
        super.onCreatedGraphView(graphView, false);

        // Add constant line
        addAndStyleSeries(constantLine, R.color.graph_dominant_constant_line);

        // Fix axis permanently
        int axisMin = Utility.Resources.getInteger(R.integer.graphs_frequency_bound_min);
        int axisMax = Utility.Resources.getInteger(R.integer.graphs_frequency_bound_max);
        setHorizontalRange(axisMin, axisMax);
        graphView.getViewport().setMinY(0);

        // Set some additional grid stuff
        graphView.getGridLabelRenderer().setNumHorizontalLabels(11);
    }

    /**
     * Add a single point to our point series
     *
     * @param dataPoints XYZ Datapoint
     * @param dimension Ignored in this case
     */
    @Override
    public void sendNewDataToSeries(DataPoint[] dataPoints, int dimension) {
        for (DataPoint dataPoint : dataPoints) {
            sortedDataPoints.add(dataPoint);
        }
        refreshPointSeries();
    }


    /**
     * Updates our point graph.
     * The points are also sorted to prevent any x-axis conflicts.
     * Returns if no graphview is present
     */
    private void refreshPointSeries() {
        if (graphView == null) {
            return;
        }

        // Delete current series if its present
        // We overwrite the series in this case
        if (graphView.getSeries().contains(pointSeries)) {
            graphView.removeSeries(pointSeries);
        }

        // Sort our datapoints
        Collections.sort(sortedDataPoints, new Comparator<DataPoint>() {
            @Override
            public int compare(DataPoint o1, DataPoint o2) {
                if (o1.getX() == o2.getX()) return 0;
                return o1.getX() > o2.getX()? 1 : -1;
            }
        });

        // Instantiate and fill our point series
        pointSeries = new PointsGraphSeries<DataPoint>();
        for (DataPoint sortedDataPoint : sortedDataPoints) {
            pointSeries.appendData(sortedDataPoint, true, 1000);
        }

        // Style point graph
        graphView.addSeries(pointSeries);
        pointSeries.setColor(Utility.Resources.getColor(R.color.graph_series_color_point));
        pointSeries.setSize(5);
    }
}
