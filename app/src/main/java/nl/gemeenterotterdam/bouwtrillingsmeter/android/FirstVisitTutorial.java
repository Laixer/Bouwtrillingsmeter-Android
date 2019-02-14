package nl.gemeenterotterdam.bouwtrillingsmeter.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstVisitTutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_visit_tutorial);

        // Finish button
        // This relaunches the main activity
        Button buttonFinishTutorail = (Button) findViewById(R.id.buttonTutorialFinish);
        buttonFinishTutorail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.firstVisit = false;
                Intent intentFinishTutorial = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentFinishTutorial);
            }
        });
    }
}
