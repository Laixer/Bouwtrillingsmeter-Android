package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.view.MotionEvent;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;

/**
 * This class holds an abstract for a graph.
 * It is used to avoid loads of duplicate code.
 * TODO This only updates graphs upon receiving new data
 * TODO Optimize by only writing if we are visible
 * TODO Currently we first create the graph, then the graphview. Might want to switch this!
 */
public abstract class Graph {

    /**
     * If this is set to true we can drag to zoom in our graph.
     */
    private static final boolean USE_ZOOM = false;

    private String name;
    private String textAxisHorizontal;
    private String textAxisVertical;
    protected GraphView graphView;

    private boolean scaleOnHold = false;
    private long previousTouch = 0;

    ArrayList<ArrayList<DataPoint>> dataPointsXYZ;
    ArrayList<LineGraphSeries<DataPoint>> series;

    /**
     * Constructor.
     *
     * @param name               Our graph name
     * @param textAxisHorizontal Our horizontal label text
     * @param textAxisVertical   Our vertical lable text
     */
    public Graph(String name, String textAxisHorizontal, String textAxisVertical) {
        this.name = name;
        this.textAxisHorizontal = textAxisHorizontal;
        this.textAxisVertical = textAxisVertical;

        series = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            series.add(new LineGraphSeries<>());
            series.get(i).setTitle(Utility.resources.getStringArray(R.array.graph_legend_xyz_names)[i]);
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

        // Labels
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

        // Check if zoom is enabled
        if (USE_ZOOM) {
            setupTouchListener();
        }
    }

    /**
     * Implements the dragging gestures.
     * Set {@link #USE_ZOOM} to true to use this.
     */
    private void setupTouchListener() {
        if (!USE_ZOOM) {
            return;
        }

        graphView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                // Drag check
                if (event.getPointerCount() > 1) {
                    scaleOnHold = true;
                }

                // Double tap check
                long currentTouch = Calendar.getInstance().getTimeInMillis();
                if (previousTouch > 0) {
                    long dt = currentTouch - previousTouch;
                    int doubleTouchMin = Utility.resources.getInteger(R.integer.double_touch_ms_min);
                    int doubleTouchMax = Utility.resources.getInteger(R.integer.double_touch_ms_max);
                    if (dt > doubleTouchMin && dt < doubleTouchMax) {
                        scaleOnHold = !scaleOnHold;
                    }
                }
                previousTouch = currentTouch;
                return false;
            }
        });
    }

    /**
     * This forces our scale to un-hold.
     */
    void forceScaleOffHold() {
        scaleOnHold = false;
    }

    /**
     * Call this to style the series
     *
     * @param series The series
     */
    void addAndStyleSeries(LineGraphSeries series, int colorResourceAsInteger) {
        if (graphView != null) {
            // Add to the graphview, or we won't see anything
            graphView.addSeries(series);

            // Series and line styling
            series.setThickness(4);
            series.setColor(Utility.applicationContext.getResources().getColor(colorResourceAsInteger));
        } else {
            System.out.println("Graph.addAndStyleSeries() graphview = null");
        }
    }

    /**
     * Set our horizontal axis range.
     * Only does so if we are not scaling the graph manually at that moment.
     * Swaps if from > to.
     *
     * @param from Range from
     * @param to   Range to
     */
    void setHorizontalRange(double from, double to) {
        if (scaleOnHold) {
            return;
        }

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
     * This also adds margin
     *
     * @param from Range from
     * @param to   Range to
     */
    void setVerticalRange(double from, double to, boolean addMarginLower, boolean addMarginHigher) {
        if (scaleOnHold) {
            return;
        }

        if (from > to) {
            double temp = from;
            from = to;
            to = temp;
        }

        if (addMarginLower || addMarginHigher) {
            double range = Math.abs(from - to);
            double margin = range * (0.01 * Utility.resources.getInteger(R.integer.graphs_axis_margins_multiplier_percentage));
            if (addMarginLower) {
                from -= margin;
            }

            if (addMarginHigher) {
                to += margin;
            }
        }

        if (graphView != null) {
            graphView.getViewport().setMinY(from);
            graphView.getViewport().setMaxY(to);
        }
    }

    /**
     * This method sends datapoints3D to our graph.
     * They get split and passed on to {@link #appendDataToList(ArrayList)}.
     *
     * @param dataPoints3D The arraylist.
     */
    public abstract <T> void sendNewDataToSeries(ArrayList<DataPoint3D<T>> dataPoints3D);

    /**
     * This method is called when we want to append new datapoints to our graphs.
     * Override this method.
     *
     * @param dataPoints The datapoints
     */
    protected abstract void appendDataToList(ArrayList<ArrayList<DataPoint>> dataPoints);

    /**
     * This pushes our datapoints onto the graph.
     * Override this method.
     */
    protected abstract void pushToGraph();

    String getName() {
        if (name == null) {
            name = "default name";
        }
        return name;
    }

    private String getTextAxisHorizontal() {
        if (textAxisHorizontal == null) {
            textAxisHorizontal = "Default horizontal axis";
        }
        return textAxisHorizontal;
    }

    private String getTextAxisVertical() {
        if (textAxisVertical == null) {
            textAxisVertical = "Default vertical axis";
        }
        return textAxisVertical;
    }

}
