/**
 * Answer.java
 * Created every time a player submits an answer.
 * Stores what they picked, whether it was right, and how long they took.
 * The points calculation lives here — difficulty sets the base, speed adds a bonus.
 *
 * Author: Sefan Adinew, Minh Nguyen
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package finalproject;

public class Answer {

    private Question question;   // which question this answer is for
    private int selectedIndex;   // what the player chose (-1 means they timed out)
    private boolean correct;     // did they get it right?
    private long timeTakenMs;    // how many milliseconds they took to answer

    // constructor — figure out immediately whether the answer is correct
    // if selectedIndex is -1 (timeout), it's always wrong
    public Answer(Question question, int selectedIndex, long timeTakenMs) {
        this.question      = question;
        this.selectedIndex = selectedIndex;
        this.timeTakenMs   = timeTakenMs;
        this.correct = (selectedIndex >= 0) && question.isCorrect(selectedIndex);
    }

    // getters
    public Question getQuestion()    { return question; }
    public int getSelectedIndex()    { return selectedIndex; }
    public boolean isCorrect()       { return correct; }
    public long getTimeTakenMs()     { return timeTakenMs; }

    // returns the text of what the player chose
    // shows "No answer" if they ran out of time
    public String getSelectedAnswerText() {
        if (selectedIndex < 0) return "No answer (timed out)";
        return question.getOptions()[selectedIndex];
    }

    // points are based on difficulty — easy=10, medium=20, hard=30
    // +5 bonus if the player answered in under 5 seconds
    public int calculatePoints() {
        if (!correct) return 0;

        int basePoints;
        String diff = question.getDifficulty().toLowerCase();

        if (diff.equals("easy")) {
            basePoints = 10;
        } else if (diff.equals("medium")) {
            basePoints = 20;
        } else {
            basePoints = 30;
        }

        // speed bonus for answering fast
        int timeBonus = (timeTakenMs / 1000 < 5) ? 5 : 0;

        return basePoints + timeBonus;
    }

    // the message shown to the player after they answer
    public String getFeedback() {
        if (correct) {
            return "Correct! You earned " + calculatePoints() + " points.";
        } else {
            return "Incorrect. The correct answer was: " + question.getCorrectAnswerText();
        }
    }

    @Override
    public String toString() {
        return "Answer{selected=" + getSelectedAnswerText()
             + ", correct=" + correct
             + ", points=" + calculatePoints() + "}";
    }
}
