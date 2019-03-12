package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BuildingCategory;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.VibrationCategory;

/**
 * TODO Javadoc
 */
public class SettingsPageActivity extends AppCompatActivity {

    Spinner spinnerCategoryBuilding;
    Spinner spinnerCategoryVibration;
    Switch switchVibrationSensitive;
    FloatingActionButton fabCategoryConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        // FAB
        fabCategoryConfirm = (FloatingActionButton) findViewById(R.id.fabCategoryConfirm);
        fabCategoryConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptConfirmChosenCategories(v);
            }
        });
        setFabBackgroundTint(fabCategoryConfirm, false);

        // Spinners
        spinnerCategoryBuilding = (Spinner) findViewById(R.id.spinnerCategoryBuilding);
        spinnerCategoryBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OnAnyItemSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                OnAnyItemSelected();
            }
        });

        spinnerCategoryVibration = (Spinner) findViewById(R.id.spinnerCategoryVibration);
        spinnerCategoryVibration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OnAnyItemSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                OnAnyItemSelected();
            }
        });

        // Switch
        switchVibrationSensitive = (Switch) findViewById(R.id.switchCategoryVibrationSensitive);

        // See if we have any settings created by our widget and push them if so
        if (SettingsPagesControl.createdSettingsFromWidget != null) {
            onPushParametersFromWidget(SettingsPagesControl.createdSettingsFromWidget);
        }
    }

    /**
     * This pushes our parameters from the widget once we confirm the widget.
     * This only works in our {@link #onCreate(Bundle)} method. This has to do
     * with the adapter, see docs.
     *
     * @param settings The generated settings file.
     */
    private void onPushParametersFromWidget(Settings settings) {
        spinnerCategoryBuilding.setSelection(settings.buildingCategory.ordinal(), true);
        spinnerCategoryVibration.setSelection(settings.vibrationCategory.ordinal(), true);
        switchVibrationSensitive.setChecked(settings.vibrationSensitive);
    }

    /**
     * This gets called when the confirming FAB is clicked
     *
     * @param view View used to pass towards the snackbar
     */
    private void attemptConfirmChosenCategories(View view) {
        // Extract
        int buildingIndex = spinnerCategoryBuilding.getSelectedItemPosition();
        int vibrationIndex = spinnerCategoryVibration.getSelectedItemPosition();

        // If we are yet to select a building category
        if (buildingIndex == 0) {
            requireFormCompletion(view, getResources().getString(R.string.category_notyetselected_building));
            return;
        }
        if (vibrationIndex == 0) {
            requireFormCompletion(view, getResources().getString(R.string.category_notyetselected_vibration));
            return;
        }

        // If we have completed our form
        BuildingCategory buildingCategory = BuildingCategory.values()[buildingIndex];
        VibrationCategory vibrationCategory = VibrationCategory.values()[vibrationIndex];
        boolean vibrationSensitive = switchVibrationSensitive.isSelected();
        SettingsGenerator.createSettingsFromCategoryPage(buildingCategory, vibrationCategory, vibrationSensitive);

        // Go
        SettingsPagesControl.onClickStartMeasurementFab(this);
    }

    /**
     * This gets called when the user attempts to complete the form without having selected everything
     *
     * @param message The message to display
     */
    private void requireFormCompletion(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * Starts the widget
     *
     * @param view The view we are in
     */
    public void onClickCategoryIDontKnow(View view) {
        SettingsPagesControl.StartWidget(this);
    }

    /**
     * Controls the look and feel for the fab
     * Also sets it to enabled or disabld
     *
     * @param fab     The floating action button
     * @param enabled True if it should be enabled
     */
    private void setFabBackgroundTint(FloatingActionButton fab, boolean enabled) {
        if (enabled) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_background_disabled)));
        }
    }

    /**
     * Gets called when the user changes something in either of the spinners
     */
    private void OnAnyItemSelected() {
        // Extract
        int buildingIndex = spinnerCategoryBuilding.getSelectedItemPosition();
        int vibrationIndex = spinnerCategoryVibration.getSelectedItemPosition();

        // If we are yet to select a building category
        if (buildingIndex == 0 || vibrationIndex == 0) {
            setFabBackgroundTint(fabCategoryConfirm, false);
        } else {
            setFabBackgroundTint(fabCategoryConfirm, true);
        }
    }
}
