package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.AccelerometerControl;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.AccelerometerListener;

public class Measuring extends AppCompatActivity implements AccelerometerListener {

    private Activity thisActivity;
    private TextView textViewMeasuringCenter;
    private Button buttonMeasuringShowGraphs;
    private ProgressBar progressBarMeasuring;
    private boolean isMeasuring;

    // TODO DeBUG Remove this
    private TextView textViewMeasuringDebugX;
    private TextView textViewMeasuringDebugY;
    private TextView textViewMeasuringDebugZ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measuring);

        // Link elements
        textViewMeasuringCenter = (TextView) findViewById(R.id.textViewMeasuringCenter);
        progressBarMeasuring = (ProgressBar) findViewById(R.id.progressBarMeasuring);
        buttonMeasuringShowGraphs = (Button) findViewById(R.id.buttonMeasuringShowGraphs);

        isMeasuring = false;

        ChangePageToState();

        // DEBUG
        // TODO Remove this
        Button buttonDebug = (Button) findViewById(R.id.buttonDebugDeciveOnTable);
        buttonDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMeasuring = !isMeasuring;
                ChangePageToState();
            }
        });
        AccelerometerControl.addListener(this);
        textViewMeasuringDebugX = (TextView) findViewById(R.id.textViewMeasuringDebugX);
        textViewMeasuringDebugY = (TextView) findViewById(R.id.textViewMeasuringDebugY);
        textViewMeasuringDebugZ = (TextView) findViewById(R.id.textViewMeasuringDebugZ);
    }

    /**
     * This controls the UI elements and text
     * If isMeasuring: True if we are measuring, false if we are waiting for the device to be placed on the table
     */
    private void ChangePageToState() {
        // Measuring state
        if (isMeasuring) {
            textViewMeasuringCenter.setText(getResources().getString(R.string.measuring_measuring_now));
            buttonMeasuringShowGraphs.setVisibility(View.VISIBLE);
        }

        // Place device on table state
        else {
            textViewMeasuringCenter.setText(getResources().getString(R.string.measuring_place_device_on_table));
            buttonMeasuringShowGraphs.setVisibility(View.GONE);
        }
    }

    /**
     * This gets called when the device is ready to start measuring
     */
    public void OnDevicePlacedOnTable() {
        isMeasuring = true;
        ChangePageToState();
    }

    /**
     * This gets called when our accelerometer measures data.
     * The data does NOT have to be different from the previous dataset.
     * This will just get called every "tick".
     *
     * @param x The acceleration in the x direction.
     * @param y The acceleration in the y direction.
     * @param z The acceleration in the z direction.
     */
    @Override
    public void onReceivedData(float x, float y, float z) {
        textViewMeasuringDebugX.setText(Float.toString(x));
        textViewMeasuringDebugY.setText(Float.toString(y));
        textViewMeasuringDebugZ.setText(Float.toString(z));
    }
}
