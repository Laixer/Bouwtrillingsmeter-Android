package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Measurement;

/**
 * This class controls the page that opens when you click a certain measurement
 * This does NOT control anything layout-wise within the measurement list
 */
public class MeasurementDetailsActivity extends AppCompatActivity {

    TextView textViewName;
    TextView textViewDateTime;
    TextView textViewLocation;
    TextView textViewDescription;
    ImageView imageViewMeasurementPhoto;

    public static Measurement measurement;

    /**
     * Gets called when this activity is launched
     * TODO Reconsider measurement calling structure
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_details);

        textViewName = (TextView) findViewById(R.id.textViewListMeasurementName);

        // Get the information from our activity
//        Intent intent = getIntent();
//        int measurementIndex = -1;
//        if (intent.hasExtra("nl.gemeenterotterdam.bouwtrillingsmeter.android.MEASUREMENT_INDEX")) {
//            measurementIndex = intent.getExtras().getInt("nl.gemeenterotterdam.bouwtrillingsmeter.android.MEASUREMENT_INDEX");
//        }
//
//        // Link our measurement
//        // TODO Handle a measurement getting error
//        measurement = Backend.MeasurementControl.getMeasurementByIndex(measurementIndex);

        // Link the descriptive UI elements
        textViewName = (TextView) findViewById(R.id.textViewDetailsMeasurementName);
        textViewDateTime = (TextView) findViewById(R.id.textViewDetailsMeasurementDateTime);
        textViewLocation = (TextView) findViewById(R.id.textViewDetailsMeasurementLocation);
        textViewDescription = (TextView) findViewById(R.id.textViewListMeasurementDescription);

        // Update all textviews at once
        onUpdateMeasurementTexts();

        // Imageview holding our photo
        // When we click the image and no image was present, we attempt to take a picture with the phones camera
        imageViewMeasurementPhoto = (ImageView) findViewById(R.id.imageViewDetailsMeasurementPhoto);
        imageViewMeasurementPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentTakePicture, 0);
            }
        });
        Utility.UpdateScaledPhoto(imageViewMeasurementPhoto, measurement.getPhoto());
    }

    /**
     * This gets called when we succesfully take a picture
     * TODO Save a pointer to the image in some way!
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        measurement.updatePhoto(bitmap);

        Utility.UpdateScaledPhoto(imageViewMeasurementPhoto, measurement.getPhoto());
    }

    /**
     * This will update all textviews containing the details about the measurement we are looking at
     * TODO Where shall we call this from?
     */
    public void onUpdateMeasurementTexts() {
        textViewName.setText(measurement.getName());
        textViewDateTime.setText(measurement.getDateTime());
        textViewLocation.setText(measurement.getLocation());
        textViewDescription.setText(measurement.getDescription());
    }

}
