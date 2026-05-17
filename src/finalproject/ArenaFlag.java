/**
 * ArenaFlag.java
 * Represents a collectible answer flag in the 2D arena.
 * Each flag corresponds to one answer option (A/B/C/D).
 * Rendered as a pole with a triangular banner using Canvas primitives.
 *
 * Author: Sefan Adinew
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package finalproject;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ArenaFlag {

    // visual dimensions and capture detection distance
    public static final double POLE_HEIGHT     = 55;
    public static final double FLAG_BANNER_W   = 36;
    public static final double FLAG_BANNER_H   = 24;
    public static final double CAPTURE_RADIUS  = 30;   // how close an entity must be to capture

    private final char   label;        // 'A', 'B', 'C', or 'D'
    private final int    answerIndex;  // 0, 1, 2, or 3 — maps to the question's options array
    private final double x;            // top-left x of the pole base
    private final double y;            // top-left y of the pole base
    private final Color  flagColor;    // color assigned from the label letter

    private boolean active = true;     // false once a player captures this flag

    // constructor — stores identity data and derives the flag color from the label
    public ArenaFlag(char label, int answerIndex, double x, double y) {
        this.label       = label;
        this.answerIndex = answerIndex;
        this.x           = x;
        this.y           = y;

        // each answer letter gets a distinct color
        switch (label) {
            case 'A': this.flagColor = Color.web("#f97316"); break;   // orange
            case 'B': this.flagColor = Color.web("#60a5fa"); break;   // blue
            case 'C': this.flagColor = Color.web("#a78bfa"); break;   // purple
            case 'D': this.flagColor = Color.web("#34d399"); break;   // teal green
            default:  this.flagColor = Color.web("#e2e8f0"); break;   // fallback grey
        }
    }

    // --- getters ---

    public char    getLabel()       { return label; }
    public int     getAnswerIndex() { return answerIndex; }
    public double  getX()           { return x; }
    public double  getY()           { return y; }
    public boolean isActive()       { return active; }

    // mark this flag as captured — stops it from being interactable
    public void capture() { active = false; }

    // collision / capture zone at the base of the pole
    public Rectangle2D getBounds() {
        return new Rectangle2D(x - 5, y + POLE_HEIGHT - 15, 20, 20);
    }

    // --- rendering ---

    // draws the flag pole, banner triangle, label letter, base glow, and footer text
    public void draw(GraphicsContext gc, long nowNano) {

        // 1. draw the vertical pole
        gc.setStroke(Color.web("#92400e"));   // brown pole
        gc.setLineWidth(4);
        gc.strokeLine(x + 3, y + POLE_HEIGHT, x + 3, y);

        // 2. draw the triangular flag banner
        double[] px = { x + 3, x + 3, x + 3 + FLAG_BANNER_W };
        double[] py = { y,      y + FLAG_BANNER_H, y + FLAG_BANNER_H / 2 };
        gc.setFill(flagColor);
        gc.fillPolygon(px, py, 3);

        // semi-transparent white outline on the banner
        gc.setStroke(Color.web("#ffffff", 0.3));
        gc.setLineWidth(1);
        gc.strokePolygon(px, py, 3);

        // 3. draw the answer letter on the banner
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.setFill(Color.WHITE);
        gc.fillText(String.valueOf(label), x + 12, y + 17);

        // 4. draw a pulsing glow circle at the base — capture zone indicator
        long   pulse = (nowNano / 400_000_000L) % 2;
        double alpha = (pulse == 0) ? 0.5 : 0.25;
        gc.setFill(flagColor.deriveColor(0, 1, 1, alpha));
        gc.fillOval(x - 8, y + POLE_HEIGHT - 10, 22, 22);

        // 5. small label below the pole base
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 9));
        gc.setFill(Color.web("#94a3b8"));
        gc.fillText("Flag " + label, x - 3, y + POLE_HEIGHT + 14);
    }
}
