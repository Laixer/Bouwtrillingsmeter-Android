package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.LimitConstants;

/**
 * This manages a {@link GraphView} with frequency on the x-axis and dominant frequency on the y-axis.
 * This contains a line indicating the limit.
 * This contains dots indicating individual exceeding cases.
 */
public class GraphFrequencyDominant extends Graph {

    // This might need to become a PQ
    private ArrayList<DataPoint> sortedDataPoints;
    private LineGraphSeries<DataPoint> constantLine;
    private PointsGraphSeries<DataPoint> pointGraphSeries;

    GraphFrequencyDominant(String name, String textAxisHorizontal, String textAxisVertical) {
        // Call super
        super(name, textAxisHorizontal, textAxisVertical);

        // Create our constant line
        ArrayList<DataPoint> dataPointsList = LimitConstants.getLimitAsDataPoints(Backend.getCurrentMeasurement().getSettings());
        DataPoint[] dataPoints = new DataPoint[dataPointsList.size()];
        for (int i = 0; i < dataPointsList.size(); i++) {
            dataPoints[i] = dataPointsList.get(i);
        }
        constantLine = new LineGraphSeries<DataPoint>(dataPoints);

        // Instantiate our sorted data points
        sortedDataPoints = new ArrayList<DataPoint>();
        pointGraphSeries = new PointsGraphSeries<DataPoint>();
    }

    /**
     * This links our graphView view object to this instance.
     * This also sets a bunch of graph layout properties.
     *
     * @param graphView The graphview in our view.
     */
    @Override
    public void onCreatedGraphView(GraphView graphView, boolean addSeriesXYZ) {
        // Add constant line
        this.graphView = graphView;
        addAndStyleSeries(constantLine, R.color.graph_dominant_constant_line, Utility.resources.getString(R.string.graph_legend_limitline_name));

        // Call super
        super.onCreatedGraphView(graphView, false);

        // Fix axis permanently
        int axisMin = Utility.resources.getInteger(R.integer.graphs_frequency_bound_min);
        int axisMax = Utility.resources.getInteger(R.integer.graphs_frequency_bound_max);
        setHorizontalRange(axisMin, axisMax);
        graphView.getViewport().setMinY(0);

        // Set some additional grid stuff
        graphView.getGridLabelRenderer().setNumHorizontalLabels(11);

        // Push to graph
        pushToGraph();
    }

    /**
     * This method sends datapoints3D to our graph.
     * All non-exceeding x and y values are set to -1
     * They get split and passed on to {@link #appendDataToList(ArrayList)}
     *
     * @param dataPoints3D The arraylist.
     */
    @Override
    public <Double> void sendNewDataToSeries(ArrayList<DataPoint3D<Double>> dataPoints3D) {
        // Edge case
        if (dataPoints3D.size() == 0) {
            return;
        }

        // Iterate trough to get all relevant datapoints
        ArrayList<ArrayList<DataPoint>> graphPoints = new ArrayList<ArrayList<DataPoint>>();
        graphPoints.add(new ArrayList<DataPoint>());

        for (DataPoint3D<Double> dataPoint3D : dataPoints3D) {
            // Only the x value is used as a workaround
            double value = dataPoint3D.values[0];
            if (value > -1) {
                Double xAxisValue = dataPoint3D.xAxisValue;
                double x = (double) xAxisValue;
                graphPoints.get(0).add(new DataPoint(x, value));
            }
        }

        // Only push if we altered
        if (graphPoints.get(0).size() > 0) {
            appendDataToList(graphPoints);
        }
    }

    /**
     * This method is called when we want to append new datapoints to our graphs.
     * Override this method.
     *
     * @param dataPoints The datapoints
     */
    @Override
    protected void appendDataToList(ArrayList<ArrayList<DataPoint>> dataPoints) {
        // Add all data, only x is used to store
        sortedDataPoints.addAll(dataPoints.get(0));

        // Sort our data points
        Collections.sort(sortedDataPoints, new Comparator<DataPoint>() {
            @Override
            public int compare(DataPoint o1, DataPoint o2) {
                if (o1.getX() == o2.getX()) return 0;
                return o1.getX() > o2.getX() ? 1 : -1;
            }
        });

        // Push to graph
        pushToGraph();
    }

    /**
     * This pushes our datapoints onto the graph.
     * Override this method.
     * TODO This does not need to iterate since we never throw away data. Just check minmax on adding.
     */
    @Override
    protected void pushToGraph() {
        // Edge case
        if (graphView == null) {
            return;
        }

        // Only do this if the graphview is visible
        if (!Utility.isVisible(graphView)) {
            return;
        }

        // Clear
        if (graphView.getSeries().contains(pointGraphSeries)) {
            graphView.removeSeries(pointGraphSeries);
        }

        // Iterate trough all datapoints
        double verticalMax = Double.MIN_VALUE;
        for (DataPoint dataPoint : sortedDataPoints) {
            // Vertical max only
            if (dataPoint.getY() > verticalMax) {
                verticalMax = dataPoint.getY();
            }
        }

        // Create new series
        DataPoint[] d = new DataPoint[sortedDataPoints.size()];
        d = sortedDataPoints.toArray(d);
        pointGraphSeries = new PointsGraphSeries<>(d);

        // Set the range we calculated
        verticalMax = Math.max(verticalMax, 20);
        setVerticalRange(0, verticalMax, false, true);

        // Add and style series
        graphView.addSeries(pointGraphSeries);
        pointGraphSeries.setColor(Utility.resources.getColor(R.color.graph_series_color_point));
        pointGraphSeries.setSize(7);
        pointGraphSeries.setTitle(Utility.resources.getString(R.string.graph_legend_exceeding_name));
    }
}