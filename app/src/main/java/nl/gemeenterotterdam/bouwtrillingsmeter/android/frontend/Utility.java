package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.ImageView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BuildingCategory;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.VibrationCategory;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class contains some utility functions used throughout the frontend.
 * <p>
 * The application context pointer is stored as {@link #ApplicationContext}.
 * The application resources pointer is stored as {@link Resources}.
 * Some scripts that are not {@link android.app.Activity} based need these pointers.
 * <p>
 * Image scaling is also implemented in here, since this is reused throughout multiple scripts.
 */
public class Utility {

    // These get set a startup since using these pointers is becoming a hassle
    // TODO Rethink this design pattern
    public static Context ApplicationContext;
    public static Resources Resources;

    /**
     * Checks if our screen is in landscape or portrait mode
     *
     * @param context The context from which we are checking
     * @return True if portrait, false if landscape
     */
    public static boolean IsScreenInPortraitMode(Context context) {
        final int screenOrientation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (screenOrientation) {
            case Surface.ROTATION_0:
                return true;
            case Surface.ROTATION_90:
                return false;
            case Surface.ROTATION_180:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns the screen width in pixels
     *
     * @param context The context from which we are checking
     * @return The screen width in pixels
     */
    public static int GetScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * Returns the screen height in pixels
     *
     * @param context The context from which we are checking
     * @return The screen height in pixels
     */
    public static int GetScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /**
     * Scales a bitmap and pushes it onto an imageview
     *
     * @param imageView The imageview to update
     * @param bitmap    The bitmap to scale
     */
    public static void UpdateScaledPhoto(ImageView imageView, Bitmap bitmap) {
        // If we have no bitmap we call the default bitmap function
        if (bitmap == null) {
            UpdateScaledPhotoMissing(imageView);
            return;
        }

        // Get the ratio as a value between 0 and 1
        double ratio = 0;
        if (Utility.IsScreenInPortraitMode(ApplicationContext)) {
            ratio = Resources.getInteger(R.integer.measurement_details_photo_portrait);
        } else {
            ratio = Resources.getInteger(R.integer.measurement_details_photo_landscape);
        }
        ratio = ratio / 100;

        // Set the pixel values
        int desiredWidth = GetScreenWidth(ApplicationContext);
        int desiredHeight = (int) ((double) (Utility.GetScreenHeight(ApplicationContext) * ratio));
        imageView.setMaxHeight(desiredHeight);

        // Scale image
        double ratioBitmap = (double) bitmap.getHeight() / (double) bitmap.getWidth();
        int bitmapHeight = (int) (desiredWidth * ratioBitmap);
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, bitmapHeight, false);
        imageView.setImageBitmap(newBitmap);
    }

    /**
     * Sets the image not present placeholder as a bitmap
     *
     * @param imageView The image view to update
     */
    public static void UpdateScaledPhotoMissing(ImageView imageView) {
        Drawable drawable = Resources.getDrawable(R.drawable.ic_image_not_present);
        imageView.setImageDrawable(drawable);
    }

    /**
     * Transform our {@link BuildingCategory} enum to the correct text.
     *
     * @param buildingCategory
     * @return Our string according to the resources: {@link R.array#category_dropdown_building}.
     */
    public static String getBuildingCategoryString(BuildingCategory buildingCategory) {
        return Utility.Resources.getStringArray(R.array.category_dropdown_building)[buildingCategory.ordinal()];
    }

    /**
     * Transform our {@link VibrationCategory} enum to the correct text.
     *
     * @param vibrationCategory
     * @return Our string according to the resources: {@link R.array#category_dropdown_vibration}.
     */
    public static String getVibrationCategoryString(VibrationCategory vibrationCategory) {
        return Utility.Resources.getStringArray(R.array.category_dropdown_vibration)[vibrationCategory.ordinal()];
    }

    /**
     * Gets our color resource
     *
     * @param dimension x=0 y=1 z=2
     * @return The resource id integer
     */
    public static int getColorResourceFromDimension(int dimension) {
        switch (dimension) {
            case 0:
                return R.color.graph_series_color_x;
            case 1:
                return R.color.graph_series_color_y;
            case 2:
                return R.color.graph_series_color_z;
        }
        return -1;
    }

}
