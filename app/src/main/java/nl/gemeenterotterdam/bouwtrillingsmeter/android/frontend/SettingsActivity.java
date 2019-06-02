package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.PreferenceManager;

/**
 * Displays our settings menu.
 * TODO Alertdialog netjes
 * TODO Als je de alertdialog wegklikt moet de switch weer uit.
 */
public class SettingsActivity extends AppCompatActivity {

    private Switch switchStreamAllData;
    private Switch switchRoaming;
    private Switch switchPickup;
    private Button buttonShowAdvancedSettings;
    private boolean importingPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Link elements
        switchStreamAllData = (Switch) findViewById(R.id.switchSettingsStreamAllData);
        switchStreamAllData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onChangeStreamAllData(switchStreamAllData.isChecked());
            }
        });

        switchRoaming = (Switch) findViewById(R.id.switchSettingsRoaming);
        switchRoaming.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onChangeRoaming(switchRoaming.isChecked());
            }
        });

        switchPickup = (Switch) findViewById(R.id.switchSettingsPickup);
        switchPickup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onChangePickup(switchPickup.isChecked());
            }
        });

        buttonShowAdvancedSettings = (Button) findViewById(R.id.buttonSettingsAdvancedShow);
        buttonShowAdvancedSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowAdvancedSettings();
            }
        });

        loadCurrentStoredSettings();
    }

    /**
     * Reads all currently stored preferences from our {@link PreferenceManager}.
     */
    private void loadCurrentStoredSettings() {
        importingPreferences = true;

        switchStreamAllData.setChecked(PreferenceManager.readBooleanPreference(R.string.pref_allow_stream_all_data));
        switchRoaming.setChecked(PreferenceManager.readBooleanPreference(R.string.pref_allow_roaming));
        switchPickup.setChecked(PreferenceManager.readBooleanPreference(R.string.pref_use_pickup));

        importingPreferences = false;
    }

    /**
     * Called when we flick the stream all data switch.
     * This shows a popup explaining the involved
     * data risks.
     *
     * @param setTo What the switch is now set to
     */
    private void onChangeStreamAllData(boolean setTo) {
        if (importingPreferences) {
            return;
        }

        if (!setTo) {
            PreferenceManager.writeBooleanPreference(R.string.pref_allow_stream_all_data, false);
        } else {
            Dialog dialog = Utility.showAndGetPopup(this, R.layout.alert_dialog_yes_no, R.string.settings_enable_roaming_alert_dialog);
            // Buttons
            dialog.findViewById(R.id.buttonAlertDialogYes).setOnClickListener((View v) -> {
                PreferenceManager.writeBooleanPreference(R.string.pref_allow_stream_all_data, true);
                dialog.dismiss();
            });
            dialog.findViewById(R.id.buttonAlertDialogNo).setOnClickListener((View v) -> {
                switchStreamAllData.setChecked(false);
                dialog.dismiss();
            });
        }
    }

    /**
     * Called when we flick the roaming switch.
     *
     * @param setTo What the switch is now set to
     */
    private void onChangeRoaming(boolean setTo) {
        if (importingPreferences) {
            return;
        }

        if (!setTo) {
            PreferenceManager.writeBooleanPreference(R.string.pref_allow_roaming, false);
        } else {
            Dialog dialog = Utility.showAndGetPopup(this, R.layout.alert_dialog_yes_no, R.string.settings_enable_roaming_alert_dialog);
            // Buttons
            dialog.findViewById(R.id.buttonAlertDialogYes).setOnClickListener((View v) -> {
                PreferenceManager.writeBooleanPreference(R.string.pref_allow_roaming, true);
                dialog.dismiss();
            });
            dialog.findViewById(R.id.buttonAlertDialogNo).setOnClickListener((View v) -> {
                switchRoaming.setChecked(false);
                dialog.dismiss();
            });
        }
    }

    /**
     * Called when we flick the pickup switch.
     *
     * @param setTo What the switch is now set to
     */
    private void onChangePickup(boolean setTo) {
        if (importingPreferences) {
            return;
        }

        PreferenceManager.writeBooleanPreference(R.string.pref_use_pickup, setTo);
    }

    /**
     * Called when we press the show advanced settings button.
     * This also temporarily disables the button.
     */
    private void onClickShowAdvancedSettings() {
        buttonShowAdvancedSettings.setEnabled(false);
        Intent intent = new Intent(getApplicationContext(), SettingsAdvancedActivity.class);
        startActivity(intent);
    }


    /**
     * Used to enable buttons etc again.
     */
    @Override
    public void onResume() {
        buttonShowAdvancedSettings.setEnabled(true);
        loadCurrentStoredSettings();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
