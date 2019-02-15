package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

public class FirstVisitTutorial extends AppCompatActivity {

    ViewPager viewPager;
    private int[] tutorialSlides = {
            R.layout.first_visit_slide_1,
            R.layout.first_visit_slide_2,
            R.layout.first_visit_slide_3
    };
    private FirstVisitSlideAdapter firstVisitSlideAdapter;

    private LinearLayout dotsLayout;
    private ImageView[] dots;

    Button buttonTutorialSkip;
    Button buttonTutorialNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for our first visit
        if (new PreferenceManager(this).checkPreference()) {
            onCompleteTutorial();
        }

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

        // Viewpager dots
        dotsLayout = (LinearLayout) findViewById(R.id.linearLayoutTutorialDots);
        setIndicatorDots(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                // If we have reached the last slide
                if (i == tutorialSlides.length - 1) {
                    buttonTutorialNext.setText("Start");
                    buttonTutorialSkip.setVisibility(View.INVISIBLE);
                } else {
                    buttonTutorialNext.setText("Next");
                    buttonTutorialSkip.setVisibility(View.VISIBLE);
                }

                // Set the dots
                setIndicatorDots(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });

        // Button to go to next item
        buttonTutorialNext = (Button) findViewById(R.id.buttonTutorialNext);
        buttonTutorialNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextSlide = viewPager.getCurrentItem() + 1;
                if (nextSlide < tutorialSlides.length) {
                    viewPager.setCurrentItem(nextSlide);
                } else {
                    onCompleteTutorial();
                }
            }
        });

        // Button to skip
        buttonTutorialSkip = (Button) findViewById(R.id.buttonTutorialSkip);
        buttonTutorialSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCompleteTutorial();
            }
        });
    }

    /**
     * Set the indicator dots
     * @param currentIndex The current index at which we are present
     */
    private void setIndicatorDots(int currentIndex) {
        if (dotsLayout != null) {
            dotsLayout.removeAllViews();
            dots = new ImageView[tutorialSlides.length];

            for (int i = 0; i < tutorialSlides.length; i++) {
                dots[i] = new ImageView(this);
                if (i == currentIndex) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_dot_active));
                } else {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_dot_inactive));
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 0, 4, 0);
                dotsLayout.addView(dots[i], params);
            }
        }


    }

    /**
     * This gets called when we skip, finish or swipe out of our tutorial
     * This also saves that we have seen the tutorial at least once
     */
    private void onCompleteTutorial() {
        new PreferenceManager(this).writePreferences();
        Intent intentStartMeasuring = new Intent(getApplicationContext(), Measuring.class);
        startActivity(intentStartMeasuring);
    }
}