package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Measurement;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class controls the page that opens when you click a list item in the measurement list on the {@link MainActivity} page.
 * This does NOT control anything layout-wise within the measurement list, this is done by {@link MainActivityMeasurementListAdapter}.
 * <p>
 * Clicking the displayed photo attempts to open our camera, coded in the {@link #onCreate(Bundle)} function.
 * The callback is handled by {@link #onActivityResult(int, int, Intent)}.
 */
public class DetailsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_details);

        // Link the descriptive UI elements
        textViewName = (TextView) findViewById(R.id.textViewListMeasurementName);
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
        updateImageVisibility(true);
        Utility.updateScaledPhoto(imageViewMeasurementPhoto, measurement.getBitmap());
    }

    /**
     * This gets called when we succesfully take a picture
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            measurement.setBitmap(bitmap);

            Utility.updateScaledPhoto(imageViewMeasurementPhoto, measurement.getBitmap());
        } catch (NullPointerException e) {
            // If we don't get an image
        }
    }

    /**
     * This will update all textviews containing the details about the measurement we are looking at
     * TODO Where shall we call this from?
     */
    public void onUpdateMeasurementTexts() {
        textViewName.setText(measurement.getName());
        textViewDateTime.setText(Utility.formatDate(measurement.getDateStart()));
        textViewDescription.setText(measurement.getDescription());

        Address address = measurement.getAddress();
        if (address == null) {
            textViewLocation.setText(getResources().getString(R.string.measurement_default_location));
        } else {
            textViewLocation.setText(address.getAddressLine(0));
        }
    }

    /**
     * Used to show or hide our image.
     *
     * @param visible True if visible
     */
    private void updateImageVisibility(boolean visible) {
        imageViewMeasurementPhoto.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

}
