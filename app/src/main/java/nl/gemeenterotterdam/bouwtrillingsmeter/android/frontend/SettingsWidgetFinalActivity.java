package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This is the page where our determined categories are displayed.
 * The widget is controlled by the {@link SettingsPagesControl} class.
 * Pressing the 'ok fab' takes us to the {@link MeasuringActivity} page.
 */
public class SettingsWidgetFinalActivity extends AppCompatActivity {

    Activity thisActivity;
    FloatingActionButton fabWidgetFinalConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_widget_final);

        // Set all text fields
        TextView textViewBuildingCategory = (TextView) findViewById(R.id.textViewWidgetFinalBuildingCategory);
        TextView textViewVibrationCategory = (TextView) findViewById(R.id.textViewWidgetFinalVibrationCategory);
        TextView textViewVibrationSensitive = (TextView) findViewById(R.id.textViewWidgetFinalVibrationSensitive);

        Settings settings = SettingsGenerator.getCurrentSettings();
        textViewBuildingCategory.setText(Utility.getBuildingCategoryString(settings.buildingCategory));
        textViewVibrationCategory.setText(Utility.getVibrationCategoryString(settings.vibrationCategory));
        textViewVibrationSensitive.setText(settings.vibrationSensitive ?
                getResources().getString(R.string.default_yes) : getResources().getString(R.string.default_no));

        // Set linker
        thisActivity = this;

        // Button to confirm
        fabWidgetFinalConfirm = (FloatingActionButton) findViewById(R.id.fabWidgetFinalConfirm);
        fabWidgetFinalConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsPagesControl.confirmWidget(thisActivity);
                finish();
            }
        });
    }

    /**
     * This discards the created {@link nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings} file created by the widget.
     * This calls {@link SettingsPagesControl#discardWidget()}.
     */
    @Override
    public void onBackPressed() {
        SettingsPagesControl.discardWidget();
        finish();
    }
}

