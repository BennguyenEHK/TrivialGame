/**
 * TimeBasedGame.java
 * The player has 60 seconds to answer as many questions as possible.
 * Instead of ending when questions run out, the question pool loops back around.
 * The game only ends when the global clock hits zero.
 *
 * Author: Sefan Adinew
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package finalproject;

import java.util.ArrayList;
import java.util.Collections;

public class TimeBasedGame extends TriviaGame {

    private Player player;
    private int timeLimitSeconds;    // how long the round lasts (default 60)
    private long gameStartTime;      // when the round started — used to count down
    private long questionStartTime;  // when the current question appeared

    public static final int DEFAULT_TIME_LIMIT = 60;

    // default constructor uses 60 seconds
    public TimeBasedGame(String playerName, String difficulty) {
        this(playerName, difficulty, DEFAULT_TIME_LIMIT);
    }

    // custom time limit constructor
    public TimeBasedGame(String playerName, String difficulty, int timeLimitSeconds) {
        super(difficulty);
        this.player           = new Player(playerName);
        this.timeLimitSeconds = timeLimitSeconds;
        // load the full question pool so we never run out mid-game
        this.currentQuestions = questionBank.getGameQuestions(difficulty, -1);
    }

    // --- implementing the abstract methods from TriviaGame ---

    @Override
    public String getGameModeName() {
        return "Time Based";
    }

    // shuffle the pool and start the 60-second clock
    @Override
    public void startGame() {
        player.resetScore();
        Collections.shuffle(currentQuestions);
        currentQuestionIndex = 0;
        gameOver             = false;
        lastAnswer           = null;
        gameStartTime        = System.currentTimeMillis();
        questionStartTime    = System.currentTimeMillis();
    }

    // answers are submitted the same way but instead of ending when questions
    // run out, we wrap back to the start and keep going until time is up
    @Override
    public void submitAnswer(int selectedIndex) {
        if (gameOver) return;

        long timeTaken = System.currentTimeMillis() - questionStartTime;

        Question q = currentQuestions.get(currentQuestionIndex);
        lastAnswer = new Answer(q, selectedIndex, timeTaken);
        player.recordAnswer(lastAnswer);

        // advance and wrap around instead of ending
        currentQuestionIndex++;
        if (currentQuestionIndex >= currentQuestions.size()) {
            Collections.shuffle(currentQuestions);  // re-shuffle for variety
            currentQuestionIndex = 0;
        }

        questionStartTime = System.currentTimeMillis();
    }

    @Override
    public Player getCurrentPlayer() {
        return player;
    }

    @Override
    public ArrayList<Player> getPlayers() {
        ArrayList<Player> list = new ArrayList<Player>();
        list.add(player);
        return list;
    }

    // --- time based specific ---

    // how many seconds are left — the GUI calls this every second to update the display
    public int getSecondsRemaining() {
        long elapsed = (System.currentTimeMillis() - gameStartTime) / 1000;
        return (int) Math.max(0, timeLimitSeconds - elapsed);
    }

    // returns true when the clock hits zero — the app's global timer checks this
    public boolean isTimeUp() {
        return getSecondsRemaining() <= 0;
    }

    public int getTimeLimitSeconds() { return timeLimitSeconds; }

    // override progress string to show time left instead of question count
    @Override
    public String getProgressString() {
        return "Time Remaining: " + getSecondsRemaining() + "s";
    }

    // override reset to also re-shuffle the question pool
    @Override
    public void resetGame() {
        player.resetScore();
        currentQuestions = questionBank.getGameQuestions(difficulty, -1);
        Collections.shuffle(currentQuestions);
        currentQuestionIndex = 0;
        gameOver             = false;
        lastAnswer           = null;
        leaderboard.clearPlayers();
    }

    public String getResultSummary() {
        return String.format(
            "Time's Up!\nPlayer: %s\nScore: %d points\nAnswered: %d questions\nCorrect: %d (%.0f%% accuracy)\nDifficulty: %s",
            player.getName(), player.getScore(),
            player.getQuestionsAnswered(), player.getCorrectAnswers(),
            player.getAccuracy(), difficulty);
    }

    @Override
    public String toString() {
        return "TimeBasedGame{player=" + player.getName()
             + ", score=" + player.getScore()
             + ", answered=" + player.getQuestionsAnswered()
             + ", timeLeft=" + getSecondsRemaining() + "s}";
    }
}
