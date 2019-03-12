package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This is the page where our determined categories are displayed.
 * The widget is controlled by the {@link SettingsWidgetControl} class.
 * Pressing the 'ok fab' takes us to the {@link MeasuringActivity} page.
 */
public class SettingsWidgetFinalActivity extends AppCompatActivity {

    Activity thisActivity;
    FloatingActionButton fabWidgetFinalConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_widget_final);

        // Set linker
        thisActivity = this;

        // Button to confirm
        fabWidgetFinalConfirm = (FloatingActionButton) findViewById(R.id.fabWidgetFinalConfirm);
        fabWidgetFinalConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsWidgetControl.confirmWidget(thisActivity);
            }
        });
    }
}
