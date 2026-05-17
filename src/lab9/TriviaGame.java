/**
 * TriviaGame.java
 * Abstract base class that all three game modes inherit from.
 * It handles the shared stuff — loading questions, tracking progress,
 * managing the leaderboard — so each subclass only has to worry
 * about what makes it different.
 *
 * The four abstract methods force every subclass to define
 * their own version of the core game behavior.
 *
 * Author: Sefan Adinew
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package lab9;

import java.util.ArrayList;

public abstract class TriviaGame {

    // shared fields — subclasses can access these directly with "protected"
    protected QuestionBank questionBank;
    protected ArrayList<Question> currentQuestions;
    protected Leaderboard leaderboard;
    protected int currentQuestionIndex;
    protected String difficulty;
    protected boolean gameOver;
    protected Answer lastAnswer;  // stores the most recent answer so the GUI can read it

    // 8 questions per round for single and two player modes
    protected static final int QUESTIONS_PER_ROUND = 8;

    // runs whenever a subclass calls super(difficulty)
    public TriviaGame(String difficulty) {
        this.questionBank         = new QuestionBank();
        this.difficulty           = difficulty;
        this.currentQuestions     = questionBank.getGameQuestions(difficulty, QUESTIONS_PER_ROUND);
        this.leaderboard          = new Leaderboard(getGameModeName());
        this.currentQuestionIndex = 0;
        this.gameOver             = false;
        this.lastAnswer           = null;
    }

    // --- abstract methods --- each subclass must implement these ---

    // the name shown in the UI and leaderboard header
    public abstract String getGameModeName();

    // set up players, reset scores, start the clock
    public abstract void startGame();

    // handle the player's answer — different for each mode
    // -1 means the player timed out
    public abstract void submitAnswer(int selectedIndex);

    // which player is currently answering
    public abstract Player getCurrentPlayer();

    // all players in this game (1 for single, 2 for two-player)
    public abstract ArrayList<Player> getPlayers();

    // --- shared methods all subclasses inherit ---

    // returns the question we're currently on
    public Question getCurrentQuestion() {
        if (currentQuestionIndex < currentQuestions.size()) {
            return currentQuestions.get(currentQuestionIndex);
        }
        return null;
    }

    // move to the next question — ends the game if we've gone through all of them
    protected void advanceQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex >= currentQuestions.size()) {
            gameOver = true;
        }
    }

    // called at the end — fills the leaderboard with final player data
    public void endGame() {
        gameOver = true;
        leaderboard.clearPlayers();
        ArrayList<Player> players = getPlayers();
        for (int i = 0; i < players.size(); i++) {
            leaderboard.addPlayer(players.get(i));
        }
    }

    // resets everything so the player can go again
    public void resetGame() {
        currentQuestions     = questionBank.getGameQuestions(difficulty, QUESTIONS_PER_ROUND);
        currentQuestionIndex = 0;
        gameOver             = false;
        lastAnswer           = null;
        ArrayList<Player> players = getPlayers();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).resetScore();
        }
        leaderboard.clearPlayers();
    }

    // getters
    public String getDifficulty()        { return difficulty; }
    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    public int getQuestionCount()        { return currentQuestions.size(); }
    public boolean isGameOver()          { return gameOver; }
    public Leaderboard getLeaderboard()  { return leaderboard; }
    public Answer getLastAnswer()        { return lastAnswer; }

    // shows something like "Question 3 of 8" — Time Based overrides this
    public String getProgressString() {
        return "Question " + (currentQuestionIndex + 1) + " of " + currentQuestions.size();
    }

    @Override
    public String toString() {
        return getGameModeName() + " [" + difficulty + "] - "
             + getProgressString()
             + (gameOver ? " GAME OVER" : "");
    }
}
