package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.jjoe64.graphview.GraphView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

public class MpaGraphsSlideAdapter extends PagerAdapter {

    /**
     * Our graphs.
     */
    private MpaGraph[] graphs;

    /**
     * Our layout inflater.
     */
    private LayoutInflater mLayoutInflater;

    /**
     * The context of this adapter.
     */
    private Context context;

    /**
     * Constructor for this adapter.
     *
     * @param context The context
     * @param graphs  The graphs to display
     */
    MpaGraphsSlideAdapter(Context context, MpaGraph[] graphs) {
        this.graphs = graphs;
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Gets the count of all items.
     * This must be implemented.
     *
     * @return Graphs count
     */
    @Override
    public int getCount() {
        return graphs.length;
    }

    /**
     * Checks if a view belongs to an object.
     * This must be implemented.
     *
     * @param view The view
     * @param o    The object
     * @return True if it is
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    /**
     * This creates an item in our adapter.
     *
     * @param container The container in which we instantiate
     * @param position  The index of this item
     * @return The created view
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.mpa_graph_template,
                container, false);

        // Get graph
        MpaGraph graph = graphs[position];

        // Create our chart and push it to the UI
        Chart chart = graph.createChart(context);
        FrameLayout frameLayout = view.findViewById(
                R.id.frameLayoutMpaGraphTemplate);
        frameLayout.addView(chart);
        graph.onTextViewTitleCreated(view.findViewById(
                R.id.textViewMpaGraphTemplateName));

        // Add, push and return
        container.addView(view);
        graph.pushToChart();
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}