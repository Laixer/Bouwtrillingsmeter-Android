package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.content.Intent;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Type;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.BuildingCategory;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.Settings;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.backend.VibrationCategory;

/**
 * This holds our settings wizard.
 */
public class SettingsWizardActivity extends AppCompatActivity {

    private ConstraintLayout layoutQuestion;
    private ConstraintLayout layoutFinish;

    // Buttons
    private Button buttonYes;
    private Button buttonNo;
    private Button buttonTryAgain;

    // Question
    private TextView textViewMain;
    private TextView textViewExtra;

    // Finish
    private TextView textViewTop;
    private TextView textViewBuildingCategory;
    private TextView textViewVibrationCategory;
    private TextView textViewVibrationSensitive;
    private TextView textViewStaticBuildingCategory;
    private TextView textViewStaticVibrationCategory;
    private TextView textViewStaticVibrationSensitive;

    private SettingsWizard settingsWizard;
    private Question currentQuestion;

    // Settings object and variables
    // These are static because of rotation
    private static BuildingCategory buildingCategory = BuildingCategory.NONE;
    private static VibrationCategory vibrationCategory = VibrationCategory.NONE;
    private static Boolean vibrationSensitive = null;
    private static Settings settings;

    /**
     * Gets called when we launch this activity.
     *
     * @param savedInstanceState Not used in this case
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Create
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_wizard);

        // Link all elements
        layoutQuestion = (ConstraintLayout) findViewById(R.id.layoutWidgetQuestions);
        layoutFinish = (ConstraintLayout) findViewById(R.id.layoutWidgetCompleted);
        ((FloatingActionButton) findViewById(R.id.fabWizardConfirm)).setOnClickListener((View v) -> {
                if (settings != null && settings.isValid()) {
                    SettingsPageActivity.onWizardCreatedValidSettings(settings);
                    finish();
                }
        });

        // Question
        textViewMain = (TextView) findViewById(R.id.textViewWizardMain);
        textViewExtra = (TextView) findViewById(R.id.textViewWizardExtra);
        buttonYes = (Button) findViewById(R.id.buttonWizardYes);
        buttonYes.setOnClickListener((View v) -> {
                onAnswered(true);
        });
        buttonNo = (Button) findViewById(R.id.buttonWizardNo);
        buttonNo.setOnClickListener((View v) -> {
                onAnswered(false);
        });

        // Finish
        textViewTop = (TextView) findViewById(R.id.textViewWizardTop);
        textViewBuildingCategory = (TextView) findViewById(R.id.textViewWizardBuildingCategory);
        textViewVibrationCategory = (TextView) findViewById(R.id.textViewWizardVibrationCategory);
        textViewVibrationSensitive = (TextView) findViewById(R.id.textViewWizardVibrationSensitive);
        textViewStaticBuildingCategory = (TextView) findViewById(R.id.textViewWizardStaticBuildingCategory);
        textViewStaticVibrationCategory = (TextView) findViewById(R.id.textViewWizardStaticVibrationCategory);
        textViewStaticVibrationSensitive = (TextView) findViewById(R.id.textViewWizardStaticVibrationSensitive);

        // Setup wizard
        settingsWizard = createQuestionWizard();
        currentQuestion = settingsWizard.getStartQuestion();
        buttonTryAgain = (Button) findViewById(R.id.buttonWizardTryAgain);
        buttonTryAgain.setOnClickListener((View v) -> {
                onClickTryAgain();
        });

        // Change layout
        switchLayout(false);
        pushQuestionToScreen(currentQuestion);
    }

    /**
     * This creates our question wizard based on our flowchart.
     */
    private SettingsWizard createQuestionWizard() {
        // Create the questions
        Question[] questions = new Question[10];
        for (int i = 0; i < questions.length; i++) {
            questions[i] = new Question(i);
        }

        // Create the outcomes
        Outcome[] outcomes = new Outcome[11];
        outcomes[0] = new Outcome<BuildingCategory>(0, BuildingCategory.class, BuildingCategory.NONE);
        outcomes[1] = new Outcome<BuildingCategory>(1, BuildingCategory.class, BuildingCategory.CATEGORY_3);
        outcomes[2] = new Outcome<BuildingCategory>(2, BuildingCategory.class, BuildingCategory.NONE);
        outcomes[3] = new Outcome<BuildingCategory>(3, BuildingCategory.class, BuildingCategory.CATEGORY_1);
        outcomes[4] = new Outcome<BuildingCategory>(4, BuildingCategory.class, BuildingCategory.CATEGORY_2);
        outcomes[5] = new Outcome<VibrationCategory>(5, VibrationCategory.class, VibrationCategory.NONE);
        outcomes[6] = new Outcome<VibrationCategory>(6, VibrationCategory.class, VibrationCategory.SHORT);
        outcomes[7] = new Outcome<VibrationCategory>(7, VibrationCategory.class, VibrationCategory.SHORT_REPEATED);
        outcomes[8] = new Outcome<VibrationCategory>(8, VibrationCategory.class, VibrationCategory.CONTINUOUS);
        outcomes[9] = new Outcome<Boolean>(9, Boolean.class, false);
        outcomes[10] = new Outcome<Boolean>(10, Boolean.class, true);

        // Create all the edges, first all questions
        questions[0].addEdge(new Edge(true, questions[0], questions[3]));
        questions[0].addEdge(new Edge(false, questions[0], questions[1]));

        questions[1].addEdge(new Edge(true, questions[1], questions[4]));
        questions[1].addEdge(new Edge(false, questions[1], questions[2]));

        questions[2].addEdge(new Edge(true, questions[2], outcomes[0]));
        questions[2].addEdge(new Edge(false, questions[2], outcomes[2]));

        questions[3].addEdge(new Edge(true, questions[3], outcomes[1]));
        questions[3].addEdge(new Edge(false, questions[3], outcomes[3]));

        questions[4].addEdge(new Edge(true, questions[4], outcomes[1]));
        questions[4].addEdge(new Edge(false, questions[4], questions[5]));

        questions[5].addEdge(new Edge(true, questions[5], outcomes[1]));
        questions[5].addEdge(new Edge(false, questions[5], outcomes[4]));

        questions[6].addEdge(new Edge(true, questions[6], outcomes[6]));
        questions[6].addEdge(new Edge(false, questions[6], questions[7]));

        questions[7].addEdge(new Edge(true, questions[7], outcomes[7]));
        questions[7].addEdge(new Edge(false, questions[7], questions[8]));

        questions[8].addEdge(new Edge(true, questions[8], outcomes[8]));
        questions[8].addEdge(new Edge(false, questions[8], outcomes[5]));

        questions[9].addEdge(new Edge(true, questions[9], outcomes[10]));
        questions[9].addEdge(new Edge(false, questions[9], outcomes[9]));

        // Then all outcomes
        outcomes[0].setNextQuestion(null);
        outcomes[1].setNextQuestion(questions[6]);
        outcomes[2].setNextQuestion(null);
        outcomes[3].setNextQuestion(questions[6]);
        outcomes[4].setNextQuestion(questions[6]);
        outcomes[5].setNextQuestion(null);
        outcomes[6].setNextQuestion(questions[9]);
        outcomes[7].setNextQuestion(questions[9]);
        outcomes[8].setNextQuestion(questions[9]);
        outcomes[9].setNextQuestion(null);
        outcomes[10].setNextQuestion(null);

        return new SettingsWizard(questions[0]);
    }

