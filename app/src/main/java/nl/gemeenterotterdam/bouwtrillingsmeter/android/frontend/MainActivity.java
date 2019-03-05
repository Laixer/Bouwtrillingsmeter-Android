package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import me.toptas.fancyshowcase.FancyShowCaseView;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Measurement;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This is the main entry point of the application.
 * This page shows an overview of our saved activities, if any are present.
 * <p>
 * Clicking an item in our activity list takes us to the {@link MeasurementDetailsActivity}.
 * Clicking the '+ fab' on first visit takes us to the {@link FirstVisitTutorialActivity}.
 * Clicking the '+ fab' after that takes us to the {@link CategoryPageActivity}.
 * <p>
 * Our first visit will highlight the '+ fab' using the {@link #showcaseFirstVisit()} function.
 */
public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView listViewMeasurements;
    MeasurementListAdapter listViewMeasurementsAdapter;
    ArrayList<Measurement> measurements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the utility frontend
        Utility.ApplicationContext = getApplicationContext();
        Utility.Resources = getResources();

        // initialize the backend
        Backend.initialize();

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating action button to start a new measurement
        FloatingActionButton fab = findViewById(R.id.fabNewMeasurement);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCreateNewMeasurement();
            }
        });

        // Setup measurements list and link adapter
        listViewMeasurements = (ListView) findViewById(R.id.listViewMeasurements);
        listViewMeasurements.setEmptyView(findViewById(R.id.textViewNoMeasurements));
        listViewMeasurementsAdapter = new MeasurementListAdapter(this, Backend.getAllMeasurementsList());
        listViewMeasurements.setAdapter(listViewMeasurementsAdapter);
        listViewMeasurements.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentShowMeasuremenstDetails = new Intent(getApplicationContext(), MeasurementDetailsActivity.class);

                // TODO Reconsider measurement linking structure
//              intentShowMeasuremenstDetails.putExtra("nl.gemeenterotterdam.bouwtrillingsmeter.android.MEASUREMENT_INDEX", position);
                MeasurementDetailsActivity.measurement = Backend.getAllMeasurementsList().get(position);

                startActivity(intentShowMeasuremenstDetails);
            }
        });

        // Showcase first visit
        // TODO debug remove this
        showcaseFirstVisit();
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
     * This gets called when we press the back button and end up in the main activity again
     */
    @Override
    public void onResume() {
        super.onResume();
        listViewMeasurementsAdapter.onDatasetChanged();
    }

    /**
     * Gets fired when we click the start a new measurement button.
     * Creates a new measurement in the backend (always).
     * This also gets fired when the tutorial finishes.
     */
    public void onClickCreateNewMeasurement() {
        Backend.createNewMeasurement();

        // If we are in our first visit
        if (GlobalVariables.firstVisit) {
            Intent intentFirstVisitTutorial = new Intent(getApplicationContext(), FirstVisitTutorialActivity.class);
            startActivity(intentFirstVisitTutorial);
        }

        // If we have already visited before
        else {
            Intent intentCategorySelection = new Intent(getApplicationContext(), CategoryPageActivity.class);
            startActivity(intentCategorySelection);
        }
    }

    // Gets called when the clear preference debug button is clicked
    // TODO Remove this
    public void onClickDebugClearAppdata(View view) {
        new PreferenceManager(this).clearPreference();
    }

    /**
     * Creates a popup on first visit that points at the plus sign
     */
    private void showcaseFirstVisit() {
        View view = findViewById(R.id.fabNewMeasurement);
        String title = getResources().getString(R.string.first_visit_popup);

        FancyShowCaseView.Builder showCaseView = new FancyShowCaseView.Builder(this);
        showCaseView.focusOn(view);

        showCaseView.title(title);
        showCaseView.titleStyle(R.style.FirstVisitPopup, Gravity.CENTER);
        showCaseView.backgroundColor(getResources().getColor(R.color.first_visit_popup_background));

        showCaseView.build().show();
    }

}
