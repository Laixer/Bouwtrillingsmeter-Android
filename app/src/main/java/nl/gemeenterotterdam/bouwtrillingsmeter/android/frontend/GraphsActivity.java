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
import java.util.Calendar;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataHandler;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataInterval;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataIntervalClosedListener;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DataPoint3D;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.DominantFrequencies;

/**
 * This activity shows all the graphs.
 */
public class GraphsActivity extends AppCompatActivity implements DataIntervalClosedListener {

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
        if (graphs != null) {
            return;
        }

        // Get constants
        String[] title = getResources().getStringArray(R.array.graph_title);
        String[] axisHorizontal = getResources().getStringArray(R.array.graph_axis_horizontal);
        String[] axisVertical = getResources().getStringArray(R.array.graph_axis_vertical);

        // Create graphs
        graphs = new Graph[5];
        graphs[0] = new GraphTime(title[0], axisHorizontal[0], axisVertical[0]);
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

        // TODO Deze splitsing by default in DataPoint3D bouwen scheelt best wel veel
        // TODO We moeten er wel altijd doorheen om de tijd te scalen en overlap te voorkomen --> Kan dit in de graph axis gedaan worden?
        /**
         * Graph 1: Acceleration // time (line)
         * Graph 2: Highest velocity // time (block)
         * Graph 3: Dominant frequency // time (block)
         * Graph 4: Amplitude // frequency (line)
         * Graph 5: Dominant frequency // frequency (point)
         */
        graphs[0].sendNewDataToSeries(dataInterval.dataPoints3DAcceleration);
        graphs[1].sendNewDataToSeries(dataInterval.getVelocitiesAbsMaxAsDataPoints());
        graphs[2].sendNewDataToSeries(dataInterval.getDominantFrequenciesAsDataPoints());
        graphs[3].sendNewDataToSeries(dataInterval.frequencyAmplitudes);
        graphs[4].sendNewDataToSeries(dataInterval.getExceedingAsDataPoints());
    }
}
