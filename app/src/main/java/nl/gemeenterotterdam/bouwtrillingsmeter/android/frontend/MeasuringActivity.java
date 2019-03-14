package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;

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
public class MeasuringActivity extends AppCompatActivity {

    private Activity thisActivity;
    private TextView textViewMeasuringCenter;
    private Button buttonMeasuringShowGraphs;
    private ProgressBar progressBarMeasuring;
    private boolean isMeasuring;

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
        Button buttonDebugFlat = (Button) findViewById(R.id.buttonDebugDeciveOnTable);
        buttonDebugFlat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMeasuring = !isMeasuring;
                ChangePageToState();
            }
        });
        Button buttonDebugStopMeasuring = (Button) findViewById(R.id.buttonDebugStopMeasuring);
        buttonDebugStopMeasuring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopMeasuring();
            }
        });

        // Link show graph
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
            Backend.debugOnPhoneFlat();
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

    public void onClickShowGraphs() {

    }

    /**
     * This gets called when we press the back button.
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle(getResources().getString(R.string.measuring_backbutton_text));
        final Dialog dialog = dialogBuilder.create();
        dialog.show();

        // Buttons
        dialogView.findViewById(R.id.buttonAlertDialogYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelMeasurement();
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.buttonAlertDialogNo).setOnClickListener(new View.OnClickListener() {
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
     * TODO Javadoc
     * TODO Remove debug statement here (backend call)
     */
    public void onStopMeasuring() {
        Backend.onPickUpPhoneWhileMeasuring();

        Intent intent = new Intent(getApplicationContext(), FinishedMeasurement.class);
        startActivity(intent);

        // Close this activity
        finish();
    }
}
