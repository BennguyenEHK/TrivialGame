/**
 * Scorable.java
 * This interface makes sure any class that tracks a score
 * has to implement the same three methods.
 * Player implements this so all scoring goes through one consistent contract.
 *
 * Author: Sefan Adinew
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package finalproject;

public interface Scorable {

    // add points to the score
    void addScore(int points);

    // return the current score
    int getScore();

    // reset everything back to zero (used when replaying)
    void resetScore();
}
