package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings;

/**
 * TODO Doc
 */
public class zSettingsWidgetSlideAdapter extends PagerAdapter {

    private zSettingsWidgetActivity parent;
    private int[] layouts;
    private LayoutInflater layoutInflater;
    private Context context;
    private int slideCount;

    private TextView[] textViewsFinalPage;

    /**
     * Constructor for this adapter
     */
    zSettingsWidgetSlideAdapter(Context context, zSettingsWidgetActivity parent, int slideCount) {
        // Save pointer
        this.context = context;
        this.parent = parent;
        this.slideCount = slideCount;

        // Fill layout array
        layouts = new int[slideCount + 1];
        for (int i = 0; i < slideCount - 1; i++) {
            layouts[i] = R.layout.settings_widget_slide;
        }
        layouts[layouts.length - 1] = R.layout.settings_widget_final;

        // Context stuff
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view;

        // Widget page
        if (position < slideCount - 1) {
            // Inflate
            view = layoutInflater.inflate(R.layout.settings_widget_slide, container, false);

            // Get elements
            TextView textViewMain = (TextView) view.findViewById(R.id.textViewSettingsSlideMain);
            TextView textViewExtra = (TextView) view.findViewById(R.id.textViewSettingsSlideExtra);
            Button buttonYes = (Button) view.findViewById(R.id.buttonSettingsSlideYes);
            Button buttonNo = (Button) view.findViewById(R.id.buttonSettingsSlideNo);

            // Assign variables
            textViewMain.setText(Utility.resources.getStringArray(R.array.wizard_question_text_main)[position]);
            textViewExtra.setText(Utility.resources.getStringArray(R.array.wizard_question_text_extra)[position]);

            // Onclick listeners
            buttonYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.onQuestionAnswered(position, true);
                }
            });
            buttonNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.onQuestionAnswered(position, false);
                }
            });
        }

        // If we are the final page
        else {
            // Inflate
            view = layoutInflater.inflate(R.layout.settings_widget_final, container, false);

            // Link elements
            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabWidgetFinalConfirm);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.onConfirmWidget();
                }
            });

            textViewsFinalPage = new TextView[3];
            textViewsFinalPage[0] = (TextView) view.findViewById(R.id.textViewWidgetFinalBuildingCategory);
            textViewsFinalPage[1] = (TextView) view.findViewById(R.id.textViewWidgetFinalVibrationCategory);
            textViewsFinalPage[2] = (TextView) view.findViewById(R.id.textViewWidgetFinalVibrationSensitive);
        }

        // Add and return
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    /**
     * Updates the final page based on our current settings
     */
    public void updateFinalPage() {
        Settings settings = SettingsGenerator.getCurrentSettings();
        int i = 0;
        textViewsFinalPage[0].setText(Utility.resources.getStringArray(R.array.category_dropdown_building)[settings.buildingCategory.ordinal()]);
        textViewsFinalPage[1].setText(Utility.resources.getStringArray(R.array.category_dropdown_vibration)[settings.vibrationCategory.ordinal()]);
        textViewsFinalPage[2].setText(Utility.resources.getString(settings.vibrationSensitive ? R.string.default_yes : R.string.default_no));
    }
}
