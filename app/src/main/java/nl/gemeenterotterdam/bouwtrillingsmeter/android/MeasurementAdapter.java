package nl.gemeenterotterdam.bouwtrillingsmeter.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.icu.util.Measure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
        }

        return view;
    }
}
