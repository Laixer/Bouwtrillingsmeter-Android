package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

public class Measuring extends AppCompatActivity {

    TextView textViewPlaceDeviceOnTable;
    Button buttonDebugDeviceOnTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measuring);

        // Text view to place device on table
        textViewPlaceDeviceOnTable = (TextView) findViewById(R.id.textViewPlaceDeviceOnTable);

        // Debug button to indicate the device is horizontal
        // TODO Remove this debug button
        buttonDebugDeviceOnTable = (Button) findViewById(R.id.buttonDebugDeciveOnTable);
        buttonDebugDeviceOnTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDevicePlacedHorizontally();
            }
        });
    }

    /**
     * This fires when the device is placed on a table in a horizontal manner
     * TODO Implement this
     */
    private void onDevicePlacedHorizontally() {
        System.out.println("Device is placed horizontally");
    }

}
