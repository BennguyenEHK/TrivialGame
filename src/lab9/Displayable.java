/**
 * Displayable.java
 * This interface forces any class that shows results to the user
 * to implement display() and getDisplayText().
 * Leaderboard implements this so rankings can always be displayed consistently.
 *
 * Author: Sefan Adinew
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package lab9;

public interface Displayable {

    // print or render the content
    void display();

    // return the content as a formatted string for the GUI
    String getDisplayText();
}
