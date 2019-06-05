package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.BuildConfig;
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

    private static final String AUTHORITY_URI = "nl.gemeenterotterdam.bouwtrillingsmeter.android." + ".fileprovider";
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;

    TextView textViewName;
    TextView textViewDateTime;
    TextView textViewLocation;
    TextView textViewDescription;
    ImageView imageViewMeasurementPhoto;

    public static Measurement measurement;

    private Uri imageUri;

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
                onClickTakePicture();
            }
        });
        updateImageVisibility(measurement.getBitmap() != null);
        // TODO Remove
        updateImageVisibility(true);
        Utility.updateScaledPhoto(imageViewMeasurementPhoto, measurement.getBitmap());
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

        PackageManager packageManager = getPackageManager();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) || intent.resolveActivity(getPackageManager()) == null) {
            Utility.showAndGetPopup(this, R.layout.alert_dialog_ok, R.string.alert_dialog_no_camera);
        } else {
            startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
        }
    }

    /**
     * This gets called when we successfully take a picture.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.getExtras() != null) try {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            File imagePath = new File(getFilesDir(), "images");
            File imageFile = new File(imagePath, "default_image.jpg");
            imageUri = FileProvider.getUriForFile(this, AUTHORITY_URI, imageFile);

            measurement.setBitmap(bitmap, imageUri);
            imageUri = null;

            Utility.updateScaledPhoto(imageViewMeasurementPhoto, measurement.getBitmap());
            return;

        } catch (Exception e) {
            /* Do nothing */
        }

        Utility.showAndGetPopup(this, R.layout.alert_dialog_ok, R.string.alert_dialog_error_taking_picture);
    }

    /**
     * Creates an image file.
     *
     * @return The image file
     * @throws IOException If we fail
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        if (imageFile == null) {
            throw new IOException("Image file is null");
        }

        return imageFile;
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
