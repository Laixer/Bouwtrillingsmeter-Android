package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

public class WidgetFinal extends AppCompatActivity {

    Activity thisActivity;
    Button buttonWidgetFinalConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_final);

        // Set linker
        thisActivity = this;

        // Button to confirm
        buttonWidgetFinalConfirm = (Button) findViewById(R.id.fabWidgetFinalConfirm);
        buttonWidgetFinalConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WidgetControl.ConfirmWidget(thisActivity);
            }
        });
    }
}
