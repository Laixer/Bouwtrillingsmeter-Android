package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

public class SettingsAdvancedActivity extends AppCompatActivity {

    private Switch switchShowGraphs;
    private Button buttonShowUserUID;

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

        // Call stuff
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

    }
}
