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
        // Get constants
        int graphCount = Utility.Resources.getInteger(R.integer.graphs_count);
        graphs = new Graph[graphCount];

        // Create all graphs
        for (int i = 0; i < graphCount; i++) {
            // Get text
            String title = getResources().getStringArray(R.array.graph_title)[i];
            String axisHorizontal = getResources().getStringArray(R.array.graph_axis_horizontal)[i];
            String axisVertical = getResources().getStringArray(R.array.graph_axis_vertical)[i];

            // Iterate trough
            for (int j = 0; j < graphCount; j++) {
                int resourceValue = Utility.Resources.getIntArray(R.array.graphs_0_time_1_frequency_2_fdom)[i];
                if (resourceValue == 0) {
                    graphs[i] = new GraphTime(title, axisHorizontal, axisVertical);
                } else if (resourceValue == 1) {
                    graphs[i] = new GraphFrequency(title, axisHorizontal, axisVertical);
                } else if (resourceValue == 2) {
                    graphs[i] = new GraphFrequencyDominant(title, axisHorizontal, axisVertical);
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
     * TODO Lots of duplicate code
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
        ArrayList<DataPoint3D<Long>> dataPoints3DTime;
        ArrayList<DataPoint3D<Double>> dataPoints3DFrequency;
        DataPoint[] dataPoints1D;

        /**
         * Graph 1: Acceleration // time
         */
        graph = graphs[0];
        dataPoints3DTime = dataInterval.dataPoints3DAcceleration;
        for (int dimension = 0; dimension < 3; dimension++) {
            dataPoints1D = new DataPoint[dataPoints3DTime.size()];
            for (int j = 0; j < dataPoints3DTime.size(); j++) {
                dataPoints1D[j] = (new DataPoint(dataPoints3DTime.get(j).xAxisValue / 1000.0, dataPoints3DTime.get(j).values[dimension]));
            }
            graph.sendNewDataToSeries(dataPoints1D, dimension);
        }

        /**
         * Graph 2: Velocity // time
         */
        graph = graphs[1];
        dataPoints3DTime = dataInterval.velocities;
        for (int dimension = 0; dimension < 3; dimension++) {
            dataPoints1D = new DataPoint[dataPoints3DTime.size()];
            for (int j = 0; j < dataPoints3DTime.size(); j++) {
                dataPoints1D[j] = (new DataPoint(dataPoints3DTime.get(j).xAxisValue / 1000.0, dataPoints3DTime.get(j).values[dimension]));
            }
            graph.sendNewDataToSeries(dataPoints1D, dimension);
        }

        /**
         * Graph 3: Dominant frequency // time
         */
        graph = graphs[2];
        double time = dataInterval.dataPoints3DAcceleration.get(0).xAxisValue / 1000.0;
        for (int dimension = 0; dimension < 3; dimension++) {
            dataPoints1D = new DataPoint[]{new DataPoint(time, dataInterval.dominantFrequencies.frequencies[dimension])};
            graph.sendNewDataToSeries(dataPoints1D, dimension);
        }

        /**
         * Graph 4: Amplitude // frequency
         */
        graph = graphs[3];
        dataPoints3DFrequency = dataInterval.frequencyAmplitudes;
        for (int dimension = 0; dimension < 3; dimension++) {
            dataPoints1D = new DataPoint[dataPoints3DFrequency.size()];
            for (int j = 0; j < dataPoints3DFrequency.size(); j++) {
                dataPoints1D[j] = (new DataPoint(dataPoints3DFrequency.get(j).xAxisValue, dataPoints3DFrequency.get(j).values[dimension]));
            }
            graph.sendNewDataToSeries(dataPoints1D, dimension);
        }

        /**
         * Graph 5: Dominant frequency // frequency
         */
        graph = graphs[4];
        ArrayList<DataPoint> tempDataPoints = new ArrayList<DataPoint>();
        DominantFrequencies dominantFrequencies = dataInterval.dominantFrequencies;
        for (int dimension = 0; dimension < 3; dimension++) {
            if (dominantFrequencies.exceedsLimit[dimension]) {
                double frequency = dominantFrequencies.frequencies[dimension];
                double velocity = dominantFrequencies.velocities[dimension];
                tempDataPoints.add(new DataPoint(frequency, velocity));
            }
        }

        // If we created new datapoints
        if (tempDataPoints.size() > 0) {
            dataPoints1D = new DataPoint[tempDataPoints.size()];
            for (int i = 0; i < tempDataPoints.size(); i++) {
                dataPoints1D[i] = tempDataPoints.get(i);
            }
            graph.sendNewDataToSeries(dataPoints1D, 0);
        }
    }
}
