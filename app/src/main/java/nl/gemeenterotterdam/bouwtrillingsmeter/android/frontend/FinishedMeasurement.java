package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;

/**
 * TODO Javadoc
 */
public class FinishedMeasurement extends AppCompatActivity {

    /**
     * On create
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
        // TODO We are creating new activites every time! Do something with this.
        FloatingActionButton fabDone = (FloatingActionButton) findViewById(R.id.fabFinishedMeasurementDone);
        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backend.onDoneWithMeasurement();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
