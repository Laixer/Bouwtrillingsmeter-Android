package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class represents a single widget page.
 * These pages are handled by the {@link WidgetControl} class.
 * The displayed text is taken from our application {@link android.content.res.Resources}.
 */
public class WidgetPageActivity extends AppCompatActivity {

    private int widgetPageIndex = -1;
    private Activity thisActivity;
    private TextView textViewWidgetMain;
    private TextView textViewWidgetExtra;
    private Button buttonWidgetYes;
    private Button buttonWidgetNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_page);

        thisActivity = this;

        // Extract our index
        Intent intent = getIntent();
        widgetPageIndex = intent.getIntExtra("nl.gemeenterotterdam.bouwtrillingsmeter.WIDGET_PAGE_INDEX", -1);

        if (widgetPageIndex > -1) {
            // Text view main
            textViewWidgetMain = (TextView) findViewById(R.id.textViewWidgetMain);
            textViewWidgetMain.setText(getResources().getStringArray(R.array.widget_text_main)[widgetPageIndex]);

            // Text view extra
            textViewWidgetExtra = (TextView) findViewById(R.id.textViewWidgetExtra);
            textViewWidgetExtra.setText(getResources().getStringArray(R.array.widget_text_extra)[widgetPageIndex]);
        }

        // Button yes
        buttonWidgetYes = (Button) findViewById(R.id.buttonWidgetYes);
        buttonWidgetYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WidgetControl.OnClickYesNo(thisActivity, widgetPageIndex, true);
            }
        });

        // Button no
        buttonWidgetNo = (Button) findViewById(R.id.buttonWidgetNo);
        buttonWidgetNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WidgetControl.OnClickYesNo(thisActivity, widgetPageIndex, false);
            }
        });

    }
}
