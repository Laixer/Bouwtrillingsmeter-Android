package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Measurement;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * This class is used as an adapter for each measurement item in the main measurement listview
 * This class does NOT control the page that opens when you click a measurement within said listview
 */
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

        TextView textViewName = (TextView) view.findViewById(R.id.textViewListMeasurementName);
        ImageView imageViewPhoto = (ImageView) view.findViewById(R.id.imageViewListMeasurementPhoto);

        // Set the name
        textViewName.setText(measurements.get(position).GetName());

        // Set the photo
        // Add an actionlistener to this to attempt to take a photo
        // TODO Remove photo bar at all when none is present????
        Bitmap photo = measurements.get(position).GetPhoto();
        Utility.UpdateScaledPhoto(imageViewPhoto, photo);

        return view;
    }

    /**
     * Forces the adapter to update
     */
    public void OnDatasetChanged() {
        this.notifyDataSetChanged();
    }

}
