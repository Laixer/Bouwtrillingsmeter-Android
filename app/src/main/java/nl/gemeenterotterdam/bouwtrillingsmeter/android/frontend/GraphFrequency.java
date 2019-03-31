package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

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

    private ArrayList<ArrayList<DataPoint>> dataPointsXYZ;

    public GraphFrequency(String name, String textAxisHorizontal, String textAxisVertical) {
        // Call super
        super(name, textAxisHorizontal, textAxisVertical);

        // Initialize variables
//        lastFrequencyValue = new double[3];

        // Initialize variables
        dataPointsXYZ = new ArrayList<ArrayList<DataPoint>>();
        for (int dimension = 0; dimension < 3; dimension++) {
            dataPointsXYZ.add(new ArrayList<DataPoint>());
        }
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
     * This method sends datapoints3D to our graph.
     * They get split and passed on to {@Link appendDataToList}.
     *
     * @param dataPoints3D The arraylist.
     */
    @Override
    public <Double> void sendNewDataToSeries(ArrayList<DataPoint3D<Double>> dataPoints3D) {
        ArrayList<ArrayList<DataPoint>> graphPoints = new ArrayList<ArrayList<DataPoint>>();
        for (int dimension = 0; dimension < 3; dimension++) {
            graphPoints.add(new ArrayList<DataPoint>());
            for (int j = 0; j < dataPoints3D.size(); j++) {
                Double value = dataPoints3D.get(j).xAxisValue;
                double x = (double) value;
                graphPoints.get(dimension).add(new DataPoint(x, dataPoints3D.get(j).values[dimension]));
            }
        }
        appendDataToList(graphPoints);
    }

    /**
     * This method is called when we want to append new datapoints to our graphs.
     * Override this method.
     *
     * @param dataPoints The datapoints
     */
    @Override
    protected void appendDataToList(ArrayList<ArrayList<DataPoint>> dataPoints) {
        // Append our data
        for (int dimension = 0; dimension < 3; dimension++) {
            dataPointsXYZ.set(dimension, dataPoints.get(dimension));

        }

        pushToGraph();
    }

    /**
     * Pushes everything
     * TODO Lots of duplicate trough graphs
     */
    protected void pushToGraph() {
        // Edge case
        if (graphView == null) {
            return;
        }

        // Clear
        graphView.removeAllSeries();

        // Iterate trough all dimensions
        double verticalMax = Double.MIN_VALUE;
        for (int dimension = 0; dimension < 3; dimension++) {
            for (DataPoint dataPoint : dataPointsXYZ.get(dimension)) {
                // Vertical max only
                if (dataPoint.getY() > verticalMax) {
                    verticalMax = dataPoint.getY();
                }
            }

            // Create new series
            DataPoint[] d = new DataPoint[dataPointsXYZ.get(dimension).size()];
            d = dataPointsXYZ.get(dimension).toArray(d);
            series.set(dimension, new LineGraphSeries<DataPoint>(d));
            addAndStyleSeries(series.get(dimension), Utility.getColorResourceFromDimension(dimension));
        }

        // Set the range we calculated
        setVerticalRange(0, verticalMax);
    }
}
