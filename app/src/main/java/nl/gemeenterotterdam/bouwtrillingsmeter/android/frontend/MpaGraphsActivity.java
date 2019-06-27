package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * This holds our graphs including their viewpager.
 * This does not create any graphs, only passes them
 * through to the {@link MpaGraphsSlideAdapter}.
 */
public class MpaGraphsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MpaGraphsSlideAdapter adapter;
    private LinearLayout dotsLayout;
    private ImageView[] dots;

    private static MpaGraphsActivity mpaGraphsActivity;

    /**
     * Called on creation.
     *
     * @param savedInstanceState Saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpa_graphs);

        // Static save
        mpaGraphsActivity = this;

        // Setup dots
        dotsLayout = (LinearLayout) findViewById(R.id.linearLayoutMpaGraphsDots);
        Utility.setIndicatorDots(this, dotsLayout, 0,
                MeasuringActivity.getMpaGraphsControl().getGraphsCount());

        // Setup viewpager
        adapter = new MpaGraphsSlideAdapter(this,
                MeasuringActivity.getMpaGraphsControl().getGraphs());
        viewPager = (ViewPager) findViewById(R.id.viewPagerMpaGraphs);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(10);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                Utility.setIndicatorDots(getApplicationContext(), dotsLayout, i,
                        MeasuringActivity.getMpaGraphsControl().getGraphsCount());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    /**
     * Override the back button pressed to only hide this activity.
     * It will not close so we can reopen it later.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MeasuringActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * Force this activity to end. This does
     * nothing if this activity was never
     * started in the first place.
     */
    public static void forceFinish() {
        if (mpaGraphsActivity != null) {
            mpaGraphsActivity.finish();
            mpaGraphsActivity = null;
        }
    }
}
