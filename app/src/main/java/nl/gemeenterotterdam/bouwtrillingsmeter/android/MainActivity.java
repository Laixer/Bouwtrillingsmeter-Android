package nl.gemeenterotterdam.bouwtrillingsmeter.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listViewMeasurements;
    ArrayList<Measurement> measurements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setupGUIElements();
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
     * Gets our GUI pointers on point
     * Also sets up clicking events and such
     */
    private void setupGUIElements() {

        // Setup measurements list and link adapter
        listViewMeasurements = (ListView) findViewById(R.id.listViewMeasurements);
        listViewMeasurements.setAdapter(new MeasurementAdapter(this, DebugMeasurementsList()));

        // Link measurement click to go to its details
        listViewMeasurements.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("     Position clicked = " + position);
                Intent intentShowMeasurementDetails = new Intent(getApplicationContext(), MeasurementDetails.class);
                intentShowMeasurementDetails.putExtra("nl.gemeenterotterdam.bouwtrillingsmeter.android.MEASUREMENT_INDEX", position);
                startActivity(intentShowMeasurementDetails);
            }
        });
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

}
