package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import me.toptas.fancyshowcase.FancyShowCaseView;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BackendListener;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BackendState;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.PreferenceManager;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This is the main entry point of the application.
 * This page shows an overview of our saved activities, if any are present.
 * <p>
 * Clicking an item in our activity list takes us to the {@link DetailsActivity}.
 * Clicking the '+ fab' on first visit takes us to the {@link FirstVisitTutorialActivity}.
 * Clicking the '+ fab' after that takes us to the {@link SettingsPageActivity}.
 * <p>
 * Our first visit will highlight the '+ fab' using the {@link #showcaseFirstVisit()} function.
 */
public class MainActivity extends AppCompatActivity implements BackendListener {

    private static final boolean SHOW_FIRST_VISIT_TUTORIAL = false;
    private static final boolean SHOW_FIRST_VISIT_SHOWCASE = false;

    Toolbar toolbar;
    ListView listViewMeasurements;
    FloatingActionButton fab;
    MainActivityMeasurementListAdapter listViewMeasurementsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request permissions
        Utility.askForPermissions(this);

        // Initialize the backend and set this as a listener
        Backend.addBackendStateListener(this);
        Backend.initialize(getApplicationContext(), getResources());

        // Initialize the utility frontend
        Utility.applicationContext = getApplicationContext();
        Utility.resources = getResources();

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
        listViewMeasurements.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            // Temporarily disable all items
            // TODO Implement

            Intent intentShowMeasurementDetails = new Intent(getApplicationContext(), DetailsActivity.class);

            // TODO Reconsider measurement linking structure
//              intentShowMeasuremenstDetails.putExtra("nl.gemeenterotterdam.bouwtrillingsmeter.android.MEASUREMENT_INDEX", position);
            DetailsActivity.measurement = Backend.getAllMeasurementsList().get(position);

            startActivity(intentShowMeasurementDetails);
        });

        // Show the highlight on the first visit
        if (SHOW_FIRST_VISIT_SHOWCASE) {
            showcaseFirstVisit();
        }

        debugClearStack();
    }

    /**
     * TODO Remove this or move this.
     */
    private void debugClearStack() {
    }

    /**
     * Toolbar stuff
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Toolbar stuff
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This gets called when we press the back button
     * and end up in the main activity again.
     */
    @Override
    public void onResume() {
        super.onResume();
        listViewMeasurementsAdapter.onDataSetChanged();
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
        if (SHOW_FIRST_VISIT_TUTORIAL && !PreferenceManager.readBooleanPreference(R.string.pref_has_visited_before)) {
            intent = new Intent(getApplicationContext(), FirstVisitTutorialActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), SettingsPageActivity.class);

            // Tell the backend we are creating a new measurement
            Backend.onClickCreateNewMeasurement();
        }
        startActivity(intent);
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

            View dialogView = inflater.inflate(R.layout.alert_dialog_ok, null);
            dialogBuilder.setView(dialogView);

            dialogBuilder.setTitle(getResources().getString(R.string.alert_dialog_unsupported_hardware_title));
            dialogBuilder.setMessage(getResources().getString(R.string.alert_dialog_unsupported_hardware_message));
            final Dialog dialog = dialogBuilder.create();
            dialog.show();

            // Buttons
            dialogView.findViewById(R.id.buttonAlertDialogOk).setOnClickListener(new View.OnClickListener() {
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
