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

        // Link view to our graph object
        graphs[position].onCreatedGraphView(graphView);

        // Assign variables
        Graph graph = graphs[position];
        textViewName.setText(graph.getName());

        // Add and return
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
