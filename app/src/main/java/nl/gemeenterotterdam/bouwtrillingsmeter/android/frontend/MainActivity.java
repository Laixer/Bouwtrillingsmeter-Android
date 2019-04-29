package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import me.toptas.fancyshowcase.FancyShowCaseView;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BackendState;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BackendListener;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Measurement;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.StorageControl;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This is the main entry point of the application.
 * This page shows an overview of our saved activities, if any are present.
 * <p>
 * Clicking an item in our activity list takes us to the {@link MeasurementDetailsActivity}.
 * Clicking the '+ fab' on first visit takes us to the {@link FirstVisitTutorialActivity}.
 * Clicking the '+ fab' after that takes us to the {@link SettingsPageActivity}.
 * <p>
 * Our first visit will highlight the '+ fab' using the {@link #showcaseFirstVisit()} function.
 */
public class MainActivity extends AppCompatActivity implements BackendListener {

    Toolbar toolbar;
    ListView listViewMeasurements;
    FloatingActionButton fab;
    MainActivityMeasurementListAdapter listViewMeasurementsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the backend and set this as a listener
        Backend.addBackendStateListener(this);
        Backend.initialize(getApplicationContext(), getResources());

        // Initialize the utility frontend
        Utility.applicationContext = getApplicationContext();
        Utility.resources = getResources();
        PreferenceManager.fetchSharedPreferences(this);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating action button to start a new measurement
        fab = findViewById(R.id.fabNewMeasurement);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCreateNewMeasurementFab();
            }
        });

        // Setup measurements list and link adapter
        listViewMeasurements = (ListView) findViewById(R.id.listViewMeasurements);
        listViewMeasurements.setEmptyView(findViewById(R.id.textViewNoMeasurements));
        listViewMeasurementsAdapter = new MainActivityMeasurementListAdapter(this, Backend.getAllMeasurementsList());
        listViewMeasurements.setAdapter(listViewMeasurementsAdapter);
        listViewMeasurements.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Temporarily disable all items
                // TODO Implement

                Intent intentShowMeasurementDetails = new Intent(getApplicationContext(), MeasurementDetailsActivity.class);

                // TODO Reconsider measurement linking structure
//              intentShowMeasuremenstDetails.putExtra("nl.gemeenterotterdam.bouwtrillingsmeter.android.MEASUREMENT_INDEX", position);
                MeasurementDetailsActivity.measurement = Backend.getAllMeasurementsList().get(position);

                startActivity(intentShowMeasurementDetails);
            }
        });

        // TODO debug remove this
        // showcaseFirstVisit();
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
        fab.setEnabled(true);

        // Push snackbar if we just closed a measurement
        if (PreferenceManager.readBooleanPreference(R.string.pref_internal_measurement_finished)) {
            Snackbar.make(listViewMeasurements, getResources().getString(R.string.finished_measurement_exit_save_confirm), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            PreferenceManager.writeBooleanPreference(R.string.pref_internal_measurement_finished, false);
        }

        // Enable all items again
        // TODO Implement
    }

    /**
     * This starts our tutorial OR starts our settings widget
     */
    public void onClickCreateNewMeasurementFab() {
        // Temporarily disable the fab
        fab.setEnabled(false);

        // Launch the next activity
        Intent intent;
        if (!PreferenceManager.readBooleanPreference(R.string.pref_has_visited_before)) {
            intent = new Intent(getApplicationContext(), FirstVisitTutorialActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), SettingsPageActivity.class);

            // Tell the backend we are creating a new measurement
            Backend.onClickCreateNewMeasurement();
        }
        startActivity(intent);
    }

    /**
     * Gets called when the clear preference debug button is clicked
     * TODO Remove this
     */
    public void onClickDebugClearAppdata(View view) {
        PreferenceManager.clearAllPreferences();
        StorageControl.removeAllInternalStorage();
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

    /**
     * This checks if we have the proper sensors in our phone.
     * This only responds to {@link BackendState#UNSUPPORTED_HARDWARE}.
     *
     * @param newBackendState The backend state
     */
    @Override
    public void onBackendStateChanged(BackendState newBackendState) {
        if (newBackendState == BackendState.UNSUPPORTED_HARDWARE) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.alert_dialog_unsupported_hardware, null);
            dialogBuilder.setView(dialogView);

            dialogBuilder.setTitle(getResources().getString(R.string.alert_dialog_unsupported_hardware));
            final Dialog dialog = dialogBuilder.create();
            dialog.show();

            // Buttons
            dialogView.findViewById(R.id.buttonAlertDialogUnsupportedHardwareOk).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                    System.exit(0);
                }
            });
        }
    }

    @Override
    public void onExceededLimit() {
    }

    /**
     * This removes the mainactivity as a backend listener.
     */
    @Override
    public void finish() {
        Backend.removeBackendStateListener(this);
        super.finish();
    }

    /**
     * Should only be called when the app is killed.
     * TODO This triggers also on minimizing!
     */
    @Override
    public void onDestroy() {
        System.out.println("Assuming that our application is closing in onDestroy()....");
        Backend.onApplicationShutdown();
        super.onDestroy();
    }
}
