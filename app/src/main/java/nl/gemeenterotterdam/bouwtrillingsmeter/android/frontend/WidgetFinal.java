package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

public class WidgetFinal extends AppCompatActivity {

    Activity thisActivity;
    FloatingActionButton fabWidgetFinalConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_final);

        // Set linker
        thisActivity = this;

        // Button to confirm
        fabWidgetFinalConfirm = (FloatingActionButton) findViewById(R.id.fabWidgetFinalConfirm);
        fabWidgetFinalConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WidgetControl.ConfirmWidget(thisActivity);
            }
        });
    }
}
