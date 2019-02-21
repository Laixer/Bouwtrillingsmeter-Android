package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BuildingCategory;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.SettingsControl;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.VibrationCategory;

public class CategoryPage extends AppCompatActivity {

    Spinner spinnerCategoryBuilding;
    Spinner spinnerCategoryVibration;
    Switch switchCategoryVibrationSensitive;
    FloatingActionButton fabCategoryConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);

        // FAB
        fabCategoryConfirm = (FloatingActionButton) findViewById(R.id.fabCategoryConfirm);
        fabCategoryConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptConfirmChosenCategories(v);
            }
        });
        fabCategoryConfirm.setEnabled(false);

        // Items
        spinnerCategoryBuilding = (Spinner) findViewById(R.id.spinnerCategoryBuilding);
        spinnerCategoryVibration = (Spinner) findViewById(R.id.spinnerCategoryVibration);
        switchCategoryVibrationSensitive = (Switch) findViewById(R.id.switchCategoryVibrationSensitive);
    }

    /**
     * This gets called when the confirming FAB is clicked
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
        boolean vibrationSensitive = switchCategoryVibrationSensitive.isSelected();
        SettingsControl.CreateSettingsFromCategoryPage(buildingCategory, vibrationCategory, vibrationSensitive);

        // Create a new intent
        Intent intent = new Intent(getApplicationContext(), Measuring.class);
        startActivity(intent);
    }

    /**
     * This gets called when the user attempts to complete the form without having selected everything
     * @param message The message to display
     */
    private void requireFormCompletion(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * Starts the widget
     * @param view The view we are in
     */
    public void onClickCategoryIDontKnow(View view) {
        WidgetControl.StartWidget(this);
    }
}
