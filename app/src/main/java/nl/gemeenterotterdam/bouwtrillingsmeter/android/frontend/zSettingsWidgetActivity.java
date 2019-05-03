package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * TODO Doc
 * TODO Implement non hacky fix for skipping the widget
 */
public class zSettingsWidgetActivity extends AppCompatActivity {

    ViewPager mViewPager;
    zSettingsWidgetSlideAdapter mSlideAdapter;

    private LinearLayout dotsLayout;
    ImageView[] dots;

    int slideCount;
    int questionCount;
    int questionsAnsweredCount;
    boolean[] answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_widget);

        // Calculate variables
        questionCount = Utility.resources.getStringArray(R.array.wizard_question_text_main).length;
        slideCount = questionCount + 1;
        answers = new boolean[questionCount];
        questionsAnsweredCount = 0;

        // Also link the adapter
        mViewPager = findViewById(R.id.viewPagerSettingsWidget);
        mSlideAdapter = new zSettingsWidgetSlideAdapter(this, this, slideCount);
        mViewPager.setAdapter(mSlideAdapter);

        // Viewpager dots
        dotsLayout = (LinearLayout) findViewById(R.id.linearLayoutSettingsWidgetDots);
        setIndicatorDots(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float oneMinusSwipeProgressX, int swipeProgress) {
                /*int desiredPosition = position + 1;
                if (desiredPosition > questionsAnsweredCount - 1) {
                    mViewPager.setCurrentItem(position);
                }*/
            }

            @Override
            public void onPageSelected(int i) {
                setIndicatorDots(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    /**
     * If we have pressed YES or NO
     * This saves the answer
     * This pushes the next slide into your face
     * This updates the indicator dots
     *
     * @param questionIndexFrom The question index
     * @param answer               The answer
     */
    public void onQuestionAnswered(int questionIndexFrom, boolean answer) {
        // Save answer
        answers[questionIndexFrom] = answer;

        // If we are not the last question
        if (questionIndexFrom < questionCount - 1) {
            mViewPager.setCurrentItem(questionIndexFrom + 1);
            setIndicatorDots(questionIndexFrom + 1);

            // If we are the last question, increase the count
            if (questionIndexFrom == questionsAnsweredCount - 1) {
                questionsAnsweredCount += 1;
            }
        }

        // If we are about to enter our summary
        else if (questionIndexFrom == questionCount - 1) {
            SettingsGenerator.overwriteSettingsFromWidget(answers);
            mSlideAdapter.updateFinalPage();
            mViewPager.setCurrentItem(questionIndexFrom + 1);
            setIndicatorDots(questionIndexFrom + 1);
        }
    }

    /**
     * This only closes the widget
     */
    public void onConfirmWidget() {
        finish();
    }

    /**
     * Set the indicator dots.
     *
     * @param currentIndex The current index at which we are present
     */
    private void setIndicatorDots(int currentIndex) {
        if (dotsLayout != null) {
            dotsLayout.removeAllViews();
            dots = new ImageView[slideCount];

            // Fill colors
            int[] drawables = new int[slideCount];
            for (int i = 0; i < slideCount; i++) {
                if (i == currentIndex) {
                    drawables[i] = R.drawable.indicator_dot_active;
                } else if (i < questionsAnsweredCount) {
                    drawables[i] = R.drawable.indicator_dot_inactive;
                } else {
                    drawables[i] = R.drawable.indicator_dot_disabled;
                }
            }

            // Write colors
            for (int i = 0; i < slideCount; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, drawables[i]));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 0, 4, 0);
                dotsLayout.addView(dots[i], params);
            }
        }
    }
}
