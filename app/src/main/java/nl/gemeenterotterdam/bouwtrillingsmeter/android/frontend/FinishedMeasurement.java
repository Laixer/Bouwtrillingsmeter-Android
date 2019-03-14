package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;

/**
 * TODO Javadoc
 */
public class FinishedMeasurement extends AppCompatActivity {

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

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        // Close this activity
        finish();

        // Trigger snackbar
        MainActivity.pushSnackbar(getResources().getString(R.string.finished_measurement_exit_save_confirm));
    }
}
