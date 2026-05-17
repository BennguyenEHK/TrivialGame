/**
 * Leaderboard.java
 * Keeps the list of players and sorts them by score at the end of the game.
 * Implements Displayable so it always has a way to show the results.
 * I used Collections.sort with an anonymous Comparator to sort descending.
 *
 * Author: Sefan Adinew
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package finalproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Leaderboard implements Displayable {

    private ArrayList<Player> players;  // the players in this game
    private String gameMode;            // which mode was played (used in the display)

    public Leaderboard(String gameMode) {
        this.gameMode = gameMode;
        this.players  = new ArrayList<Player>();
    }

    // add a player to the board
    public void addPlayer(Player player) {
        if (player != null) {
            players.add(player);
        }
    }

    // clear the list when resetting
    public void clearPlayers() {
        players.clear();
    }

    // sort players highest score first using an anonymous Comparator
    public ArrayList<Player> getRankedPlayers() {
        ArrayList<Player> sorted = new ArrayList<Player>(players);
        Collections.sort(sorted, new Comparator<Player>() {
            @Override
            public int compare(Player a, Player b) {
                return Integer.compare(b.getScore(), a.getScore()); // descending
            }
        });
        return sorted;
    }

    // returns the player in first place
    public Player getWinner() {
        if (players.isEmpty()) return null;
        return getRankedPlayers().get(0);
    }

    // returns a player's rank (1 = first place)
    public int getRankOf(Player player) {
        ArrayList<Player> ranked = getRankedPlayers();
        for (int i = 0; i < ranked.size(); i++) {
            if (ranked.get(i).getName().equals(player.getName())) {
                return i + 1;
            }
        }
        return -1; // not found
    }

    public int getPlayerCount() { return players.size(); }
    public String getGameMode() { return gameMode; }

    // --- Displayable interface methods ---

    // prints the leaderboard to the console (used for testing)
    @Override
    public void display() {
        System.out.println(getDisplayText());
    }

    // builds the full leaderboard string shown in the GUI TextArea
    @Override
    public String getDisplayText() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LEADERBOARD: " + gameMode.toUpperCase() + " ===\n\n");

        ArrayList<Player> ranked = getRankedPlayers();

        if (ranked.isEmpty()) {
            sb.append("No players yet.\n");
        } else {
            for (int i = 0; i < ranked.size(); i++) {
                Player p = ranked.get(i);
                sb.append(String.format("%d. %-20s  %4d pts   Correct: %d/%d   Accuracy: %.0f%%%n",
                        i + 1,
                        p.getName(),
                        p.getScore(),
                        p.getCorrectAnswers(),
                        p.getQuestionsAnswered(),
                        p.getAccuracy()));
            }

            Player winner = getWinner();
            if (winner != null) {
                sb.append("\nWinner: " + winner.getName()
                        + " with " + winner.getScore() + " points!\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Leaderboard{mode=" + gameMode + ", players=" + players.size() + "}";
    }
}
