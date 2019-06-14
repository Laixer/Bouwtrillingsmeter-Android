package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

public class MPAndroidChartActivity extends AppCompatActivity {

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpandroid_chart);

        // Fetch chart
        chart = (LineChart) findViewById(R.id.chart);

        // Setup
        startAutomaticUpdate();
    }

    private void startAutomaticUpdate() {
        TimerTask timerTask = new TimerTask() {
            public synchronized void run() {

                // Create new entries
                ArrayList<Entry> entries = new ArrayList<>();
                Random random = new Random(Calendar.getInstance().getTimeInMillis());
                for (int i = 0; i < 100; i++) {
                    entries.add(new Entry(i, random.nextFloat()));
                }

                // Push to graph
                LineDataSet lineDataSet = new LineDataSet(entries, "my label");
                LineData lineData = new LineData(lineDataSet);
                chart.setData(lineData);
                chart.invalidate();
            }

        };

        // Schedule
        Timer timer = new Timer("timer");
        int interval = 1000 / 20;
        timer.scheduleAtFixedRate(timerTask, 0, interval);

    }

}
