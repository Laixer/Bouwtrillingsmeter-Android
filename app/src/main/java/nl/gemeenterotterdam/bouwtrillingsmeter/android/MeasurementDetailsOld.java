package nl.gemeenterotterdam.bouwtrillingsmeter.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MeasurementDetailsOld extends AppCompatActivity {

    TextView textViewName;
    ImageView imageViewPhoto;

    /**
     * Gets called when this activity is launched
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_details_old);

        setupGUIElements();

        // Get the information from our activity
        Intent intent = getIntent();
        int measurementIndex = intent.getIntExtra("nl.gemeenterotterdam.bouwtrillingsmeter.android.MEASUREMENT_INDEX", -1);

        if (measurementIndex > -1) {
            // TODO Get measurement and do stuff
            textViewName.setText(measurementIndex);
        }
    }

    /**
     * Gets our GUI element pointers on point
     */
    private void setupGUIElements() {
        textViewName = (TextView) findViewById(R.id.textViewName);
        imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
    }
}
