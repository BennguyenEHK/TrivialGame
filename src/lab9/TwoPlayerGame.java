/**
 * TwoPlayerGame.java
 * Two players take turns answering questions.
 * Each question goes to one player — they alternate back and forth.
 * Whoever has the highest score at the end wins.
 *
 * Author: Sefan Adinew
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package lab9;

import java.util.ArrayList;

public class TwoPlayerGame extends TriviaGame {

    private Player player1;
    private Player player2;
    private int currentPlayerIndex;  // 0 = player1's turn, 1 = player2's turn
    private long questionStartTime;

    public TwoPlayerGame(String player1Name, String player2Name, String difficulty) {
        super(difficulty);
        this.player1            = new Player(player1Name);
        this.player2            = new Player(player2Name);
        this.currentPlayerIndex = 0;  // player 1 always goes first
    }

    // --- implementing the abstract methods from TriviaGame ---

    @Override
    public String getGameModeName() {
        return "Two Player";
    }

    // reset both players and start from question 1 with player 1's turn
    @Override
    public void startGame() {
        player1.resetScore();
        player2.resetScore();
        currentQuestionIndex = 0;
        currentPlayerIndex   = 0;
        gameOver             = false;
        lastAnswer           = null;
        questionStartTime    = System.currentTimeMillis();
    }

    // whoever's turn it is answers this question, then we switch
    @Override
    public void submitAnswer(int selectedIndex) {
        if (gameOver || currentQuestionIndex >= currentQuestions.size()) return;

        long timeTaken = System.currentTimeMillis() - questionStartTime;

        Question q = currentQuestions.get(currentQuestionIndex);
        lastAnswer = new Answer(q, selectedIndex, timeTaken);
        getCurrentPlayer().recordAnswer(lastAnswer);

        // flip turns: 0 -> 1 -> 0 -> 1
        currentPlayerIndex = (currentPlayerIndex == 0) ? 1 : 0;

        advanceQuestion();

        questionStartTime = System.currentTimeMillis();
    }

    // returns whichever player's turn it is right now
    @Override
    public Player getCurrentPlayer() {
        return (currentPlayerIndex == 0) ? player1 : player2;
    }

    // returns both players in a list
    @Override
    public ArrayList<Player> getPlayers() {
        ArrayList<Player> list = new ArrayList<Player>();
        list.add(player1);
        list.add(player2);
        return list;
    }

    // --- two player specific ---

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }

    // index of the player whose turn it is (0 = player1, 1 = player2)
    // read by ArenaGamePanel before submitAnswer() flips it
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }

    // whoever has more points — returns null if it's a tie
    public Player getWinner() {
        if (player1.getScore() > player2.getScore()) return player1;
        if (player2.getScore() > player1.getScore()) return player2;
        return null;
    }

    // label shown in the game screen header during play
    public String getTurnLabel() {
        int num = (currentPlayerIndex == 0) ? 1 : 2;
        return "Player " + num + "'s Turn (" + getCurrentPlayer().getName() + ")";
    }

    // score comparison string shown in the header
    public String getScoreComparison() {
        return player1.getName() + ": " + player1.getScore()
             + " pts  |  " + player2.getName() + ": " + player2.getScore() + " pts";
    }

    public String getAttackerName() {
        return getCurrentPlayer().getName();
    }

    public String getDefenderName() {
        return (currentPlayerIndex == 0) ? player2.getName() : player1.getName();
    }

    @Override
    public String toString() {
        return "TwoPlayerGame{p1=" + player1.getName() + "(" + player1.getScore() + " pts)"
             + ", p2=" + player2.getName() + "(" + player2.getScore() + " pts)"
             + ", " + getProgressString() + "}";
    }
}
