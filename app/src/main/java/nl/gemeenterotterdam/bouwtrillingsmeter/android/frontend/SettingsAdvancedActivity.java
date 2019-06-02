package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Backend;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.PreferenceManager;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.StorageControl;

public class SettingsAdvancedActivity extends AppCompatActivity {

    private Switch switchShowGraphs;
    private Button buttonShowUserUID;
    private Button buttonClearAppData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_advanced);

        // Link elements
        switchShowGraphs = (Switch) findViewById(R.id.switchSettingsAdvancedShowGraphs);
        switchShowGraphs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onChangeShowGraphs(switchShowGraphs.isChecked());
            }
        });

        buttonShowUserUID = (Button) findViewById(R.id.buttonSettingsAdvancedShowUserUID);
        buttonShowUserUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowUserUID();
            }
        });

        buttonClearAppData = (Button) findViewById(R.id.buttonSettingsAdvancedClearAppData);
        buttonClearAppData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickClearApplicationData();
            }
        });

        loadCurrentStoredSettings();
    }

    /**
     * Reads all currently stored preferences from our {@link PreferenceManager}.
     */
    private void loadCurrentStoredSettings() {
        switchShowGraphs.setChecked(PreferenceManager.readBooleanPreference(R.string.pref_show_graphs));
    }

    /**
     * Called when we flick the graph switch.
     *
     * @param setTo What the switch is now set to
     */
    private void onChangeShowGraphs(boolean setTo) {
        PreferenceManager.writeBooleanPreference(R.string.pref_show_graphs, setTo);
    }

    /**
     * When we click the show user UID button.
     * TODO Implement
     */
    private void onClickShowUserUID() {
        String uid = Backend.getUserUID();
        Dialog dialog = Utility.showAndGetPopup(this, R.layout.alert_dialog_ok, uid);
        dialog.findViewById(R.id.buttonAlertDialogOk).setOnClickListener((View v) -> {
            PreferenceManager.writeBooleanPreference(R.string.pref_allow_stream_all_data, true);
            dialog.dismiss();
        });
    }

    /**
     * This clears our preferences.
     * This removes all our storage.
     */
    private void onClickClearApplicationData() {
        Dialog dialog = Utility.showAndGetPopup(this, R.layout.alert_dialog_ok, R.string.settings_advanced_delete_application_data_alert_dialog);
        dialog.findViewById(R.id.buttonAlertDialogOk).setOnClickListener((View v) -> {
            dialog.dismiss();
        });

        PreferenceManager.clearAllPreferences();
        StorageControl.removeAllInternalStorage();
        loadCurrentStoredSettings();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
