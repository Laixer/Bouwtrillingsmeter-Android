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

/**
 * This class controls the page that opens when you click a certain measurement
 * This does NOT control anything layout-wise within the measurement list
 */
public class MeasurementDetails extends AppCompatActivity {

    TextView textViewName;
    ImageView imageViewMeasurementPhoto;

    /**
     * Gets called when this activity is launched
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_details);

        textViewName = (TextView) findViewById(R.id.textViewListMeasurementName);

        // Get the information from our activity
        Intent intent = getIntent();
        if (intent.hasExtra("nl.gemeenterotterdam.bouwtrillingsmeter.android.MEASUREMENT_INDEX")) {
            // TODO Get measurement and do stuff
            int measurementIndex = intent.getExtras().getInt("nl.gemeenterotterdam.bouwtrillingsmeter.android.MEASUREMENT_INDEX");
            textViewName.setText(Integer.toString(measurementIndex));
        }

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
    }

    /**
     * This gets called when we succesfully take a picture
     * TODO Save a pointer to the image in some way!
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        imageViewMeasurementPhoto.setImageBitmap(bitmap);
    }


}
