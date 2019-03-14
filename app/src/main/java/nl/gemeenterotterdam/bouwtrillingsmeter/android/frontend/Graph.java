package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Random;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataInterval;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataIntervalClosedListener;

public class Graph {

    private String name;
    private String textAxisHorizontal;
    private String textAxisVertical;
    private LineGraphSeries<DataPoint> series;
    private GraphView graphView;

    public Graph(String name, String textAxisHorizontal, String textAxisVertical) {
        this.name = name;
        this.textAxisHorizontal = textAxisHorizontal;
        this.textAxisVertical = textAxisVertical;

        series = new LineGraphSeries<DataPoint>();
    }

    /**
     * This links our graphView view object to this instance.
     * This also sets a bunch of graph layout properties.
     *
     * @param graphView The graphview in our view.
     */
    public void onCreatedGraphView(GraphView graphView) {
        // Link graphview and series
        this.graphView = graphView;
        graphView.addSeries(series);

        // Series and line styling
        series.setThickness(4);
        series.setColor(Utility.ApplicationContext.getResources().getColor(R.color.colorPrimary));

        // Scaling
        Viewport viewport = graphView.getViewport();
        viewport.setScalable(true);
        viewport.setScalableY(true);
        viewport.setScrollable(true);
        viewport.setScrollableY(true);

        // Text and names
        // graphView.setTitle(graph.getName()); This is done with a separate label because it looked ugly

        GridLabelRenderer gridLabelRenderer = graphView.getGridLabelRenderer();
        graphView.setPadding(0, 0, 0, 0);
        gridLabelRenderer.setHorizontalAxisTitle(getTextAxisHorizontal());
        gridLabelRenderer.setVerticalAxisTitle(getTextAxisVertical());
        gridLabelRenderer.setPadding(40);
    }

    public void addToSeries(ArrayList<DataPoint> dataPoints) {
        for (DataPoint dataPoint : dataPoints) {
            series.appendData(dataPoint, true, 1000);
        }
    }

    /**
     * Getters.
     */

    public LineGraphSeries<DataPoint> getSeries() {
        return series;
    }

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
