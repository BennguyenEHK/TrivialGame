/**
 * ArenaEntity.java
 * Represents a movable player character in the 2D flag-capture arena.
 * Handles movement physics, collision bounds, animation state, and Canvas rendering.
 * The entity is drawn programmatically using JavaFX GraphicsContext primitives.
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
import javafx.scene.text.TextAlignment;

public class ArenaEntity {

    // size and movement constants
    public static final double WIDTH  = 36;
    public static final double HEIGHT = 36;
    public static final double SPEED  = 3.5;

    // three possible animation states for this entity
    private enum EntityState { NORMAL, ZAPPED, CELEBRATING }

    // position in the arena
    private double x;
    private double y;

    // arena bounds used to clamp the entity inside the playfield
    private final double arenaW;
    private final double arenaH;

    private final Color  bodyColor;   // the fill color drawn on the entity body
    private final String name;        // display name shown below the entity

    private EntityState state        = EntityState.NORMAL;
    private long        stateEndNano = 0;   // nanosecond timestamp when the current state expires

    private boolean showRoleBadge = false;
    private boolean isAttacker    = false;

    // constructor — stores all parameters
    public ArenaEntity(String name, Color bodyColor,
                       double startX, double startY,
                       double arenaW, double arenaH) {
        this.name      = name;
        this.bodyColor = bodyColor;
        this.x         = startX;
        this.y         = startY;
        this.arenaW    = arenaW;
        this.arenaH    = arenaH;
    }

    // --- movement and state update ---

    // called every frame — moves the entity if not zapped, checks state expiry
    public void update(boolean up, boolean down, boolean left, boolean right, long nowNano) {
        // frozen during zap; check whether the zap has expired
        if (state == EntityState.ZAPPED) {
            if (nowNano > stateEndNano) {
                state = EntityState.NORMAL;
            }
            return;
        }

        // compute velocity from held keys
        double dx = 0;
        double dy = 0;
        if (left)  dx -= SPEED;
        if (right) dx += SPEED;
        if (up)    dy -= SPEED;
        if (down)  dy += SPEED;

        // apply movement and clamp to arena boundaries
        x = clamp(x + dx, 0, arenaW - WIDTH);
        y = clamp(y + dy, 0, arenaH - HEIGHT);
    }

    // returns the axis-aligned bounding box for collision detection
    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, WIDTH, HEIGHT);
    }

    // pushes the entity by (dx, dy) — used for collision resolution
    public void pushBack(double dx, double dy) {
        x = clamp(x + dx, 0, arenaW - WIDTH);
        y = clamp(y + dy, 0, arenaH - HEIGHT);
    }

    // freeze this entity and flash for 800 ms
    public void triggerZap() {
        state        = EntityState.ZAPPED;
        stateEndNano = System.nanoTime() + 800_000_000L;
    }

    // play the celebration ring animation for 1.5 s
    public void triggerCelebrate() {
        state        = EntityState.CELEBRATING;
        stateEndNano = System.nanoTime() + 1_500_000_000L;
    }

    // reset position and clear any active state
    public void reset(double startX, double startY) {
        x            = startX;
        y            = startY;
        state        = EntityState.NORMAL;
        stateEndNano = 0;
    }

    public void setRole(boolean attacker, boolean show) {
        this.isAttacker    = attacker;
        this.showRoleBadge = show;
    }

    // --- state query methods ---

    public boolean isZapped()      { return state == EntityState.ZAPPED; }
    public boolean isCelebrating() { return state == EntityState.CELEBRATING; }

    // --- position getters ---

    public double getX()       { return x; }
    public double getY()       { return y; }
    public double getCenterX() { return x + WIDTH  / 2; }
    public double getCenterY() { return y + HEIGHT / 2; }

    // --- identity getters ---

    public String getName()  { return name; }
    public Color  getColor() { return bodyColor; }

    // --- rendering ---

    // draws this entity onto the given Canvas GraphicsContext for the current frame
    public void draw(GraphicsContext gc, long nowNano) {

        // zap flash effect — alternating yellow / red aura
        if (state == EntityState.ZAPPED) {
            boolean flash = ((nowNano / 100_000_000L) % 2 == 0);
            gc.setFill(flash
                    ? Color.web("#ffdd00", 0.55)
                    : Color.web("#ff3300", 0.45));
            gc.fillOval(x - 10, y - 10, WIDTH + 20, HEIGHT + 20);
        }

        // celebrating — expanding golden ring that loops every 500 ms
        if (state == EntityState.CELEBRATING) {
            long   stateStart = stateEndNano - 1_500_000_000L;
            long   elapsed    = nowNano - stateStart;
            if (elapsed < 0) elapsed = 0;
            double t          = (double)(elapsed % 500_000_000L) / 500_000_000.0;
            double r          = t * 50;
            double alpha      = Math.max(0.0, Math.min(1.0, 1.0 - t));
            gc.setStroke(Color.web("#fbbf24", alpha));
            gc.setLineWidth(2.5);
            gc.strokeOval(getCenterX() - r, getCenterY() - r, r * 2, r * 2);
        }

        // determine body fill color — flash red while zapped
        boolean flash     = (state == EntityState.ZAPPED) && ((nowNano / 100_000_000L) % 2 == 0);
        Color   drawColor = (flash) ? Color.web("#ff5555") : bodyColor;

        // draw rounded-rectangle body
        gc.setFill(drawColor);
        gc.fillRoundRect(x, y, WIDTH, HEIGHT, 10, 10);

        // white outline around the body
        gc.setStroke(Color.web("#ffffff", 0.65));
        gc.setLineWidth(1.8);
        gc.strokeRoundRect(x, y, WIDTH, HEIGHT, 10, 10);

        // left eye — white sclera + dark pupil
        gc.setFill(Color.WHITE);
        gc.fillOval(x + 8,  y + 9,  8, 8);
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 10, y + 11, 4, 4);

        // right eye — white sclera + dark pupil
        gc.setFill(Color.WHITE);
        gc.fillOval(x + 20, y + 9,  8, 8);
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 22, y + 11, 4, 4);

        // role badge — "ATK" or "DEF" shown above entity body in two-player mode
        if (showRoleBadge) {
            double badgeX = x + (WIDTH / 2.0) - 16.0;
            double badgeY = y - 18.0;
            gc.setFill(isAttacker
                ? Color.rgb(220, 80, 60, 0.88)
                : Color.rgb(60, 100, 200, 0.88));
            gc.fillRoundRect(badgeX, badgeY, 32, 13, 4, 4);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 8));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(isAttacker ? "ATK" : "DEF", x + WIDTH / 2.0, badgeY + 10.0);
            gc.setTextAlign(TextAlignment.LEFT);
        }

        // celebrating bounce — shift the name tag y slightly with a sine wave
        double bounceOffsetY = 0;
        if (state == EntityState.CELEBRATING) {
            bounceOffsetY = Math.sin(nowNano / 150_000_000.0) * 3;
        }

        // name tag drawn below the entity body
        String displayName = name.length() > 8 ? name.substring(0, 8) : name;
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.WHITE);
        gc.fillText(displayName,
                x + WIDTH / 2 - displayName.length() * 2.8,
                y + HEIGHT + 13 + bounceOffsetY);
    }

    // --- private helper ---

    // clamps value to [min, max]
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
