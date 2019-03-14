package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataHandler;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataInterval;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataIntervalClosedListener;

/**
 * This activity shows all the graphs.
 */
public class GraphsActivity extends AppCompatActivity implements DataIntervalClosedListener {

    ViewPager viewPager;
    private Graph[] graphs;
    private GraphSlideAdapter graphSlideAdapter;
    private DataInterval previousDataInterval;

    private LinearLayout dotsLayout;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        // Add this as a listener for interval close events
        DataHandler.addDataIntervalClosedListener(this);

        // Create all graphs
        createAllGraphs();

        // Viewpager for the tutorial
        // Also link the adapter
        viewPager = findViewById(R.id.viewPagerGraphs);
        graphSlideAdapter = new GraphSlideAdapter(this, graphs);
        viewPager.setAdapter(graphSlideAdapter);

        // Viewpager dots
        dotsLayout = (LinearLayout) findViewById(R.id.linearLayoutGraphsDots);
        setIndicatorDots(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                setIndicatorDots(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });
    }

    /**
     * Set the indicator dots.
     *
     * @param currentIndex The current index at which we are present
     */
    private void setIndicatorDots(int currentIndex) {
        if (dotsLayout != null) {
            dotsLayout.removeAllViews();
            dots = new ImageView[graphs.length];

            for (int i = 0; i < graphs.length; i++) {
                dots[i] = new ImageView(this);
                if (i == currentIndex) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_dot_active));
                } else {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_dot_inactive));
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 0, 4, 0);
                dotsLayout.addView(dots[i], params);
            }
        }


    }

    /**
     * Creates all the graphs
     */
    private void createAllGraphs() {
        int graphCount = 5;
        graphs = new Graph[graphCount];
        for (int i = 0; i < graphCount; i++) {
            String title = getResources().getStringArray(R.array.graph_title)[i];
            String axisHorizontal = getResources().getStringArray(R.array.graph_axis_horizontal)[i];
            String axisVertical = getResources().getStringArray(R.array.graph_axis_vertical)[i];
            graphs[i] = new Graph(title, axisHorizontal, axisVertical);
        }
    }

    /**
     * This updates all graphs
     * This gets called when the {@link DataInterval} is closed.
     * Do not forget to add the @Override tag to this function!
     *
     * @param dataInterval
     */
    @Override
    public void onDataIntervalClosed(DataInterval dataInterval) {
        // Only trigger if this is a new data interval
        if (dataInterval == previousDataInterval) {
            return;
        }

        // Try it out
        Graph graph = graphs[0];
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        ArrayList<DataPoint> points = new ArrayList<DataPoint>();
        for (nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint dataPoint : dataInterval.dataPoints) {
            points.add(new DataPoint(dataPoint.time, dataPoint.values[0]));
        }

        graph.addToSeries(points);
    }
}
