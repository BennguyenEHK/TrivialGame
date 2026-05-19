/**
 * ArenaGamePanel.java
 * The 2D flag-capture arena — the right panel of the split game screen.
 * Extends Pane to hold a fixed Canvas that is driven by an AnimationTimer game loop.
 *
 * Responsibilities:
 *   - Spawn four answer flags (A/B/C/D) at valid random positions each question.
 *   - Move entities in response to keyboard input (P1: WASD, P2: Arrow keys).
 *   - Resolve entity-entity push collisions in two-player mode.
 *   - Detect when the active entity's center reaches a flag's capture radius.
 *   - Fire the onFlagCaptured callback (passed in from SustainabilityTriviaApp) with the answer index.
 *
 * Author: Minh Nguyen
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package finalproject;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

public class ArenaGamePanel extends Pane {

    // Canvas dimensions — right panel of the 900-wide split screen
    public static final double W = 520;
    public static final double H = 560;

    // Minimum distances to ensure flags are never spawned too close to entities or each other
    private static final double MIN_DIST_FROM_SPAWN   = 120;
    private static final double MIN_DIST_BETWEEN_FLAGS = 92;
    private static final double EDGE_MARGIN            = 52;

    private final Canvas           canvas;
    private final GraphicsContext  gc;
    private AnimationTimer         gameLoop;

    // Player entities — p2Entity is null in single-player / time-based mode
    private final ArenaEntity  p1Entity;
    private       ArenaEntity  p2Entity;

    // Flags for the current question round
    private final List<ArenaFlag> flags = new ArrayList<>();

    // State
    private boolean questionActive  = false;
    private boolean twoPlayerMode   = false;
    private int     activePlayerIdx = 0;   // 0 = P1 is the answerer, 1 = P2 is the answerer

    // Enhancement A — collision wave animation fields
    private long   collisionWaveStartTime = -1L;
    private double collisionWaveX         = 0.0;
    private double collisionWaveY         = 0.0;
    private static final long[] WAVE_OFFSETS  = {0L, 80_000_000L, 160_000_000L};
    private static final long   WAVE_DURATION = 300_000_000L;
    private static final long   WAVE_TOTAL    = 460_000_000L;

    // Enhancement B — turn overlay fields
    private boolean  overlayActive       = false;
    private long     overlayStartTime    = -1L;
    private String   overlayAttackerName = "";
    private String   overlayDefenderName = "";
    private int      overlayRoundNumber  = 0;
    public  Runnable onOverlayDismissed  = null;
    private static final long OVERLAY_DURATION = 3_000_000_000L;

    // Keys currently held — updated by SustainabilityTriviaApp via scene key handlers
    private final Set<KeyCode> pressed = new HashSet<>();

    // Fired by the arena when a flag is captured; passes the answerIndex (0–3)
    private final Consumer<Integer> onFlagCaptured;

    private final Random rng = new Random();

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    /** Single-player and time-based constructor. */
    public ArenaGamePanel(String playerName, Consumer<Integer> onFlagCaptured) {
        this.twoPlayerMode  = false;
        this.onFlagCaptured = onFlagCaptured;

        canvas = new Canvas(W, H);
        gc     = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        setPrefSize(W, H);

        // Entity spawns at canvas center
        p1Entity = new ArenaEntity(playerName, Color.web("#4ade80"),
            W / 2 - ArenaEntity.WIDTH  / 2,
            H / 2 - ArenaEntity.HEIGHT / 2,
            W, H);
        p2Entity = null;
    }

    /** Two-player constructor — both entities present at all times. */
    public ArenaGamePanel(String p1Name, String p2Name, Consumer<Integer> onFlagCaptured) {
        this.twoPlayerMode  = true;
        this.onFlagCaptured = onFlagCaptured;

        canvas = new Canvas(W, H);
        gc     = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        setPrefSize(W, H);

        // P1 on the left quarter, P2 on the right quarter
        p1Entity = new ArenaEntity(p1Name, Color.web("#4ade80"),
            W * 0.25 - ArenaEntity.WIDTH  / 2,
            H / 2    - ArenaEntity.HEIGHT / 2,
            W, H);
        p2Entity = new ArenaEntity(p2Name, Color.web("#60a5fa"),
            W * 0.75 - ArenaEntity.WIDTH  / 2,
            H / 2    - ArenaEntity.HEIGHT / 2,
            W, H);
    }

    // ============================================================
    // PUBLIC INTERFACE (called by SustainabilityTriviaApp)
    // ============================================================

    /**
     * Load a new question: spawn flags for each answer option and reset entity positions.
     * activePlayerIdx tells the panel which entity can capture flags this round.
     */
    public void loadQuestion(Question question, int activePlayerIdx) {
        this.activePlayerIdx = activePlayerIdx;
        flags.clear();
        spawnFlags(question);
        resetPositions();

        if (twoPlayerMode) {
            // Enhancement B: show 3-second overlay before question becomes active
            this.questionActive  = false;
            this.overlayActive   = true;
            this.overlayStartTime = -1L;
            overlayRoundNumber++;
            overlayAttackerName = (activePlayerIdx == 0) ? p1Entity.getName() : p2Entity.getName();
            overlayDefenderName = (activePlayerIdx == 0) ? p2Entity.getName() : p1Entity.getName();
        } else {
            this.questionActive = true;
        }

        // Enhancement D: set role badges on entities
        p1Entity.setRole(activePlayerIdx == 0, twoPlayerMode);
        if (p2Entity != null) p2Entity.setRole(activePlayerIdx == 1, true);
    }

    /** Stop accepting flag captures without stopping the game loop (called on timeout). */
    public void deactivate() {
        questionActive = false;
    }

    /** Trigger zap animation on the active answerer entity. */
    public void zapActiveEntity() {
        activeEntity().triggerZap();
    }

    /** Trigger celebration animation on the active answerer entity. */
    public void celebrateActiveEntity() {
        activeEntity().triggerCelebrate();
    }

    /** Start the AnimationTimer. Call after the scene is shown. */
    public void start() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                tick(now);
            }
        };
        gameLoop.start();
    }

    /** Stop the AnimationTimer cleanly. */
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
    }

    /** Forward key-pressed events from the scene. */
    public void handleKeyPressed(KeyCode key)  { pressed.add(key); }

    /** Forward key-released events from the scene. */
    public void handleKeyReleased(KeyCode key) { pressed.remove(key); }

    // ============================================================
    // PRIVATE GAME LOOP
    // ============================================================

    private void tick(long now) {
        // Enhancement B: overlay countdown — runs before any other logic
        if (twoPlayerMode && overlayActive) {
            if (overlayStartTime == -1L) overlayStartTime = now;
            if ((now - overlayStartTime) >= OVERLAY_DURATION) {
                overlayActive  = false;
                questionActive = true;
                if (onOverlayDismissed != null) onOverlayDismissed.run();
            }
        }

        updateEntities(now);
        if (twoPlayerMode && p2Entity != null) resolveEntityCollision(now);
        if (!overlayActive) checkCaptures();
        render(now);
    }

    /** Move entities based on currently held keys. */
    private void updateEntities(long now) {
        if (twoPlayerMode) {
            // Two-player: P1 uses WASD, P2 uses Arrow keys
            p1Entity.update(
                pressed.contains(KeyCode.W),
                pressed.contains(KeyCode.S),
                pressed.contains(KeyCode.A),
                pressed.contains(KeyCode.D),
                now
            );
            if (p2Entity != null) {
                p2Entity.update(
                    pressed.contains(KeyCode.UP),
                    pressed.contains(KeyCode.DOWN),
                    pressed.contains(KeyCode.LEFT),
                    pressed.contains(KeyCode.RIGHT),
                    now
                );
            }
        } else {
            // Single-player and Time Based: Arrow keys only
            p1Entity.update(
                pressed.contains(KeyCode.UP),
                pressed.contains(KeyCode.DOWN),
                pressed.contains(KeyCode.LEFT),
                pressed.contains(KeyCode.RIGHT),
                now
            );
        }
    }

    /**
     * Push overlapping entities apart using center-to-center direction.
     * Enhancement A: asymmetric push — attacker is repelled 3× harder (48 px) than the
     * defender (16 px), plus an expanding amber wave ring animation at the collision midpoint.
     */
    private void resolveEntityCollision(long now) {
        if (p2Entity == null) return;

        javafx.geometry.Rectangle2D b1 = p1Entity.getBounds();
        javafx.geometry.Rectangle2D b2 = p2Entity.getBounds();
        if (!b1.intersects(b2)) return;

        // Capture midpoint BEFORE pushBack for the wave origin
        double waveX = (p1Entity.getCenterX() + p2Entity.getCenterX()) / 2.0;
        double waveY = (p1Entity.getCenterY() + p2Entity.getCenterY()) / 2.0;

        double dx  = p2Entity.getCenterX() - p1Entity.getCenterX();
        double dy  = p2Entity.getCenterY() - p1Entity.getCenterY();
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len < 0.01) { dx = 1; dy = 0; len = 1; }

        // Penetration depth: how far they have overlapped
        double penetration = ArenaEntity.WIDTH - len;
        if (penetration <= 0) return;

        // Asymmetric push: defender repels the attacker 3x harder than they are pushed back.
        // 3x chosen as the midpoint of the requested 2–4 range: visible enough to matter,
        // not so large it feels unfair. activePlayerIdx 0 = p1 is attacker, 1 = p2 is attacker.
        double attackerPush = 48.0;   // 3 × base (16)
        double defenderPush = 16.0;
        double ap = (activePlayerIdx == 0) ? attackerPush : defenderPush;
        double dp = (activePlayerIdx == 0) ? defenderPush : attackerPush;
        p1Entity.pushBack(-(dx / len) * ap, -(dy / len) * ap);
        p2Entity.pushBack( (dx / len) * dp,  (dy / len) * dp);

        // Enhancement A: spawn wave only if previous wave has expired (or never started)
        if (collisionWaveStartTime < 0 || (now - collisionWaveStartTime) >= WAVE_TOTAL) {
            collisionWaveStartTime = now;
            collisionWaveX = waveX;
            collisionWaveY = waveY;
        }
    }

    /** Check if the active entity has reached any flag's capture radius. */
    private void checkCaptures() {
        if (!questionActive) return;

        ArenaEntity actor = activeEntity();
        if (actor.isZapped()) return;  // frozen entities cannot capture

        double ax = actor.getCenterX();
        double ay = actor.getCenterY();

        for (ArenaFlag flag : flags) {
            if (!flag.isActive()) continue;

            // Flag base center (bottom of pole)
            double fx   = flag.getX() + ArenaFlag.FLAG_BANNER_W / 2.0;
            double fy   = flag.getY() + ArenaFlag.POLE_HEIGHT;
            double dist = Math.sqrt((ax - fx) * (ax - fx) + (ay - fy) * (ay - fy));

            if (dist < ArenaFlag.CAPTURE_RADIUS) {
                flag.capture();
                questionActive = false;
                onFlagCaptured.accept(flag.getAnswerIndex());
                return;
            }
        }
    }

    // ============================================================
    // RENDERING
    // ============================================================

    private void render(long now) {
        gc.clearRect(0, 0, W, H);
        drawBackground();
        drawFlags(now);
        drawEntities(now);
        drawCollisionWave(now);   // Enhancement A — wave rings on top of entities
        drawTurnOverlay(now);     // Enhancement B — overlay on top of everything (only active for 3s)
        drawHUD();
    }

    /** Dark grid arena floor with glowing border and corner marks. */
    private void drawBackground() {
        // Dark floor fill
        gc.setFill(Color.web("#071207"));
        gc.fillRect(0, 0, W, H);

        // Subtle grid pattern for depth
        gc.setStroke(Color.web("#1a3a1a", 0.45));
        gc.setLineWidth(0.8);
        for (double x = 0; x < W; x += 40) gc.strokeLine(x, 0, x, H);
        for (double y = 0; y < H; y += 40) gc.strokeLine(0, y, W, y);

        // Glowing outer border
        gc.setStroke(Color.web("#4ade80", 0.50));
        gc.setLineWidth(2.5);
        gc.strokeRect(3, 3, W - 6, H - 6);

        // Corner accent marks (adventure map style)
        drawCornerMarks();

        // Instructions at top center of arena
        String instr = twoPlayerMode
            ? "P1: WASD  |  P2: ARROWS  ·  Reach the correct flag!"
            : "ARROW KEYS to move  ·  Reach the correct flag!";
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.web("#4ade80", 0.40));
        double textW = instr.length() * 5.5;
        gc.fillText(instr, W / 2 - textW / 2, 17);
    }

    private void drawCornerMarks() {
        double sz = 13;
        gc.setStroke(Color.web("#4ade80", 0.80));
        gc.setLineWidth(2);
        // Top-left
        gc.strokeLine(3,    3,    3 + sz, 3);
        gc.strokeLine(3,    3,    3,      3 + sz);
        // Top-right
        gc.strokeLine(W-3-sz, 3,    W-3, 3);
        gc.strokeLine(W-3,    3,    W-3, 3+sz);
        // Bottom-left
        gc.strokeLine(3,    H-3, 3+sz, H-3);
        gc.strokeLine(3,    H-3-sz, 3, H-3);
        // Bottom-right
        gc.strokeLine(W-3-sz, H-3, W-3, H-3);
        gc.strokeLine(W-3,    H-3-sz, W-3, H-3);
    }

    private void drawFlags(long now) {
        for (ArenaFlag flag : flags) {
            if (flag.isActive()) flag.draw(gc, now);
        }
    }

    private void drawEntities(long now) {
        p1Entity.draw(gc, now);
        if (twoPlayerMode && p2Entity != null) p2Entity.draw(gc, now);
    }

    /** Enhancement A: draw expanding amber rings at the collision midpoint. */
    private void drawCollisionWave(long now) {
        if (collisionWaveStartTime < 0) return;
        long elapsed = now - collisionWaveStartTime;
        if (elapsed > WAVE_TOTAL) { collisionWaveStartTime = -1L; return; }
        for (int i = 0; i < WAVE_OFFSETS.length; i++) {
            long ringElapsed = elapsed - WAVE_OFFSETS[i];
            if (ringElapsed < 0 || ringElapsed > WAVE_DURATION) continue;
            double t      = (double) ringElapsed / WAVE_DURATION;
            double radius = t * 40;
            double alpha  = 0.85 * (1.0 - t);
            gc.setStroke(Color.rgb(255, 200, 80, Math.max(0.0, alpha)));
            gc.setLineWidth(2.5);
            gc.strokeOval(collisionWaveX - radius, collisionWaveY - radius, radius * 2, radius * 2);
        }
    }

    /** Enhancement B: draw a 3-second full-canvas overlay showing attacker/defender. */
    private void drawTurnOverlay(long now) {
        if (!twoPlayerMode || !overlayActive || overlayStartTime < 0) return;
        long   elapsed  = now - overlayStartTime;
        double progress = Math.min(1.0, (double) elapsed / OVERLAY_DURATION);

        // dark semi-transparent background
        gc.setFill(Color.rgb(0, 0, 0, 0.72));
        gc.fillRect(0, 0, W, H);

        gc.setTextAlign(TextAlignment.CENTER);

        // round number
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        gc.setFill(Color.rgb(170, 170, 170, 1.0));
        gc.fillText("ROUND " + overlayRoundNumber, W / 2, 218);

        // attacker section
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.setFill(Color.rgb(255, 102, 102, 1.0));
        gc.fillText("ATTACKING", W / 2, 256);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        gc.setFill(Color.WHITE);
        gc.fillText(overlayAttackerName, W / 2, 300);

        // defender section
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.setFill(Color.rgb(102, 170, 255, 1.0));
        gc.fillText("DEFENDING", W / 2, 340);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        gc.setFill(Color.rgb(204, 204, 204, 1.0));
        gc.fillText(overlayDefenderName, W / 2, 376);

        // countdown progress bar (shrinks as time runs out)
        double barW  = 260;
        double barX  = W / 2 - barW / 2;
        double barY  = 416;
        double barHt = 8;
        gc.setFill(Color.rgb(60, 60, 60, 0.8));
        gc.fillRoundRect(barX, barY, barW, barHt, 4, 4);
        double fill = barW * (1.0 - progress);
        if (fill > 0) {
            gc.setFill(Color.rgb(255, 200, 80, 1.0));
            gc.fillRoundRect(barX, barY, fill, barHt, 4, 4);
        }

        gc.setTextAlign(TextAlignment.LEFT); // reset
    }

    /** Show whose turn it is at the bottom of the arena in two-player mode. */
    private void drawHUD() {
        if (!twoPlayerMode || p2Entity == null) return;

        ArenaEntity answerer = activeEntity();
        String turnText = ">>> " + answerer.getName() + "'s turn to answer <<<";

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setFill(Color.web("#fbbf24", 0.85));
        double textW = turnText.length() * 5.5;
        gc.fillText(turnText, W / 2 - textW / 2, H - 11);
    }

    // ============================================================
    // FLAG SPAWNING
    // ============================================================

    /**
     * Spawn one flag per answer option in the question.
     * True/False questions spawn 2 flags; multiple-choice spawn 4.
     * Flags are placed with minimum distance from entity spawn centers
     * and from each other, using rejection sampling.
     */
    private void spawnFlags(Question question) {
        String[] options  = question.getOptions();
        int      numFlags = Math.min(options.length, 4);
        char[]   labels   = {'A', 'B', 'C', 'D'};

        // Entity spawn center coordinates for distance checking
        double[] spawnCx, spawnCy;
        if (twoPlayerMode) {
            spawnCx = new double[]{ W * 0.25, W * 0.75 };
            spawnCy = new double[]{ H / 2,    H / 2 };
        } else {
            spawnCx = new double[]{ W / 2 };
            spawnCy = new double[]{ H / 2 };
        }

        for (int i = 0; i < numFlags; i++) {
            double fx, fy;
            int    attempts = 0;
            do {
                fx = EDGE_MARGIN + rng.nextDouble() * (W - EDGE_MARGIN * 2 - ArenaFlag.FLAG_BANNER_W);
                fy = EDGE_MARGIN + rng.nextDouble() * (H - EDGE_MARGIN * 2 - ArenaFlag.POLE_HEIGHT);
                attempts++;
            } while (!isFlagPositionValid(fx, fy, spawnCx, spawnCy, i) && attempts < 300);

            flags.add(new ArenaFlag(labels[i], i, fx, fy));
        }
    }

    /** Returns true when the candidate position is far enough from spawns and other flags. */
    private boolean isFlagPositionValid(double fx, double fy,
                                         double[] spawnCx, double[] spawnCy,
                                         int numAlreadyPlaced) {
        // Check against all entity spawn centers
        for (int i = 0; i < spawnCx.length; i++) {
            double dx = fx - spawnCx[i];
            double dy = fy - spawnCy[i];
            if (Math.sqrt(dx * dx + dy * dy) < MIN_DIST_FROM_SPAWN) return false;
        }
        // Check against already-placed flags
        for (int i = 0; i < numAlreadyPlaced && i < flags.size(); i++) {
            ArenaFlag f  = flags.get(i);
            double    dx = fx - f.getX();
            double    dy = fy - f.getY();
            if (Math.sqrt(dx * dx + dy * dy) < MIN_DIST_BETWEEN_FLAGS) return false;
        }
        return true;
    }

    // ============================================================
    // HELPERS
    // ============================================================

    /** Returns the entity that can capture flags this round. */
    private ArenaEntity activeEntity() {
        if (!twoPlayerMode || activePlayerIdx == 0) return p1Entity;
        return p2Entity;
    }

    /** Reset entity positions to starting locations for a new question. */
    private void resetPositions() {
        if (!twoPlayerMode) {
            p1Entity.reset(W / 2 - ArenaEntity.WIDTH  / 2,
                           H / 2 - ArenaEntity.HEIGHT / 2);
        } else {
            p1Entity.reset(W * 0.25 - ArenaEntity.WIDTH  / 2,
                           H / 2    - ArenaEntity.HEIGHT / 2);
            if (p2Entity != null) {
                p2Entity.reset(W * 0.75 - ArenaEntity.WIDTH  / 2,
                               H / 2    - ArenaEntity.HEIGHT / 2);
            }
        }
    }
}
