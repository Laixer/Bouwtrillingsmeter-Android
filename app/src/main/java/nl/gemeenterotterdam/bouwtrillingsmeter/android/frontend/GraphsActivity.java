package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataHandler;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataInterval;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataIntervalClosedListener;

/**
 * This activity shows all the graphs.
 */
public class GraphsActivity extends AppCompatActivity implements DataIntervalClosedListener {

    /**
     * This is recalled to make sure we don't make multiple activities for this.
     */
    private static GraphsActivity graphsActivity = null;

    /**
     * This is static because we never want to recreate our graphs during a measurement.
     */
    static Graph[] graphs = null;

    private ViewPager viewPager;
    private GraphsSlideAdapter graphSlideAdapter;
    private LinearLayout dotsLayout;

    private DataInterval previousDataInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        // Create all graphs
        // Add this as a listener for interval close events and datapoint events
        if (graphsActivity == null) {
            graphsActivity = this;
            DataHandler.addDataIntervalClosedListener(this);
            createAllGraphs();
        } else {
            for (Graph g : graphs) {
                g.forceScaleOffHold();
                g.pushToGraph();
            }
        }

        // Viewpager dots
        dotsLayout = (LinearLayout) findViewById(R.id.linearLayoutGraphsDots);
        Utility.setIndicatorDots(this, dotsLayout, 0, graphs.length);

        // Setup viewpager
        graphSlideAdapter = new GraphsSlideAdapter(this, graphs);
        viewPager = findViewById(R.id.viewPagerGraphs);
        viewPager.setAdapter(graphSlideAdapter);
        viewPager.setOffscreenPageLimit(10);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Utility.setIndicatorDots(getApplicationContext(), dotsLayout, i, graphs.length);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    /**
     * This creates all our graphs.
     * The graphviews are linked later.
     */
    private void createAllGraphs() {
        if (graphs != null) {
            System.out.println("Creating graphs when graphs is not null.");
            return;
        }

        // Get constants
        String[] title = getResources().getStringArray(R.array.graph_title);
        String[] axisHorizontal = getResources().getStringArray(R.array.graph_axis_horizontal);
        String[] axisVertical = getResources().getStringArray(R.array.graph_axis_vertical);

        // Create graphs
        graphs = new Graph[5];
        graphs[0] = new GraphTimeLine(title[0], axisHorizontal[0], axisVertical[0]);
        graphs[1] = new GraphTimeBar(title[1], axisHorizontal[1], axisVertical[1]);
        graphs[2] = new GraphTimeBar(title[2], axisHorizontal[2], axisVertical[2]);
        graphs[3] = new GraphFrequency(title[3], axisHorizontal[3], axisVertical[3]);
        graphs[4] = new GraphFrequencyDominant(title[4], axisHorizontal[4], axisVertical[4]);
    }

    /**
     * This updates all graphs and thus is quite a big function.
     * This gets called when the {@link DataInterval} is closed.
     * Do not forget to add the @Override tag to this function!
     *
     * @param dataInterval The data interval that was closed
     */
    @Override
    public void onDataIntervalClosed(DataInterval dataInterval) {
        // Only trigger if this is a new data interval
        if (dataInterval == previousDataInterval) {
            return;
        }

        previousDataInterval = dataInterval;

        // TODO Deze splitsing by default in DataPoint3D bouwen scheelt best wel veel
        // TODO We moeten er wel altijd doorheen om de tijd te scalen en overlap te voorkomen --> Kan dit in de graph axis gedaan worden?
        /**
         * Graph 1: Acceleration // time (line)
         * Graph 2: Highest velocity // time (block)
         * Graph 3: Dominant frequency // time (block)
         * Graph 4: Amplitude // frequency (line)
         * Graph 5: Dominant frequency // frequency (point)
         */
        graphs[0].sendNewDataToSeries(dataInterval.getDataPoints3DAcceleration());
        graphs[1].sendNewDataToSeries(dataInterval.getVelocitiesAbsMaxAsDataPoints());
        graphs[2].sendNewDataToSeries(dataInterval.getDominantFrequenciesAsDataPoints());
        graphs[3].sendNewDataToSeries(dataInterval.getFrequencyAmplitudes());
        graphs[4].sendNewDataToSeries(dataInterval.getExceedingAsDataPoints());
    }

    /**
     * Override the back button pressed to only hide this activity.
     * It will not close so we can reopen it later.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MeasuringActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * This finishes the existing activity,
     * clears the graphs and
     * removes it as listener.
     */
    public static void forceFinish() {
        // Call
        DataHandler.removeDataIntervalClosedListener(graphsActivity);
        if (graphsActivity != null) {
            graphsActivity.finish();
        }

        // Set null
        graphs = null;
        graphsActivity = null;
    }

}
