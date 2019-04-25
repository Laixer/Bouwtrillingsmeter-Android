package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BackendState;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BackendListener;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This activity gets called when we finish setting up our settings in the {@link SettingsPageActivity} page.
 * First, we await till the user places the phone flat on a surface.
 * TODO Implement this call from the backend.
 * <p>
 * When the phone is flat, we begin our measurements.
 * TODO Implement this call to the backend.
 */
public class MeasuringActivity extends AppCompatActivity implements BackendListener {

    private TextView textViewMeasuringCenter;
    private Button buttonMeasuringShowGraphs;
    private boolean isMeasuring;
    private boolean hasUnlockedGraphs;
    private long timePreviousTouch = 0;
    private int totalTapCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measuring);

        // Set this as listener
        Backend.addBackendStateListener(this);

        // Link elements
        textViewMeasuringCenter = (TextView) findViewById(R.id.textViewMeasuringCenter);
        buttonMeasuringShowGraphs = (Button) findViewById(R.id.buttonMeasuringShowGraphs);
        buttonMeasuringShowGraphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowGraphs();
            }
        });

        // Set bools
        isMeasuring = false;
        hasUnlockedGraphs = PreferenceManager.readBooleanPreference(R.string.pref_graph_unlocked_before);

        UpdatePageState();
    }

    /**
     * Used to check for the 7 click unlock for the graphs
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            long timeCurrentTouch = Calendar.getInstance().getTimeInMillis();
            long dt = timeCurrentTouch - timePreviousTouch;

            // Reset tap count after not having tapped for 1 second
            if (dt > 1000) {
                totalTapCount = 0;
            }

            // Increment tap count
            else {
                totalTapCount++;

                // If we have enough taps, act accordingly
                if (totalTapCount >= 7) {
                    hasUnlockedGraphs = !hasUnlockedGraphs;
                    PreferenceManager.writeBooleanPreference(R.string.pref_graph_unlocked_before, hasUnlockedGraphs);

                    if (isMeasuring) {
                        buttonMeasuringShowGraphs.setVisibility(hasUnlockedGraphs ? View.VISIBLE : View.GONE);
                    }

                    // Show snackbar
                    View view = findViewById(R.id.textViewMeasuringCenter);
                    int resource = hasUnlockedGraphs ? R.string.measuring_snackbar_unlocked_graphs : R.string.measuring_snackbar_relocked_graphs;
                    Snackbar.make(view, getResources().getString(resource), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    // Reset the total tap count
                    totalTapCount = 0;
                }
            }

            // Save the time
            timePreviousTouch = timeCurrentTouch;
        }

        super.onTouchEvent(e);
        return true;
    }

    /**
     * This controls the UI elements and text
     * If isMeasuring: True if we are measuring, false if we are waiting for the device to be placed on the table
     */
    private void UpdatePageState() {
        // Measuring state
        if (isMeasuring) {
            if (hasUnlockedGraphs) {
                buttonMeasuringShowGraphs.setVisibility(View.VISIBLE);
            }
            startMeasuringTextCycle();
        }

        // Place device on table state
        else {
            textViewMeasuringCenter.setText(getResources().getString(R.string.measuring_place_device_on_table));
            buttonMeasuringShowGraphs.setVisibility(View.GONE);
        }
    }

    /**
     * This starts the text cycle while measuring.
     */
    private void startMeasuringTextCycle() {
        final ArrayList<String> strings = new ArrayList<String>();
        strings.add(getResources().getString(R.string.measuring_cycle_measuring_now));
        strings.add(getResources().getString(R.string.measuring_cycle_keep_on_table));
        strings.add(getResources().getString(R.string.measuring_cycle_lift_to_stop));
        strings.add(getResources().getString(R.string.measuring_cycle_no_exceeding_detected));

        new Thread(new Runnable() {
            public void run() {
                int index = 0;
                while (isMeasuring) {
                    // Determine the desired string
                    String text = "";

                    // Check for (new) backend exceedings
                    // Our iterations pause for one cycle
                    Date dateLastExceeding = Backend.getTimeLastExceeding();
                    long millisLastExceeding = 0;
                    if (dateLastExceeding != null) {
                        millisLastExceeding = dateLastExceeding.getTime();
                    }
                    long millisCurrent = Calendar.getInstance().getTimeInMillis();
                    long dt = millisCurrent - millisLastExceeding;

                    if (Backend.isCurrentMeasurementExceeded()
                            && (Backend.getTimeLastExceeding() == null
                            || -Backend.getTimeLastExceeding().getTime() + Calendar.getInstance().getTimeInMillis() > Constants.minimumTimeInMillisBetweenExceedings)) {
                        text = getResources().getString(R.string.measuring_cycle_measuring_now);
                    }

                    // Else display a regular message
                    else {
                        if (index >= strings.size()) {
                            index = 0;
                        }
                        text = strings.get(index);
                        index++;
                    }

                    // Push the text onto the textview
                    // This can only be done in the UI thread
                    final String textAsFinal = text;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewMeasuringCenter.setText(textAsFinal);
                            textViewMeasuringCenter.setGravity(Gravity.CENTER);
                        }
                    });

                    // Set timer
                    try {
                        Thread.sleep(Constants.measuringTextCycleSleepTimeInMillis);
                    } catch (Exception e) {
                        //
                    }

                }
            }
        }).start();
    }

    /**
     * Open the graphs, by calling {@link GraphsActivity}.
     * This does not start a new activity if the activity is already open.
     */
    public void onClickShowGraphs() {
        Intent intent = new Intent(getApplicationContext(), GraphsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * This gets called when we press the back button.
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.alert_dialog_cancel_measurement, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle(getResources().getString(R.string.measuring_alert_dialog));
        final Dialog dialog = dialogBuilder.create();
        dialog.show();

        // Buttons
        dialogView.findViewById(R.id.buttonAlertDialogCancelMeasurementYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelMeasurement();
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.buttonAlertDialogCancelMeasurementNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * This gets called when we (want to) cancel our current measurement.
     * This makes us go back to the {@link MainActivity} page.
     */
    public void onCancelMeasurement() {
        Backend.onPressedBackButton();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        finish();
    }

    /**
     * Gets called when the backend state changes
     *
     * @param newBackendState The new Backend State
     */
    @Override
    public void onBackendStateChanged(BackendState newBackendState) {
        switch (newBackendState) {
            case MEASURING:
                isMeasuring = true;
                UpdatePageState();
                break;

            case FINISHED_MEASUREMENT:
                Intent intent = new Intent(getApplicationContext(), FinishedMeasurementActivity.class);
                startActivity(intent);

                // Close this activity
                finish();

                // Close the graphs activity
                GraphsActivity.forceFinish();
                break;
        }
    }

    @Override
    public void onExceededLimit() {
    }

    /**
     * Remove this as a listener
     */
    @Override
    public void finish() {
        Backend.removeBackendStateListener(this);
        super.finish();
    }
}
