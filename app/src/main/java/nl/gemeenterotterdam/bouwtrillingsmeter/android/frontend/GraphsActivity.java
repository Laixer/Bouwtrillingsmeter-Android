package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataHandler;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataInterval;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataIntervalClosedListener;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3DTime;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPointAccelerometerCreatedListener;

import java.util.Date;

/**
 * This activity shows all the graphs.
 */
public class GraphsActivity extends AppCompatActivity implements DataIntervalClosedListener, DataPointAccelerometerCreatedListener {

    ViewPager viewPager;
    private Graph[] graphs;
    private GraphsSlideAdapter graphSlideAdapter;
    private DataInterval previousDataInterval;

    private LinearLayout dotsLayout;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        // Add this as a listener for interval close events and datapoint events
        DataHandler.addDataIntervalClosedListener(this);
        DataHandler.addDataPointAccelerometerCreatedListeners(this);

        // Create all graphs
        createAllGraphs();

        // Viewpager for the tutorial
        // Also link the adapter
        viewPager = findViewById(R.id.viewPagerGraphs);
        graphSlideAdapter = new GraphsSlideAdapter(this, graphs);
        viewPager.setAdapter(graphSlideAdapter);

        // Viewpager dots
        dotsLayout = (LinearLayout) findViewById(R.id.linearLayoutGraphsDots);
        setIndicatorDots(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                setIndicatorDots(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
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
     * Creates all the graphs.
     */
    private void createAllGraphs() {
        int graphCount = Utility.Resources.getInteger(R.integer.graphs_count);
        graphs = new Graph[graphCount];
        for (int i = 0; i < graphCount; i++) {
            String title = getResources().getStringArray(R.array.graph_title)[i];
            String axisHorizontal = getResources().getStringArray(R.array.graph_axis_horizontal)[i];
            String axisVertical = getResources().getStringArray(R.array.graph_axis_vertical)[i];
            for (int j = 0; j < graphCount; j++) {
                int resourceValue = Utility.Resources.getIntArray(R.array.graphs_0_time_1_frequency)[i];
                if (resourceValue == 0) {
                    graphs[i] = new GraphTime(title, axisHorizontal, axisVertical);
                } else if (resourceValue == 1) {
                    // TODO Frequency
                    graphs[i] = new GraphTime(title, axisHorizontal, axisVertical);
                } else {
                    throw new UnsupportedOperationException("The value in our resources indicating wether we are dealing with a frequency or time graph can only be 1 or 0!");
                }
            }
        }
    }

    /**
     * This updates all graphs and thus is quite a big function.
     * This gets called when the {@link DataInterval} is closed.
     * Do not forget to add the @Override tag to this function!
     * TODO Implement this
     *
     * @param dataInterval
     */
    @Override
    public void onDataIntervalClosed(DataInterval dataInterval) {
        // Only trigger if this is a new data interval
        if (dataInterval == previousDataInterval) {
            return;
        }

        Graph graph;

        /**
         * Graph 1: Velocity // time
         */
        graph = graphs[1];
        ArrayList<DataPoint3DTime> dataPoints3D = dataInterval.dataPoints3DAcceleration;
        DataPoint[] dataPoints1D;
        for (int i = 0; i < 3; i++) {
            dataPoints1D = new DataPoint[dataPoints3D.size()];
            for (int j = 0; j < dataPoints3D.size(); j++) {
                dataPoints1D[j] = (new DataPoint(dataPoints3D.get(j).time, dataPoints3D.get(j).values[i]));
            }
            graph.addDataToSeries1D(dataPoints1D, i);
        }
    }

    /**
     * This processes realtime accelerometer data.
     * TODO Implement some safes.
     *
     * @param dataPoint3DTime
     */
    @Override
    public void onDataPointAccelerometerCreated(DataPoint3DTime dataPoint3DTime) {
        Graph graph = graphs[0];
        if (graph != null && graph.canDataBeAdded()) {
            graph = graphs[0];
            for (int i = 0; i < 3; i++) {
                DataPoint[] singleDataPoint1D = new DataPoint[1];
                singleDataPoint1D[0] = new DataPoint(dataPoint3DTime.time, dataPoint3DTime.values[i]);
                graph.addDataToSeries1D(singleDataPoint1D, i);
            }
        }
    }
}
