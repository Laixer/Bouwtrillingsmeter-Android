package gemeenterotterdam.trillingmeterapp.frontend;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gemeenterotterdam.trillingmeterapp.R;
import gemeenterotterdam.trillingmeterapp.backend.Measurement;
import gemeenterotterdam.trillingmeterapp.backend.StorageControl;
import gemeenterotterdam.trillingmeterapp.backend.StorageWriteException;

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

    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;

    private TextView textViewName;
    private TextView textViewDateTime;
    private TextView textViewLocation;
    private TextView textViewDescription;
    private ImageView imageViewMeasurementPhoto;
    private FloatingActionButton fabTakePicture;

    public static Measurement measurement;

    /**
     * Gets called when this activity is launched.
     * TODO Reconsider measurement calling structure.
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

        // Update all text views at once
        onUpdateMeasurementTexts();

        // Click FAB to take new picture
        fabTakePicture = (FloatingActionButton) findViewById(R.id.fabDetailsTakePicture);
        fabTakePicture.setOnClickListener((view) -> onClickTakePicture());

        // Click imageview to enlarge photo
        imageViewMeasurementPhoto = (ImageView) findViewById(R.id.imageViewDetailsMeasurementPhoto);
        imageViewMeasurementPhoto.setOnClickListener((view) -> onClickEnlargePicture());
        updateImageView();

        // TODO Alpha
        if (Utility.isAlpha()) {
            imageViewMeasurementPhoto.setVisibility(View.GONE);
            ((View) fabTakePicture).setVisibility(View.GONE);
        }
    }

    /**
     * Enlarges the picture for us.
     * This gets triggered by clicking the image view.
     */
    private void onClickEnlargePicture() {
        // TODO Implement
    }

    /**
     * Takes a picture if we have a camera.
     * Shows a popup if we can't take a picture.
     */
    private void onClickTakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) || intent.resolveActivity(getPackageManager()) == null) {
            Utility.showAndGetPopup(this, R.layout.alert_dialog_ok, R.string.alert_dialog_no_camera);
        } else {
            startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
        }
    }

    /**
     * Updates the image view of this activity.
     * It disappears if no image is present.
     */
    private void updateImageView() {
        boolean bitmapPresent = measurement.getBitmap() != null;
        updateImageVisibility(bitmapPresent);
        Utility.updateScaledPhoto(imageViewMeasurementPhoto, measurement.getBitmap());
    }

    /**
     * This gets called when we successfully take a picture.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == REQUEST_CODE_IMAGE_CAPTURE
                && resultCode == RESULT_OK
                && data != null
                && data.getExtras() != null) try {

            // Get image
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap == null) {
                return;
            }

            // Write to storage
            String imageName = Utility.getNameForImage(measurement);
            StorageControl.writeImage(imageName, bitmap);

            // Write to measurement
            measurement.setBitmap(bitmap, imageName);
            updateImageView();
            return;

        } catch (StorageWriteException e) {
            System.out.println(e.getMessage());
        }

        Utility.showAndGetPopup(this, R.layout.alert_dialog_ok, R.string.alert_dialog_error_taking_picture);
    }

    /**
     * This will update all textviews containing the details about the measurement we are looking at
     * TODO Where shall we call this from?
     */
    public void onUpdateMeasurementTexts() {
        textViewName.setText(measurement.getName());
        textViewDateTime.setText(Utility.formatMeasurementDateTime(measurement));
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

}
