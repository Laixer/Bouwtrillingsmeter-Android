package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;

/**
 * This activity shows all the graphs.
 */
public class GraphsActivity extends AppCompatActivity {

    ViewPager viewPager;
    private Graph[] graphs;
    private GraphSlideAdapter graphSlideAdapter;

    private LinearLayout dotsLayout;
    private ImageView[] dots;

    Button buttonTutorialSkip;
    Button buttonTutorialNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        // TODO Debug this creates graphs
        graphs = new Graph[5];
        graphs[0] = new Graph("Graph 1!","Horizontal","Vertical");
        graphs[1] = new Graph("Graph 2!","Horizontal","Vertical");
        graphs[2] = new Graph("Graph 3!","Horizontal","Vertical");
        graphs[3] = new Graph("Graph 4!","Horizontal","Vertical");
        graphs[4] = new Graph("Graph 5!","Horizontal","Vertical");

        // Viewpager for the tutorial
        // Also link the adapter
        viewPager = findViewById(R.id.viewPagerGraphs);
        graphSlideAdapter = new GraphSlideAdapter(this, graphs);
        viewPager.setAdapter(graphSlideAdapter);

        // Viewpager dots
        dotsLayout = (LinearLayout) findViewById(R.id.linearLayoutGraphsDots);
        setIndicatorDots(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                setIndicatorDots(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });
    }

    /**
     * Set the indicator dots.
     *
     * @param currentIndex The current index at which we are present
     */
    private void setIndicatorDots(int currentIndex) {
        if (dotsLayout != null) {
            dotsLayout.removeAllViews();
            dots = new ImageView[graphs.length];

            for (int i = 0; i < graphs.length; i++) {
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

}
