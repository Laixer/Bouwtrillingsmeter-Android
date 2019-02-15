package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Measurement;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView listViewMeasurements;
    ArrayList<Measurement> measurements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating action button to start a new measurement
        FloatingActionButton fab = findViewById(R.id.fabNewMeasurement);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartMeasurement();
            }
        });

        // Setup measurements list and link adapter
        listViewMeasurements = (ListView) findViewById(R.id.listViewMeasurements);
        listViewMeasurements.setAdapter(new MeasurementAdapter(this, DebugMeasurementsList()));
        listViewMeasurements.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentShowMeasuremenstDetails = new Intent(getApplicationContext(), MeasurementDetails.class);
                intentShowMeasuremenstDetails.putExtra("nl.gemeenterotterdam.bouwtrillingsmeter.android.MEASUREMENT_INDEX", position);
                startActivity(intentShowMeasuremenstDetails);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Function used for debugging purposes
     * TODO Remove this (debug)
     */
    private void DebugFunction() {
        //
    }

    /**
     * Used to create a list of 5 measurements, used for debugging
     * TODO Remove this (debug)
     * @return
     */
    private ArrayList<Measurement> DebugMeasurementsList() {
        ArrayList<Measurement> measurements = new ArrayList<Measurement>();

        measurements.add(new Measurement("Measurement 1", null));
        measurements.add(new Measurement("Measurement 2", null));
        measurements.add(new Measurement("Measurement 3", null));
        measurements.add(new Measurement("Measurement 4", null));
        measurements.add(new Measurement("Measurement 5", null));

        return measurements;
    }

    /**
     * Gets fired when we click the start a new measurement button
     * This also gets fired when the tutorial finishes
     */
    public void onStartMeasurement() {
        if (GlobalVariables.firstVisit) {
            Intent intentFirstVisitTutorial = new Intent(getApplicationContext(), FirstVisitTutorial.class);
            startActivity(intentFirstVisitTutorial);
        } else {
            Intent intentStartMeasurement = new Intent(getApplicationContext(), Measuring.class);
            startActivity(intentStartMeasurement);
        }
    }

    // Gets called when the clear preference debug button is clicked
    // TODO Remove this
    public void onClickClearPreferences(View view) {
        new PreferenceManager(this).clearPreference();
    }
}
