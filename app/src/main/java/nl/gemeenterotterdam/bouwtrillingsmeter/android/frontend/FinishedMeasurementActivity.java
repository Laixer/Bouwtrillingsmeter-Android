package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Measurement;

/**
 * This activity launches when we successfully complete our measurement.
 */
public class FinishedMeasurementActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private Measurement measurement;

    /**
     * On create.
     * When we exit this activity, all previous activities but the
     * {@link MainActivity} are closed and removed from the backstack.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_measurement);

        // Link elements
        measurement = Backend.getLastMeasurement();
        editTextName = (EditText) findViewById(R.id.editTextFinishedMeasurementName);
        editTextName.setText(measurement.getName());
        editTextDescription = (EditText) findViewById(R.id.editTextFinishedMeasurementDescription);
        editTextDescription.setText(measurement.getDescription());
        setupEditTextListeners();

        // Indicate if we have exceeded any limits or not
        TextView textViewExceeded = (TextView) findViewById(R.id.textViewFinishedMeasurementExceeded);
        boolean exceeded = Backend.getCurrentMeasurement().getAllExceedingDataIntervals() != null;
        if (exceeded) {
            textViewExceeded.setText(getResources().getString(R.string.finished_measurement_exceeded));
        } else {
            textViewExceeded.setText(getResources().getString(R.string.finished_measurement_not_exceeded));
        }

        // Take us back to the main screen
        FloatingActionButton fabDone = (FloatingActionButton) findViewById(R.id.fabFinishedMeasurementDone);
        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExitThisActivity();
            }
        });
    }

    private void setupEditTextListeners() {
        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                measurement.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                measurement.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        onExitThisActivity();
    }

    /**
     * This takes us back to the main activity.
     * This also pushes a snackbar, confirming our measurement was saved and sent.
     */
    private void onExitThisActivity() {
        Backend.onDoneWithMeasurement();

        // Flag the mainactivity so it displays a snackbar
        PreferenceManager.writeBooleanPreference(R.string.pref_internal_measurement_finished, true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        // Close this activity
        finish();
    }
}
