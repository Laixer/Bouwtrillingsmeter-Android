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

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Measurement;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class is used as an adapter for each measurement
 * item in the main measurement listview displayd on our
 * {@link MainActivity}. This class does NOT control the
 * page that opens when you click a measurement within
 * said listview. This is done by {@link DetailsActivity}.
 */
public class MainActivityMeasurementListAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private ArrayList<Measurement> measurements;

    /**
     * Class constructor for our list view controller
     *
     * @param context      Our context, usually referred to as "this"
     * @param measurements The pointer to the measurements list
     */
    MainActivityMeasurementListAdapter(Context context, ArrayList<Measurement> measurements) {
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
        TextView textViewDate = (TextView) view.findViewById(R.id.textViewListMeasurementDate);
        ImageView imageViewPhoto = (ImageView) view.findViewById(R.id.imageViewListMeasurementPhoto);

        // Set the variables
        textViewName.setText(measurements.get(position).getName());
        textViewDate.setText(Utility.formatMeasurementDateTime(measurements.get(position)));

        // Set the photo
        // Add an actionlistener to this to attempt to take a photo
        // TODO ALpha
        if (!Utility.isAlpha()) {
            Bitmap photo = measurements.get(position).getBitmap();
            if (photo == null) {
                imageViewPhoto.getLayoutParams().height = 0;
                imageViewPhoto.requestLayout();
            } else {
                Utility.updateScaledPhoto(imageViewPhoto, photo);
            }
        } else {
            imageViewPhoto.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Forces the adapter to update
     */
    void onDataSetChanged() {
        this.notifyDataSetChanged();
    }

}