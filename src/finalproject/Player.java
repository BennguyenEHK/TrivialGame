/**
 * Player.java
 * Tracks one player's name, score, and answer history for the whole game.
 * Implements Scorable so it's forced to have addScore, getScore, and resetScore.
 * I used a static counter to track how many players have been created total.
 *
 * Author: Sefan Adinew, Minh Nguyen
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package finalproject;

import java.util.ArrayList;

public class Player implements Scorable {

    private String name;                  // player's name entered on the welcome screen
    private int score;                    // running total of points earned
    private ArrayList<Answer> answers;   // every answer the player has submitted
    private int questionsAnswered;        // how many questions they've attempted
    private int correctAnswers;           // how many they got right
    private static int playerCount = 0;  // counts all Player objects ever created

    // constructor — starts everything at zero
    public Player(String name) {
        this.name              = name;
        this.score             = 0;
        this.answers           = new ArrayList<Answer>();
        this.questionsAnswered = 0;
        this.correctAnswers    = 0;
        playerCount++;
    }

    // --- Scorable interface methods ---

    // only add points if the value is positive (no negative scoring)
    @Override
    public void addScore(int points) {
        if (points > 0) {
            score += points;
        }
    }

    @Override
    public int getScore() { return score; }

    // wipe everything when the player hits Play Again
    @Override
    public void resetScore() {
        score             = 0;
        questionsAnswered = 0;
        correctAnswers    = 0;
        answers.clear();
    }

    // called after every answer — records the answer and updates the score
    public void recordAnswer(Answer answer) {
        answers.add(answer);
        questionsAnswered++;
        if (answer.isCorrect()) {
            correctAnswers++;
            addScore(answer.calculatePoints());
        }
    }

    // getters
    public String getName()              { return name; }
    public ArrayList<Answer> getAnswers(){ return answers; }
    public int getQuestionsAnswered()    { return questionsAnswered; }
    public int getCorrectAnswers()       { return correctAnswers; }
    public static int getPlayerCount()   { return playerCount; }

    // accuracy as a percentage — avoids dividing by zero
    public double getAccuracy() {
        if (questionsAnswered == 0) return 0.0;
        return (double) correctAnswers / questionsAnswered * 100.0;
    }

    // setter with basic validation
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    @Override
    public String toString() {
        return String.format("Player{name='%s', score=%d, correct=%d/%d, accuracy=%.1f%%}",
                name, score, correctAnswers, questionsAnswered, getAccuracy());
    }
}
