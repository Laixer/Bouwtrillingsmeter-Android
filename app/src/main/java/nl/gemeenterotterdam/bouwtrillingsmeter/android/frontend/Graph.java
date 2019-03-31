package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.graphics.Color;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Random;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataInterval;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataIntervalClosedListener;

/**
 * TODO Doc
 */
public abstract class Graph {

    private String name;
    private String textAxisHorizontal;
    private String textAxisVertical;
    protected GraphView graphView;

    /**
     * This has to be an ArrayList, since our LineGraphSeries is generic.
     * Java does not support arrays of generic types.
     */
    protected ArrayList<LineGraphSeries<DataPoint>> series;

    /**
     * Constructor
     *
     * @param name               Our graph name
     * @param textAxisHorizontal Our horizontal label text
     * @param textAxisVertical   Our vertical lable text
     */
    public Graph(String name, String textAxisHorizontal, String textAxisVertical) {
        this.name = name;
        this.textAxisHorizontal = textAxisHorizontal;
        this.textAxisVertical = textAxisVertical;

        series = new ArrayList<LineGraphSeries<DataPoint>>();
        for (int i = 0; i < 3; i++) {
            series.add(new LineGraphSeries<DataPoint>());
            series.get(i).setTitle(Utility.Resources.getStringArray(R.array.graph_legend_xyz_names)[i]);
        }
    }

    /**
     * This links our graphView view object to this instance.
     * This also sets a bunch of graph layout properties.
     *
     * @param graphView The graphview in our view.
     */
    protected void onCreatedGraphView(GraphView graphView, boolean addSeriesXYZ) {
        // Link graphview and series
        this.graphView = graphView;

        // Link series
        if (addSeriesXYZ) {
            addAndStyleSeries(series.get(0), R.color.graph_series_color_x);
            addAndStyleSeries(series.get(1), R.color.graph_series_color_y);
            addAndStyleSeries(series.get(2), R.color.graph_series_color_z);
        }

        // Scaling
        Viewport viewport = graphView.getViewport();
        viewport.setScalable(true);
        viewport.setScalableY(true);
        viewport.setScrollable(true);
        viewport.setScrollableY(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setYAxisBoundsManual(true);

        // Text and names
        // graphView.setTitle(graph.getName()); This is done with a separate label because it looked ugly

        GridLabelRenderer gridLabelRenderer = graphView.getGridLabelRenderer();
        graphView.setPadding(0, 0, 0, 0);
        gridLabelRenderer.setHorizontalAxisTitle(getTextAxisHorizontal());
        gridLabelRenderer.setVerticalAxisTitle(getTextAxisVertical());
        gridLabelRenderer.setPadding(40);
        gridLabelRenderer.setHighlightZeroLines(true);

        // Legend renderer
        LegendRenderer legendRenderer = graphView.getLegendRenderer();
        legendRenderer.setVisible(true);
        legendRenderer.setAlign(LegendRenderer.LegendAlign.TOP);
    }

    /**
     * Call this to style the series
     * TODO Ontbeun graphview not null
     *
     * @param series
     */
    protected void addAndStyleSeries(LineGraphSeries series, int colorResourceAsInteger) {
        if (graphView != null) {
            // Add to the graphview, or we won't see anything
            graphView.addSeries(series);

            // Series and line styling
            series.setThickness(4);
            series.setColor(Utility.ApplicationContext.getResources().getColor(colorResourceAsInteger));
        }
    }

    /**
     * Set our horizontal axis range.
     * Only does so if we are not scaling the graph manually at that moment.
     * Swaps if from > to.
     *
     * @param from
     * @param to
     */
    protected void setHorizontalRange(double from, double to) {
        if (from > to) {
            double temp = from;
            from = to;
            to = temp;
        }

        if (graphView != null) {
            graphView.getViewport().setMinX(from);
            graphView.getViewport().setMaxX(to);
        }
    }

    /**
     * Set our horizontal axis range.
     * Only does so if we are not scaling the graph manually at that moment.
     * Swaps if from > to.
     *
     * @param from
     * @param to
     */
    protected void setVerticalRange(double from, double to) {
        if (from > to) {
            double temp = from;
            from = to;
            to = temp;
        }

        if (graphView != null) {
            graphView.getViewport().setMinY(from);
            graphView.getViewport().setMaxY(to);
        }
    }

    /**
     * This method is called when we want to append new datapoints to our graphs.
     * Override this method.
     *
     * @param dataPoints The datapoints
     */
    abstract void sendNewDataToSeries(ArrayList<ArrayList<DataPoint>> dataPoints);

    /**
     * Getters.
     */

    /** */
    public String getName() {
        if (name == null) {
            name = "default name";
        }
        return name;
    }

    public String getTextAxisHorizontal() {
        if (textAxisHorizontal == null) {
            textAxisHorizontal = "Default horizontal axis";
        }
        return textAxisHorizontal;
    }

    public String getTextAxisVertical() {
        if (textAxisVertical == null) {
            textAxisVertical = "Default vertical axis";
        }
        return textAxisVertical;
    }

}
