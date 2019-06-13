package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Deque;
import java.util.LinkedList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BackendState;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BackendListener;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.PreferenceManager;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This activity gets called when we finish setting up our settings in the {@link SettingsPageActivity} page.
 */
public class MeasuringActivity extends AppCompatActivity implements BackendListener {

    /**
     * The amount of ms we wait before displaying our next string message
     * in the {@link MeasuringActivity} while measuring.
     */
    private static final int TEXT_CYCLE_SLEEP_TIME_IN_MILLIS = 2200;

    /**
     * The minimum time needed between two exceeding events in order for our UI to display a new message.
     */
    private static final int MINIMUM_TIME_IN_MILLIS_BETWEEN_EXCEEDINGS = 1500;

    private TextView textViewCenter;
    private Button buttonShowGraphs;
    private Button buttonStartStop;

    // Static to be preserved after rotation
    private static Boolean isMeasuring = null;
    private static long millisLastShownExceeding = 0;
    private static long currentCycleId = 0;
    private static Deque<String> strings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measuring);

        // Link elements
        textViewCenter = (TextView) findViewById(R.id.textViewMeasuringCenter);
        buttonShowGraphs = (Button) findViewById(R.id.buttonMeasuringShowGraphs);
        buttonShowGraphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowGraphs();
            }
        });
        buttonStartStop = (Button) findViewById(R.id.buttonMeasuringStartStop);
        buttonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStartStop();
            }
        });

        // Set bool, but only if we just start this activity
        if (isMeasuring == null) {
            isMeasuring = false;
        }

        // Add this as a listener
        Backend.addBackendStateListener(this);

        // Update our page state
        updatePageState();
    }

    /**
     * This controls the UI elements and text
     * If isMeasuring: True if we are measuring, false if we are waiting for the device to be placed on the table
     */
    private void updatePageState() {
        if (!isMeasuring) {
            startTextCycleWaiting();
            buttonStartStop.setText(R.string.measuring_start);
        } else {
            startTextCycleMeasuring(false);
            buttonStartStop.setText(R.string.measuring_stop);
        }

        updateButtonsVisibility();
    }

    /**
     * Call this to update the visibility of our buttons.
     * Based on the {@link PreferenceManager}.
     */
    public void updateButtonsVisibility() {
        boolean showGraphs = PreferenceManager.readBooleanPreference(R.string.pref_show_graphs);
        boolean usePickup = PreferenceManager.readBooleanPreference(R.string.pref_use_pickup);
        buttonStartStop.setVisibility(usePickup ? View.GONE : View.VISIBLE);
        buttonShowGraphs.setVisibility(showGraphs && isMeasuring ? View.VISIBLE : View.GONE);
    }

    /**
     * Starts our text cycle while waiting
     */
    private void startTextCycleWaiting() {
        strings = new LinkedList<>();

        // TODO Alpha
        if (Utility.isAlpha()) {
            strings.addLast(getResources().getString(R.string.preparing_cycle_place_device_on_table));
        } else {
            strings.addLast(getResources().getString(R.string.preparing_cycle_place_device_on_table));
            strings.addLast(getResources().getString(R.string.preparing_cycle_start_flag));
        }

        startTextCycle();
    }

    /**
     * This starts the text cycle while measuring.
     */
    private void startTextCycleMeasuring(boolean showNowExceedingMessage) {
        strings = new LinkedList<>();

        if (showNowExceedingMessage) {
            strings.addFirst(getResources().getString(R.string.measuring_cycle_exceeding_detected_now));
        }

        // TODO Alpha
        if (Utility.isAlpha()) {
            strings.addLast(getResources().getString(R.string.measuring_cycle_measuring_now_alpha));
        } else {
            strings.addLast(getResources().getString(R.string.measuring_cycle_measuring_now));
            strings.addLast(getResources().getString(R.string.measuring_cycle_stop_flag));
            strings.addLast(getResources().getString(R.string.measuring_cycle_keep_on_table));
            strings.addLast(getResources().getString(R.string.measuring_cycle_exceeding_detected_flag));
        }

        startTextCycle();
    }

    /**
     * This loops through whatever is in {@link #strings}.
     * Upon callign this function again, the {@link #currentCycleId} is
     * incremented, stopping all existing loops.
     */
    private void startTextCycle() {
        new Thread(() -> {
            currentCycleId++;
            final long thisId = currentCycleId;

            while (currentCycleId == thisId) {
                // Push the text onto the textview
                // This can only be done in the UI thread
                runOnUiThread(() -> {
                    // Exit if a new cycle was started
                    if (currentCycleId != thisId) {
                        return;
                    }

                    // This can sometimes happen during rotation
                    if (strings == null || strings.size() == 0) {
                        return;
                    }

                    // Get and push the string
                    String text = strings.getFirst();
                    strings.removeFirst();
                    if (!text.equals(getResources().getString(R.string.measuring_cycle_exceeding_detected_now))) {
                        strings.addLast(text);
                    }

                    text = checkForFlags(text);
                    textViewCenter.setText(text);
                    textViewCenter.setGravity(Gravity.CENTER);
                });

                // Set timer
                // TODO This can be done cleaner 100%
                try {
                    Thread.sleep(TEXT_CYCLE_SLEEP_TIME_IN_MILLIS);
                } catch (Exception e) {
                    //
                }
            }
        }).start();
    }

    /**
     * This checks if we have flagged our text message.
     * It can either be flag_start or flag_stop.
     * This function converts the flag to the desired message
     * based on our user preferences.
     *
     * @param string The string, without knowing if it is a flag or not
     * @return The proper string or the original string if it is not a flag
     */
    private String checkForFlags(String string) {
        if (string.equals(getResources().getString(R.string.preparing_cycle_start_flag))) {
            boolean usePickup = PreferenceManager.readBooleanPreference(R.string.pref_use_pickup);
            int id = usePickup ? R.string.preparing_cycle_start_flat : R.string.preparing_cycle_start_button;
            return getResources().getString(id);
        }

        if (string.equals(getResources().getString(R.string.measuring_cycle_stop_flag))) {
            boolean usePickup = PreferenceManager.readBooleanPreference(R.string.pref_use_pickup);
            int id = usePickup ? R.string.measuring_cycle_stop_pickup : R.string.measuring_cycle_stop_button;
            return getResources().getString(id);
        }

        if (string.equals(getResources().getString(R.string.measuring_cycle_exceeding_detected_flag))) {
            int id = Backend.isCurrentMeasurementExceeded() ? R.string.measuring_cycle_exceeding_detected : R.string.measuring_cycle_no_exceeding_detected;
            return getResources().getString(id);
        }

        return string;
    }

    /**
     * Not used at the moment.
     *
     * @param newBackendState The new state
     */
    @Override
    public void onBackendStateChanged(BackendState newBackendState) {
        // Not used
    }

    /**
     * This gets called when we exceed our limit.
     * This determines whether or not we should display this.
     */
    @Override
    public void onExceededLimit() {
        long millisLastExceeding = Backend.getTimeLastExceeding().getTime();
        if (millisLastExceeding - millisLastShownExceeding > MINIMUM_TIME_IN_MILLIS_BETWEEN_EXCEEDINGS) {
            startTextCycleMeasuring(true);
        }
    }

    /**
     * Open the graphs, by calling {@link GraphsActivity}.
     * This does not start a new activity if the activity is already open.
     */
    private void onClickShowGraphs() {
        Intent intent = new Intent(getApplicationContext(), GraphsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * Starts or stops our measurement.
     */
    private void onClickStartStop() {
        if (!isMeasuring) {
            Backend.onReadyToStartMeasurement();
            onStartMeasuring();
        } else {
            Backend.onRequestEndMeasurement();
            onFinishMeasuring();
        }
    }

    /**
     * This gets called when we press the back button.
     */
    @Override
    public void onBackPressed() {
        // Create a dialog
        int resource = isMeasuring ? R.string.measuring_alert_dialog_stop : R.string.measuring_alert_dialog_back;
        Dialog dialog = Utility.showAndGetPopup(this, R.layout.alert_dialog_yes_no, resource);
        dialog.setCanceledOnTouchOutside(true);

        // Buttons
        dialog.findViewById(R.id.buttonAlertDialogYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMeasuring) {
                    onBackToMainActivity();
                } else {
                    // This will stop the measurement and
                    // get us to the end screen.
                    onClickStartStop();
                }
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.buttonAlertDialogNo).setOnClickListener(new View.OnClickListener() {
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
    public void onBackToMainActivity() {
        Backend.onPressedBackButton();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        finish();
    }

    /**
     * Updates the UI when we start measuring
     */
    private void onStartMeasuring() {
        isMeasuring = true;
        updatePageState();
    }

    /**
     * Updates the UI when we finish measuring (not cancel!).
     */
    private void onFinishMeasuring() {
        DetailsActivity.measurement = Backend.getLastMeasurement();
        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        startActivity(intent);

        // Close this activity
        finish();
    }

    /**
     * Called when this activity closes.
     */
    @Override
    public void finish() {
        GraphsActivity.forceFinish();
        isMeasuring = null;
        Backend.removeBackendStateListener(this);
        super.finish();
    }
}
