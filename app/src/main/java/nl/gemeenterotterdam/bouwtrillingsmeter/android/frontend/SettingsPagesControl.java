package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings;

/**
 * This class takes us through the widget to determine the settings.
 * We enter this widget from the {@link SettingsPageActivity} page.
 * Pressing the 'ok fab', present on the {@link SettingsWidgetFinalActivity} page takes us to the {@link MeasuringActivity} page.
 * <p>
 * Every widget page is a {@link SettingsWidgetPageActivity}.
 * At the end of the widget the determined categories are displayed via the {@link SettingsWidgetFinalActivity}.
 * <p>
 * This class does NOT call {@link Backend#onGeneratedNewSettings(Settings)}.
 */
public class SettingsPagesControl {

    private static int widgetPageCount;
    private static boolean[] widgetAnswers;
    public static Settings createdSettingsFromWidget = null;

    /**
     * This takes us to the category page activity.
     * This is the entry point of everything that this class controls.
     * This gets called from {@link MainActivity}.
     *
     * @param from The activity from which we call this.
     */
    public static void onClickNewMeasurementFab(Activity from) {
        Backend.onClickCreateNewMeasurement();

        createdSettingsFromWidget = null;

        // If we are in our first visit
        if (GlobalVariables.firstVisit) {
            Intent intentFirstVisitTutorial = new Intent(from.getApplicationContext(), FirstVisitTutorialActivity.class);
            from.startActivity(intentFirstVisitTutorial);
        }

        // If we have already visited before
        else {
            Intent intentCategorySelection = new Intent(from.getApplicationContext(), SettingsPageActivity.class);
            from.startActivity(intentCategorySelection);
        }
    }

    /**
     * This gets called when we confirm our settings.
     * Called from {@link SettingsPageActivity#attemptConfirmChosenCategories(View)}.
     * @param from
     */
    public static void onClickStartMeasurementFab(Activity from) {
        Backend.onClickCompleteSettingsSetup();

        Intent intent = new Intent(from.getApplicationContext(), MeasuringActivity.class);
        from.startActivity(intent);

        // Close our settings tab activity
        from.finish();
    }

    /**
     * Starts the widget
     *
     * @param from The activity from which we are being called
     */
    public static void StartWidget(Activity from) {
        widgetPageCount = from.getResources().getStringArray(R.array.widget_text_main).length;
        widgetAnswers = new boolean[widgetPageCount];
        SetToPage(from, 0);
    }

    /**
     * This completes and closes the widget.
     * This also pushes the new settings onto the UI,
     * by calling {@link SettingsPageActivity#onPushParametersFromWidget(Settings)}.
     * <p>
     *
     * @param from The activity from which we are being called
     */
    public static void confirmWidget(Activity from) {
        SettingsGenerator.overwriteSettingsFromWidget(widgetAnswers);
        createdSettingsFromWidget = SettingsGenerator.getCurrentSettings();

        // Go back to the settings page
        // DO NOT launch a new activity for this!
        Intent intent = new Intent(from.getApplicationContext(), SettingsPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        from.startActivity(intent);

        // Close the previous question
        from.finish();
    }

    /**
     * This gets called when the user clicks a button in the widget
     *
     * @param from            The activity from which we are being called
     * @param widgetPageIndex The page index from which the button was pressed
     * @param yes             True if yes, false if no
     */
    public static void onClickYesNo(Activity from, int widgetPageIndex, boolean yes) {
        widgetAnswers[widgetPageIndex] = yes;

        // If we have completed the widget
        if (widgetPageIndex >= widgetPageCount - 1) {
            Intent intent = new Intent(from.getApplicationContext(), SettingsWidgetFinalActivity.class);
            from.startActivity(intent);
        }

        // If we are still within the pages
        else {
            SetToPage(from, widgetPageIndex + 1);
        }

        // Close the previous question
        from.finish();
    }

    /**
     * This function sets our widget to a certain page by creating a new activity
     *
     * @param from  The activity from which we are being called
     * @param index The page index
     */
    private static void SetToPage(Activity from, int index) {
        Intent intent = new Intent(from.getApplicationContext(), SettingsWidgetPageActivity.class);
        intent.putExtra("nl.gemeenterotterdam.bouwtrillingsmeter.WIDGET_PAGE_INDEX", index);
        from.startActivity(intent);
    }

}
