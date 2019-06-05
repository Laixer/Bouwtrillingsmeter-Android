package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Measurement;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class contains some utility functions used throughout the frontend.
 * <p>
 * The application context pointer is stored as {@link #applicationContext}.
 * The application resources pointer is stored as {@link Resources}.
 * Some scripts that are not {@link android.app.Activity} based need these pointers.
 * <p>
 * Image scaling is also implemented in here, since this is reused throughout multiple scripts.
 */
class Utility {

    // These get set a startup since using these pointers is becoming a hassle
    // TODO Rethink this design pattern
    public static Context applicationContext;
    public static Resources resources;

    private static String[] MONTH_NAMES = {"Januari", "Februari", "Maart", "April", "Mei", "Juni", "Juli", "Augustus", "September", "October", "November", "December"};

    /**
     * Checks if our screen is in landscape or portrait mode
     *
     * @param context The context from which we are checking
     * @return True if portrait, false if landscape
     */
    private static boolean isScreenInPortraitMode(Context context) {
        final int screenOrientation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (screenOrientation) {
            case Surface.ROTATION_0:
                return true;
            case Surface.ROTATION_90:
                return false;
            case Surface.ROTATION_180:
                return true;
            case Surface.ROTATION_270:
                return false;
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
    private static int getScreenWidth(Context context) {
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
    private static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /**
     * Scales a bitmap and pushes it onto an imageview.
     * TODO Remove missing photo bit here
     *
     * @param imageView The imageview to update
     * @param bitmap    The bitmap to scale
     */
    static void updateScaledPhoto(ImageView imageView, Bitmap bitmap) {
        // Get the width/height ratio as a value between 0 and 1
        double ratio = 0;
        if (Utility.isScreenInPortraitMode(applicationContext)) {
            ratio = resources.getInteger(R.integer.measurement_details_photo_portrait);
        } else {
            ratio = resources.getInteger(R.integer.measurement_details_photo_landscape);
        }
        ratio = ratio / 100;

        // Set the pixel values
        int desiredWidth = getScreenWidth(applicationContext);
        int desiredHeight = (int) ((double) (Utility.getScreenHeight(applicationContext) * ratio));
        imageView.setMaxWidth(desiredWidth);
        imageView.setMaxHeight(desiredHeight);

        // Write image or default if bitmap is null
        if (bitmap == null) {
            Drawable drawable = resources.getDrawable(R.drawable.ic_image_not_present);
            imageView.setImageDrawable(drawable);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Gets our color resource
     *
     * @param dimension x=0 y=1 z=2
     * @return The resource id integer
     */
    static int getColorResourceFromDimension(int dimension) {
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

    /**
     * Requests all our permissions.
     * TODO Add error handling
     * TODO Add permission denied handling
     *
     * @param activity The activity from which we call this
     */
    static void askForPermissions(Activity activity) {
        try {
            // Get our permissions
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = packageInfo.requestedPermissions;
            ActivityCompat.requestPermissions(activity, requestedPermissions, 0);
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("Package with name " + activity.getPackageName() + " not found.");
        }
    }

    /**
     * Shows and returns a dialog.
     *
     * @param activity       The activity from which we call this
     * @param dialogResource The dialog resource
     * @param stringResource The string resource for the text to display
     * @return The dialog
     */
    static Dialog showAndGetPopup(Activity activity, int dialogResource, int stringResource) {
        return showAndGetPopup(activity, dialogResource, activity.getResources().getString(stringResource));
    }

    /**
     * Shows and returns a dialog.
     *
     * @param activity       The activity from which we call this
     * @param dialogResource The dialog resource
     * @param string         The string to display
     * @return The dialog
     */
    static Dialog showAndGetPopup(Activity activity, int dialogResource, String string) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View dialogView = inflater.inflate(dialogResource, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setMessage(string);
        final Dialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        // Buttons
        // TODO Kan eleganter
        if (dialogResource == R.layout.alert_dialog_ok) {
            ((Button) dialog.findViewById(R.id.buttonAlertDialogOk)).setOnClickListener((view) -> {
                dialog.dismiss();
            });
        } else if (dialogResource == R.layout.alert_dialog_yes_no) {
            ((Button) dialog.findViewById(R.id.buttonAlertDialogYes)).setOnClickListener((view) -> {
                dialog.dismiss();
            });
            ((Button) dialog.findViewById(R.id.buttonAlertDialogNo)).setOnClickListener((view) -> {
                dialog.dismiss();
            });
        }

        return dialog;
    }

    /**
     * Converts our date object to a nice string for our UI.
     *
     * @param date The date object
     * @return The formatted string
     */
    static String formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(calendar.get(Calendar.DAY_OF_MONTH));
        stringBuilder.append(" ");
        stringBuilder.append(MONTH_NAMES[calendar.get(Calendar.MONTH)]);
        stringBuilder.append(" ");
        stringBuilder.append(calendar.get(Calendar.YEAR));

        return stringBuilder.toString();
    }

    /**
     * Checks if our view is visible
     * @param view The view to check
     * @return True if visible
     */
    static boolean isVisible(final View view) {
        // Edge case
        if (view == null) {
            return false;
        }

        // Edge case
        if (!view.isShown()) {
            return false;
        }

        // Draw rect
        final Rect actualPosition = new Rect();
        view.getGlobalVisibleRect(actualPosition);
        final Rect screen = new Rect(0, 0, getScreenWidth(applicationContext), getScreenHeight(applicationContext));
        return actualPosition.intersect(screen);
    }

    /**
     * Generates a name for our image.
     * This is based on the measurement UID and the
     * current time and date.
     *
     * @param measurement The measurement to which this belongs
     * @return A formatted name
     */
    static String getNameForImage(@NotNull Measurement measurement) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp + "-" + measurement.getUID() + ".png";
    }

}