    /**
     * Either shows the question layout or the finish layout.
     * @param finished True if we are in the finish tab.
     */
    private void switchLayout(boolean finished) {
        if (finished) {
            layoutQuestion.setVisibility(View.GONE);
            layoutFinish.setVisibility(View.VISIBLE);
        } else {
            layoutQuestion.setVisibility(View.VISIBLE);
            layoutFinish.setVisibility(View.GONE);
        }
    }

    /**
     * This pushes a question to the screen.
     *
     * @param question The question to push
     */
    private void pushQuestionToScreen(Question question) {
        if (question != currentQuestion) {
            System.out.println("Set the question on screen as current question first!");
            return;
        }

        textViewMain.setText(getResources().getStringArray(R.array.wizard_question_text_main)[question.getIndex()]);
        textViewExtra.setText(getResources().getStringArray(R.array.wizard_question_text_extra)[question.getIndex()]);
    }

    /**
     * Gets called when one of the buttons is pressed.
     *
     * @param answer The answer to the question.
     */
    private void onAnswered(boolean answer) {
        Question nextQuestion = currentQuestion.getNextQuestion(answer);

        // Check for outcome
        if (nextQuestion instanceof Outcome) {
            Outcome outcome = (Outcome) nextQuestion;
            nextQuestion = outcome.getNextQuestion();

            // Save our outcome
            saveOutcome(outcome);

            // Get out of here if the outcome is an endpoint
            if (nextQuestion == null) {
                onReachedWizardFinish(outcome);
                return;
            }
        }

        currentQuestion = nextQuestion;
        pushQuestionToScreen(currentQuestion);
    }

