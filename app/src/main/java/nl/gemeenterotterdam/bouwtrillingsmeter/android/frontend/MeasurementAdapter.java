package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Measurement;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

public class MeasurementAdapter extends BaseAdapter {

    LayoutInflater mLayoutInflater;
    ArrayList<Measurement> measurements;

    /**
     * Class constructor for our list view controller
     * @param context Our context, usually referred to as "this"
     * @param measurements The pointer to the measurements list
     */
    public MeasurementAdapter(Context context, ArrayList<Measurement> measurements) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.measurements = measurements;
    }

    @Override
    public int getCount() {
        return measurements.size();
    }

    @Override
    public Object getItem(int position) {
        return measurements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.list_measurements_details, null);

        TextView textViewName = (TextView) view.findViewById(R.id.textViewName);
        ImageView imageViewPhoto = (ImageView) view.findViewById(R.id.imageViewPhoto);

        textViewName.setText(measurements.get(position).GetName());
        Bitmap photo = measurements.get(position).GetPhoto();
        if (photo != null) {
            // TODO Implement scaling, else we crash our app
            imageViewPhoto.setImageBitmap(photo);
        } else {
            imageViewPhoto.setMaxHeight(0);
        }

        return view;
    }

    /**
     * Returns the corresponding resource index to our measurement image
     * TODO Implement this so that specific images get returned too
     * @param measurementIndex
     * @return The corresponding resource index for the image
     */
    private int getImage(int measurementIndex) {
        return R.drawable.image_not_present;
    }

    /**
     * Scales an image for us to fit an imageview
     * @param imageView The imageview on which we project our image
     * @param imageIndex The resource based index of the image
     */
    private void scaleImage(ImageView imageView, int imageIndex) {
//        Display screen = getWindowManager().getDefaultDisplay();
//        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getResources(), imageIndex, options);     // Decode the image to save performance
//
//        int widthImage = options.outWidth;
//        int widthScreen = screen.getWidth();
//        if (widthImage > widthScreen) {
//            int ratio = Math.round( (float)widthImage / (float)widthScreen);
//            options.inSampleSize = ratio;                               // Sets the scale factor for our bitmap that we are about to draw
//        }
//
//        // Create a scaled image and put it in the image view
//        options.inJustDecodeBounds = false;
//        Bitmap bitmapScaled = BitmapFactory.decodeResource(getResources(), imageIndex, options);
//        imageView.setImageBitmap(bitmapScaled);
    }
}
