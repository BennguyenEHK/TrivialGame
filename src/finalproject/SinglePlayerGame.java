/**
 * SinglePlayerGame.java
 * One player, 8 questions, highest score possible.
 * Extends TriviaGame and fills in all four abstract methods
 * with the single-player version of the logic.
 *
 * Author: Sefan Adinew
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package finalproject;

import java.util.ArrayList;

public class SinglePlayerGame extends TriviaGame {

    private Player player;           // the one player in this mode
    private long questionStartTime;  // used to calculate how long they took to answer

    public SinglePlayerGame(String playerName, String difficulty) {
        super(difficulty);  // loads questions and sets up leaderboard
        this.player = new Player(playerName);
    }

    // --- implementing the abstract methods from TriviaGame ---

    @Override
    public String getGameModeName() {
        return "Single Player";
    }

    // reset the player and start fresh from question 1
    @Override
    public void startGame() {
        player.resetScore();
        currentQuestionIndex = 0;
        gameOver             = false;
        lastAnswer           = null;
        questionStartTime    = System.currentTimeMillis();
    }

    // called when the player clicks an answer or times out
    // records the answer, updates the score, then moves to the next question
    @Override
    public void submitAnswer(int selectedIndex) {
        if (gameOver || currentQuestionIndex >= currentQuestions.size()) return;

        long timeTaken = System.currentTimeMillis() - questionStartTime;

        Question q = currentQuestions.get(currentQuestionIndex);
        lastAnswer = new Answer(q, selectedIndex, timeTaken);
        player.recordAnswer(lastAnswer);

        advanceQuestion();

        questionStartTime = System.currentTimeMillis();
    }

    // always returns the one player
    @Override
    public Player getCurrentPlayer() {
        return player;
    }

    // returns a list with just the one player in it
    @Override
    public ArrayList<Player> getPlayers() {
        ArrayList<Player> list = new ArrayList<Player>();
        list.add(player);
        return list;
    }

    // --- single player specific ---

    public int getFinalScore() {
        return player.getScore();
    }

    // summary string for debugging
    public String getResultSummary() {
        return String.format(
            "Game Over!\nPlayer: %s\nScore: %d points\nCorrect: %d/%d (%.0f%% accuracy)\nDifficulty: %s",
            player.getName(), player.getScore(),
            player.getCorrectAnswers(), player.getQuestionsAnswered(),
            player.getAccuracy(), difficulty);
    }

    @Override
    public String toString() {
        return "SinglePlayerGame{player=" + player.getName()
             + ", score=" + player.getScore()
             + ", " + getProgressString() + "}";
    }
}
