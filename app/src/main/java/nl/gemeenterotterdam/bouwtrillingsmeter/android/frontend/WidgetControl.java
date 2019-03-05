package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.content.Intent;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * This class takes us through the widget to determine the settings.
 * We enter this widget from the {@link CategoryPageActivity} page.
 * Pressing the 'ok fab', present on the {@link WidgetFinalActivity} page takes us to the {@link MeasuringActivity} page.
 * <p>
 * Every widget page is a {@link WidgetPageActivity}.
 * At the end of the widget the determined categories are displayed via the {@link WidgetFinalActivity}.
 */
public class WidgetControl {

    private static int widgetPageCount;
    private static boolean[] results;

    /**
     * Starts the widget
     *
     * @param from The activity from which we are being called
     */
    public static void StartWidget(Activity from) {
        widgetPageCount = from.getResources().getStringArray(R.array.widget_text_main).length;
        results = new boolean[widgetPageCount];
        SetToPage(from, 0);
    }

    /**
     * This completes and closes the widget
     * TODO Disable back button flowing back into the widget
     *
     * @param from The activity from which we are being called
     */
    public static void ConfirmWidget(Activity from) {

    }

    /**
     * This gets called when the user clicks a button in the widget
     *
     * @param from            The activity from which we are being called
     * @param widgetPageIndex The page index from which the button was pressed
     * @param yes             True if yes, false if no
     */
    public static void OnClickYesNo(Activity from, int widgetPageIndex, boolean yes) {
        results[widgetPageIndex] = yes;

        // If we have completed the widget
        if (widgetPageIndex >= widgetPageCount - 1) {
            Intent intent = new Intent(from.getApplicationContext(), WidgetFinalActivity.class);
            from.startActivity(intent);
        }

        // If we are still within the pages
        else {
            SetToPage(from, widgetPageIndex + 1);
        }

    }

    /**
     * This function sets our widget to a certain page by creating a new activity
     *
     * @param from  The activity from which we are being called
     * @param index The page index
     */
    private static void SetToPage(Activity from, int index) {
        Intent intent = new Intent(from.getApplicationContext(), WidgetPageActivity.class);
        intent.putExtra("nl.gemeenterotterdam.bouwtrillingsmeter.WIDGET_PAGE_INDEX", index);
        from.startActivity(intent);
    }

}
