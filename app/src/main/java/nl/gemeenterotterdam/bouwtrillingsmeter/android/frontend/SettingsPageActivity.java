package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BuildingCategory;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.ConstantsLimits;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.VibrationCategory;

/**
 * TODO Javadoc
 */
public class SettingsPageActivity extends AppCompatActivity {

    private Spinner spinnerCategoryBuilding;
    private Spinner spinnerCategoryVibration;
    private Switch switchVibrationSensitive;
    private FloatingActionButton fabCategoryConfirm;
    private Button buttonIDontKnow;

    private static Settings settings;

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

        // Button
        buttonIDontKnow = (Button) findViewById(R.id.buttonSettingsIDontKnow);
        buttonIDontKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCategoryIDontKnow();
            }
        });
    }

    /**
     * This pushes our parameters from the widget once we confirm the widget.
     */
    private void pushCurrentSettingsToFields() {
        if (settings != null) {
            spinnerCategoryBuilding.setSelection(settings.getBuildingCategory().ordinal(), true);
            spinnerCategoryVibration.setSelection(settings.getVibrationCategory().ordinal(), true);
            switchVibrationSensitive.setChecked(settings.isVibrationSensitive());
        }
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

        // If we don't have the correct location permissions
        if (!ConstantsLimits.hasPermissionToFetchLocation(this)) {
            Utility.askForPermissions(this);
            return;
        }

        // If location is enabled
        if (!ConstantsLimits.isLocationEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setMessage(getResources().getString(R.string.alert_dialog_enable_location));
            builder.setPositiveButton(getResources().getString(R.string.default_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
            return;
        }

        // Temporarily disable the FAB
        fabCategoryConfirm.setEnabled(false);

        // If we have completed our form
        BuildingCategory buildingCategory = BuildingCategory.values()[buildingIndex];
        VibrationCategory vibrationCategory = VibrationCategory.values()[vibrationIndex];
        boolean vibrationSensitive = switchVibrationSensitive.isSelected();
        settings = new Settings(buildingCategory, vibrationCategory, vibrationSensitive);
        Backend.onGeneratedNewSettings(settings);

        // Go and remove this from stack
        Backend.onClickCompleteSettingsSetup();
        Intent intent = new Intent(getApplicationContext(), MeasuringActivity.class);
        startActivity(intent);
        finish();
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
     */
    private void onClickCategoryIDontKnow() {
        // Temporarily disable button
        buttonIDontKnow.setEnabled(false);

        Intent intent = new Intent(getApplicationContext(), SettingsWizardActivity.class);
        startActivity(intent);
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

    /**
     * When we successfully exit the wizard.
     * @param settings The created and already validated settings file
     */
    public static void onWizardCreatedValidSettings(Settings settings) {
        SettingsPageActivity.settings = settings;
    }

    /**
     * This gets called when we press the back button.
     */
    @Override
    public void onBackPressed() {
        Backend.onPressedBackButton();
        finish();
    }

    /**
     * Check for any widget exported settings when we get back to this activity,
     */
    @Override
    public void onResume() {
        super.onResume();

        // Push settings to fields
        pushCurrentSettingsToFields();

        // Enable fab
        fabCategoryConfirm.setEnabled(true);
        buttonIDontKnow.setEnabled(true);
    }

    /**
     * Override to prevent memory leak.
     */
    @Override
    public void finish() {
        super.finish();
        settings = null;
    }
}