    /**
     * Gets called when we want to try the wizard again.
     * TODO Implement
     */
    private void onClickTryAgain() {
        Intent intent = new Intent(getApplicationContext(), SettingsWizardActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Pushes the outcome value to our current settings variable.
     *
     * @param outcome The outcome of the last question
     */
    private void saveOutcome(Outcome outcome) {
        Type type = outcome.getType();
        if (type == BuildingCategory.class) {
            buildingCategory = (BuildingCategory) outcome.getOutcome();
        } else if (type == VibrationCategory.class) {
            vibrationCategory = (VibrationCategory) outcome.getOutcome();
        } else if (type == Boolean.class) {
            vibrationSensitive = (Boolean) outcome.getOutcome();
        }
    }

    /**
     * Gets called when we reach an endpoint in the flowchart.
     * @param outcome The outcome
     */
    private void onReachedWizardFinish(Outcome outcome) {
        switchLayout(true);

        settings = new Settings(buildingCategory, vibrationCategory, vibrationSensitive);

        if (settings.isValid()) {
            setFinishTextViewsVisibilities(true);
            findViewById(R.id.buttonWizardTryAgain).setVisibility(View.GONE);
            textViewTop.setText(getResources().getString(R.string.widget_final_top_success));
            textViewBuildingCategory.setText(getResources().getStringArray(R.array.category_dropdown_building)[settings.getBuildingCategory().ordinal()]);
            textViewVibrationCategory.setText(getResources().getStringArray(R.array.category_dropdown_vibration)[settings.getVibrationCategory().ordinal()]);
            textViewVibrationSensitive.setText(settings.isVibrationSensitive() ? getResources().getString(R.string.default_yes) : getResources().getString(R.string.default_no));
        } else {
            setFinishTextViewsVisibilities(false);
            textViewTop.setText(getResources().getStringArray(R.array.wizard_outcome_text)[outcome.getIndex()]);
            findViewById(R.id.buttonWizardTryAgain).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Changes our outcome text visibility.
     * @param visible True if visible, false if not
     */
    private void setFinishTextViewsVisibilities(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        textViewBuildingCategory.setVisibility(visibility);
        textViewVibrationCategory.setVisibility(visibility);
        textViewVibrationSensitive.setVisibility(visibility);
        textViewStaticBuildingCategory.setVisibility(visibility);
        textViewStaticVibrationCategory.setVisibility(visibility);
        textViewStaticVibrationSensitive.setVisibility(visibility);
    }

    /**
     * Gets called when we press the back button while in this activity.
     */
    @Override
    public void onBackPressed() {
        finish();
    }

}
