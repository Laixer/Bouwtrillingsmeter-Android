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

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView listViewMeasurements;
    MeasurementAdapter listViewMeasurementsAdapter;
    ArrayList<Measurement> measurements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the utility frontend
        Utility.ApplicationContext = getApplicationContext();
        Utility.Resources = getResources();

        // Initialize the backend
        Backend.Initialize();

        // TODO Remove this debug statement
        // Backend.MeasurementControl.setDebugMeasurementsList();

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
        listViewMeasurements.setEmptyView(findViewById(R.id.textViewNoMeasurements));
        listViewMeasurementsAdapter = new MeasurementAdapter(this, Backend.MeasurementControl.getAllMeasurements());
        listViewMeasurements.setAdapter(listViewMeasurementsAdapter);
        listViewMeasurements.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentShowMeasuremenstDetails = new Intent(getApplicationContext(), MeasurementDetails.class);
                intentShowMeasuremenstDetails.putExtra("nl.gemeenterotterdam.bouwtrillingsmeter.android.MEASUREMENT_INDEX", position);
                startActivity(intentShowMeasuremenstDetails);
            }
        });

        // Showcase first visit
        // TODO debug remove this
        // ShowcaseFirstVisit();
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
        listViewMeasurementsAdapter.OnDatasetChanged();
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
            Intent intentCategorySelection = new Intent(getApplicationContext(), CategoryPage.class);
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
    private void ShowcaseFirstVisit() {
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
