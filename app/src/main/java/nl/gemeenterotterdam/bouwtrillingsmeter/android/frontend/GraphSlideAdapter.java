package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.LineGraphSeries;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

public class GraphSlideAdapter extends PagerAdapter {


    private Graph[] graphs;
    private LayoutInflater layoutInflater;
    private Context context;

    /**
     * Constructor for this adapter
     *
     * @param context The context
     */
    public GraphSlideAdapter(Context context, Graph[] graphs) {
        this.context = context;
        this.graphs = graphs;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return graphs.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.graph_template, container, false);

        // Get elements
        TextView textViewName = (TextView) view.findViewById(R.id.textViewGraphTemplateName);
        GraphView graphView = (GraphView) view.findViewById(R.id.graphViewGraphTemplate);

        // Assign variables
        Graph graph = graphs[position];
        textViewName.setText(graph.getName());
        setAllGraphViewProperties(graphView, graph);

        // Add and return
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    /**
     * This sets a bunch of properties on our graph view.
     * This is placed in a function to clean up the {@link #instantiateItem(ViewGroup, int)} function.
     *
     * @param graphView
     */
    private void setAllGraphViewProperties(GraphView graphView, Graph graph) {
        // Series and line styling
        LineGraphSeries series = graph.getAsSeries();
        series.setThickness(4);
        series.setColor(Utility.ApplicationContext.getResources().getColor(R.color.colorPrimary));
        graphView.addSeries(series);

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
        gridLabelRenderer.setHorizontalAxisTitle(graph.getTextAxisHorizontal());
        gridLabelRenderer.setVerticalAxisTitle(graph.getTextAxisVertical());
        gridLabelRenderer.setPadding(40);
    }
}
