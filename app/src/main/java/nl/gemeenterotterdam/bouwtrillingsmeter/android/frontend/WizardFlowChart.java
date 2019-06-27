package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * This represents an entire flowchart as a graph.
 */
class Wizard {

    private Question start;

    /**
     * Creates a new instance representing a flowchart.
     *
     * @param start The start question
     */
    Wizard(Question start) {
        this.start = start;
    }

    /**
     * Gets our starting question.
     *
     * @return Get the start question
     */
    Question getStartQuestion() {
        return start;
    }

}


/**
 * A node represents either a question.
 */
class Question {

    private int index;
    private Edge edgeYes;
    private Edge edgeNo;

    Question(int questionIndex) {
        this.index = questionIndex;
    }

    /**
     * Adds an edge.
     *
     * @param edge The edge
     */
    void addEdge(Edge edge) {
        if (edge.isYes()) {
            edgeYes = edge;
        } else {
            edgeNo = edge;
        }
    }

    /**
     * Gets our next question.
     *
     * @param answer The answer to this question
     * @return The next question
     */
    Question getNextQuestion(boolean answer) {
        if (answer) {
            return edgeYes.getTo();
        } else {
            return edgeNo.getTo();
        }
    }

    /**
     * Gets the question index to look up resources.
     *
     * @return The index
     */
    int getIndex() {
        return index;
    }

}


/**
 * This represents an outcome node.
 */
class Outcome<T> extends Question {

    private final T outcome;
    private final Class<T> type;
    private Question nextQuestion;
    private boolean end;

    Outcome(int index, Class<T> type, T outcome) {
        super(index);
        this.type = type;
        this.outcome = outcome;
        end = false;
    }

    /**
     * Sets the next question.
     * If set to null this is treated as an endpoint.
     * @param nextQuestion The next question.
     */
    void setNextQuestion(Question nextQuestion) {
        if (nextQuestion == null) {
            end = true;
        }
        this.nextQuestion = nextQuestion;
    }

    /**
     * Returns the next question.
     * If this is an endpoint this returns null.
     * @return The next question, or null
     */
    Question getNextQuestion() {
        return nextQuestion;
    }

    /**
     * Used to determine the type.
     * Generic typing is lost at runtime.
     * @return The type
     */
    Class<T> getType() {
        return type;
    }

    /**
     * Gets the outcome.
     * Null of .NONE if none was found.
     * @return The outcome
     */
    T getOutcome() {
        return outcome;
    }

}


/**
 * And edge represents an edge in the flowchart.
 * If {@link #yes} is true, the answer to the question
 * corresponds to "yes" for this edge.
 */
class Edge {

    private boolean yes;
    private Question from;
    private Question to;

    Edge(boolean yes, Question from, Question to) {
        this.yes = yes;
        this.from = from;
        this.to = to;
    }

    boolean isYes() {
        return yes;
    }

    Question getFrom() {
        return from;
    }

    Question getTo() {
        return to;
    }

}
