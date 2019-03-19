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
    private GraphView graphView;

    public Graph(String name, String textAxisHorizontal, String textAxisVertical) {
        this.name = name;
        this.textAxisHorizontal = textAxisHorizontal;
        this.textAxisVertical = textAxisVertical;
    }

    /**
     * Checks if we can add data.
     * This is to make sure that we have a viewport to add to
     *
     * @return False if we cant add data.
     */
    public boolean canDataBeAdded() {
        return graphView == null ? false : true;
    }

    /**
     * This links our graphView view object to this instance.
     * This also sets a bunch of graph layout properties.
     *
     * @param graphView The graphview in our view.
     */
    protected void onCreatedGraphView(GraphView graphView) {
        // Link graphview and series
        this.graphView = graphView;

        // Scaling
        Viewport viewport = graphView.getViewport();
        viewport.setScalable(true);
        viewport.setScalableY(true);
        viewport.setScrollable(true);
        viewport.setScrollableY(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(100);

        // Text and names
        // graphView.setTitle(graph.getName()); This is done with a separate label because it looked ugly

        GridLabelRenderer gridLabelRenderer = graphView.getGridLabelRenderer();
        graphView.setPadding(0, 0, 0, 0);
        gridLabelRenderer.setHorizontalAxisTitle(getTextAxisHorizontal());
        gridLabelRenderer.setVerticalAxisTitle(getTextAxisVertical());
        gridLabelRenderer.setPadding(40);

        // Legend renderer
        LegendRenderer legendRenderer = graphView.getLegendRenderer();
        legendRenderer.setVisible(true);
        legendRenderer.setAlign(LegendRenderer.LegendAlign.TOP);
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
        // Return if we are scaling manually
        // TODO Implement

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
     * Call this to style the series
     *
     * @param series
     */
    protected void addAndStyleSeries(LineGraphSeries series, int colorResourceAsInteger) {
        // Add to the graphview, or we won't see anything
        graphView.addSeries(series);

        // Series and line styling
        series.setThickness(4);
        series.setColor(Utility.ApplicationContext.getResources().getColor(colorResourceAsInteger));
    }

    public void addDataToSeries1D(DataPoint[] dataPoints, int dimension) {
        /*  */
    }

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
