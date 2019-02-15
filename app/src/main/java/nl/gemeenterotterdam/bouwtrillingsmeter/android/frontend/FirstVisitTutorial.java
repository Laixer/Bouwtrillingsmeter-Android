package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

public class FirstVisitTutorial extends AppCompatActivity {

    ViewPager viewPager;
    private int[] tutorialSlides = {
            R.layout.first_visit_slide_1,
            R.layout.first_visit_slide_2,
            R.layout.first_visit_slide_3
    };
    private FirstVisitSlideAdapter firstVisitSlideAdapter;
    Button buttonFinishTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_visit_tutorial);

        // This finishes the tutorial
        // TODO Implement a way to skip the tutorial?
//        buttonFinishTutorial = (Button) findViewById(R.id.buttonTutorialFinish);
//        buttonFinishTutorial.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GlobalVariables.firstVisit = false;
//                Intent intentFinishTutorial = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intentFinishTutorial);
//            }
//        });

        // Viewpager for the tutorial
        // Also link the adapter
        viewPager = findViewById(R.id.viewPagerTutorial);
        firstVisitSlideAdapter = new FirstVisitSlideAdapter(this, tutorialSlides);
        viewPager.setAdapter(firstVisitSlideAdapter);
    }

}