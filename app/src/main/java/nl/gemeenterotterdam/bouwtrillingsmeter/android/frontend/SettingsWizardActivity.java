package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
 * TODO Who holds the settings file?
 */
public class SettingsWizardActivity extends AppCompatActivity {

    private SettingsWizard settingsWizard;

    private TextView textViewMain;
    private TextView textViewExtra;
    private Button buttonYes;
    private Button buttonNo;

    private Question currentQuestion;
    private Settings settings;

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
        textViewMain = (TextView) findViewById(R.id.textViewSettingsWizardMain);
        textViewExtra = (TextView) findViewById(R.id.textViewSettingsWizardExtra);
        buttonYes = (Button) findViewById(R.id.buttonSettingsWizardYes);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAnswered(true);
            }
        });
        buttonNo = (Button) findViewById(R.id.buttonSettingsWizardNo);
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAnswered(false);
            }
        });

        // Setup wizard
        settings = new Settings();
        settingsWizard = createQuestionWizard();
        currentQuestion = settingsWizard.getStartQuestion();
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
     * This pushes a question to the screen.
     *
     * @param question The question to push
     */
    private void pushQuestionToScreen(Question question) {
        if (question != currentQuestion) {
            throw new IllegalStateException("Set the question on screen as current question first!");
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

            // Get out of here if the outcome is an endpoint
            if (nextQuestion == null) {
                onReachedWizardFinish();
                return;
            }

            // Save our outcome
            saveOutcome(outcome);
        }

        currentQuestion = nextQuestion;
        pushQuestionToScreen(currentQuestion);
    }

    /**
     * Pushes the outcome value to our current settings variable.
     *
     * @param outcome The outcome of the last question
     */
    private void saveOutcome(Outcome outcome) {
        Type type = outcome.getType();
        if (type == BuildingCategory.class) {
            settings.buildingCategory = (BuildingCategory) outcome.getOutcome();
            System.out.println("BC = " + settings.buildingCategory);
        } else if (type == VibrationCategory.class) {
            settings.vibrationCategory = (VibrationCategory) outcome.getOutcome();
            System.out.println("VC = " + settings.vibrationCategory);
        } else if (type == Boolean.class) {
            settings.vibrationSensitive = (Boolean) outcome.getOutcome();
            System.out.println("VS = " + settings.vibrationSensitive);
        }
    }

    private void onReachedWizardFinish() {
        System.out.println("FINISH REACHED");
    }

}
