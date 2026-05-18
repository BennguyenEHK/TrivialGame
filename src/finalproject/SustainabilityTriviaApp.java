/**
 * SustainabilityTriviaApp.java
 * Main JavaFX application — entry point for the whole game.
 * Handles navigation between three screens: Welcome, Game (arena), and Leaderboard.
 *
 * The game screen is a horizontal split panel:
 *   LEFT  — read-only question display with A/B/C/D option labels.
 *   RIGHT — a 2D flag-capture arena (ArenaGamePanel) driven by an AnimationTimer.
 *
 * The TriviaGame reference (polymorphism) drives all three modes.
 * Only the game screen changes; Welcome and Leaderboard screens are unchanged.
 *
 * Author: Sefan Adinew
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package finalproject;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.text.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class SustainabilityTriviaApp extends Application {

    public static final int    WINDOW_WIDTH  = 900;
    public static final int    WINDOW_HEIGHT = 650;
    public static final String APP_TITLE     = "Sustainability Trivia Game";

    private Stage primaryStage;

    // holds whichever game mode is currently active — declared as TriviaGame for polymorphism
    private TriviaGame currentGame;

    // the 2D arena canvas panel — created fresh for each game session
    private ArenaGamePanel arenaPanel;

    // top bar labels shared across the game screen
    private Label playerLabel;
    private Label defenderLabel;
    private Label scoreLabel;
    private Label progressLabel;
    private Label timerLabel;

    // left panel controls: question text, read-only option labels, feedback, next button
    private Label   questionLabel;
    private Label   feedbackLabel;
    private Label[] optionLabels = new Label[4];   // A/B/C/D — display only, not clickable
    private Button  nextButton;

    // per-question 20-second countdown (Two Player and Time Based only)
    private Timeline questionTimer;
    private int      questionSecondsLeft;

    // 60-second global timer used only in Time Based mode
    private Timeline globalTimer;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setResizable(false);
        showWelcomeScreen();
        primaryStage.show();
    }

    // loads styles.css from the same folder as the class files
    private void applyStylesheet(Scene scene) {
        try {
            String css = getClass().getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            // game still runs without the stylesheet
            System.out.println("Note: styles.css not found. Running with default styling.");
        }
    }

    // ==========================================================================
    // WELCOME SCREEN
    // ==========================================================================

    public void showWelcomeScreen() {
        stopAllTimers();

        VBox root = new VBox(0);
        root.getStyleClass().add("welcome-root");

        // title banner
        VBox titleBanner = new VBox(6);
        titleBanner.getStyleClass().add("title-banner");
        titleBanner.setAlignment(Pos.CENTER);
        titleBanner.setPadding(new Insets(28, 20, 22, 20));

        Label title = new Label("Sustainability Trivia Game");
        title.getStyleClass().add("game-title");

        Label subtitle = new Label("Student Sustainability Leadership Program (SSLP)");
        subtitle.getStyleClass().add("game-subtitle");

        Label purpose = new Label(
            "Test your knowledge about recycling, energy, water, biking, reuse, and campus sustainability.");
        purpose.getStyleClass().add("game-purpose");
        purpose.setMaxWidth(620);
        purpose.setAlignment(Pos.CENTER);

        titleBanner.getChildren().addAll(title, subtitle, purpose);

        // form area — player names, mode, difficulty
        VBox formBox = new VBox(12);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(18, 80, 18, 80));

        Label namesHeader = new Label("Player Names");
        namesHeader.getStyleClass().add("section-header");

        TextField player1Field = new TextField();
        player1Field.setPromptText("Enter Player 1 name");
        player1Field.getStyleClass().add("name-input");
        player1Field.setMaxWidth(300);

        TextField player2Field = new TextField();
        player2Field.setPromptText("Enter Player 2 name (Two Player only)");
        player2Field.getStyleClass().add("name-input");
        player2Field.setMaxWidth(300);
        player2Field.setDisable(true);

        Label modeHeader = new Label("Select Game Mode");
        modeHeader.getStyleClass().add("section-header");

        ToggleGroup modeGroup    = new ToggleGroup();
        RadioButton singleButton = new RadioButton("Single Player  — answer 8 questions, highest score wins");
        RadioButton twoButton    = new RadioButton("Two Player     — alternate turns, highest score wins");
        RadioButton timeButton   = new RadioButton("Time Based     — answer as many as possible in 60 seconds");

        singleButton.setToggleGroup(modeGroup);
        twoButton.setToggleGroup(modeGroup);
        timeButton.setToggleGroup(modeGroup);
        singleButton.getStyleClass().add("mode-radio");
        twoButton.getStyleClass().add("mode-radio");
        timeButton.getStyleClass().add("mode-radio");
        singleButton.setSelected(true);

        // enable player 2 field only when two-player mode is selected
        twoButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            player2Field.setDisable(!newVal);
        });

        Label diffHeader = new Label("Select Difficulty");
        diffHeader.getStyleClass().add("section-header");

        ComboBox<String> difficultyBox = new ComboBox<String>();
        difficultyBox.getItems().addAll("easy", "medium", "hard");
        difficultyBox.setValue("easy");
        difficultyBox.getStyleClass().add("difficulty-combo");

        Label diffHint = new Label(
            "easy = 10 pts  |  medium = 20 pts  |  hard = 30 pts  |  answer in under 5 sec = +5 bonus");
        diffHint.getStyleClass().add("hint-label");

        Label statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        Button startButton = new Button("Start Game ▶");
        startButton.getStyleClass().add("start-button");
        startButton.setOnAction(e -> {
            String p1         = player1Field.getText().trim();
            String p2         = player2Field.getText().trim();
            String difficulty = difficultyBox.getValue();

            if (p1.isEmpty()) p1 = "Player 1";

            if (twoButton.isSelected() && p2.isEmpty()) {
                statusLabel.setText("Please enter Player 2's name.");
                statusLabel.setTextFill(Color.web("#f87171"));
                return;
            }

            statusLabel.setText("");

            // polymorphism — currentGame holds different subclass depending on selection
            if (singleButton.isSelected()) {
                currentGame = new SinglePlayerGame(p1, difficulty);
            } else if (twoButton.isSelected()) {
                currentGame = new TwoPlayerGame(p1, p2, difficulty);
            } else {
                currentGame = new TimeBasedGame(p1, difficulty);
            }

            currentGame.startGame();
            showGameScreen();
        });

        formBox.getChildren().addAll(
            namesHeader, player1Field, player2Field,
            new Separator(),
            modeHeader, singleButton, twoButton, timeButton,
            new Separator(),
            diffHeader, difficultyBox, diffHint,
            new Separator(),
            statusLabel, startButton
        );

        root.getChildren().addAll(titleBanner, formBox);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        applyStylesheet(scene);
        primaryStage.setScene(scene);
    }

    // ==========================================================================
    // GAME SCREEN — split-panel arena (LEFT: question  |  RIGHT: 2D arena)
    // ==========================================================================

    public void showGameScreen() {
        stopAllTimers();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-root");

        // ── TOP BAR ────────────────────────────────────────────────────────────
        HBox topBox = new HBox(22);
        topBox.getStyleClass().add("top-bar");
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(9, 24, 9, 24));

        playerLabel   = new Label();
        defenderLabel = new Label();
        scoreLabel    = new Label();
        progressLabel = new Label();
        timerLabel    = new Label();
        playerLabel.getStyleClass().add("player-turn-label");
        defenderLabel.getStyleClass().add("defender-label");
        defenderLabel.setVisible(false);
        scoreLabel.getStyleClass().add("score-label");
        progressLabel.getStyleClass().add("progress-label");
        timerLabel.getStyleClass().add("timer-label");

        VBox playerRoleBox = new VBox(2, playerLabel, defenderLabel);
        playerRoleBox.setAlignment(Pos.CENTER_LEFT);

        topBox.getChildren().addAll(playerRoleBox, scoreLabel, progressLabel, timerLabel);

        // ── LEFT PANEL — read-only question display ─────────────────────────────
        VBox leftPanel = new VBox(10);
        leftPanel.getStyleClass().add("question-panel");
        leftPanel.setPrefWidth(380);
        leftPanel.setMinWidth(380);
        leftPanel.setPadding(new Insets(14, 16, 14, 16));

        // movement instructions at the top of the left panel
        String instrText = (currentGame instanceof TwoPlayerGame)
            ? "P1: WASD  |  P2: Arrow Keys\nNavigate to the correct flag — push to compete!"
            : "Arrow Keys to move  —  Capture the correct flag!";
        Label modeInstr = new Label(instrText);
        modeInstr.getStyleClass().add("arena-instruction");
        modeInstr.setWrapText(true);

        questionLabel = new Label();
        questionLabel.getStyleClass().add("question-text");
        questionLabel.setWrapText(true);
        questionLabel.setMaxWidth(348);

        // 4 read-only option labels styled as mission log entries
        VBox optionsBox = new VBox(7);
        for (int i = 0; i < 4; i++) {
            optionLabels[i] = new Label();
            optionLabels[i].getStyleClass().add("option-display-label");
            optionLabels[i].setWrapText(true);
            optionLabels[i].setMaxWidth(348);
            optionsBox.getChildren().add(optionLabels[i]);
        }

        // push feedback and next button to the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        feedbackLabel = new Label("Navigate your character to the correct flag!");
        feedbackLabel.getStyleClass().add("feedback-label");
        feedbackLabel.setWrapText(true);
        feedbackLabel.setMaxWidth(348);

        nextButton = new Button("Next Question ▶");
        nextButton.getStyleClass().add("next-button");
        nextButton.setVisible(false);
        nextButton.setOnAction(e -> displayQuestion());

        leftPanel.getChildren().addAll(
            modeInstr,
            new Separator(),
            questionLabel,
            optionsBox,
            spacer,
            new Separator(),
            feedbackLabel,
            nextButton
        );

        // ── RIGHT PANEL — 2D arena canvas ───────────────────────────────────────
        if (currentGame instanceof TwoPlayerGame) {
            TwoPlayerGame tpg = (TwoPlayerGame) currentGame;
            arenaPanel = new ArenaGamePanel(
                tpg.getPlayer1().getName(),
                tpg.getPlayer2().getName(),
                this::onArenaFlagCaptured
            );
        } else {
            // single-player and time-based share the single-entity constructor
            arenaPanel = new ArenaGamePanel(
                currentGame.getCurrentPlayer().getName(),
                this::onArenaFlagCaptured
            );
        }

        // Enhancement B: wire overlay-dismissed callback so timer starts after overlay
        arenaPanel.onOverlayDismissed = this::startQuestionTimer;

        // ── ASSEMBLE ────────────────────────────────────────────────────────────
        HBox mainArea = new HBox(0);
        mainArea.getChildren().addAll(leftPanel, arenaPanel);

        root.setTop(topBox);
        root.setCenter(mainArea);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        applyStylesheet(scene);

        // Key events must be registered on the scene and forwarded to the arena panel
        // (Canvas nodes do not receive keyboard focus directly)
        scene.setOnKeyPressed(e  -> { if (arenaPanel != null) arenaPanel.handleKeyPressed(e.getCode()); });
        scene.setOnKeyReleased(e -> { if (arenaPanel != null) arenaPanel.handleKeyReleased(e.getCode()); });

        primaryStage.setScene(scene);

        arenaPanel.start();

        // only the 60-second global clock runs in Time Based mode
        if (currentGame instanceof TimeBasedGame) {
            startGlobalTimer();
        }

        displayQuestion();
    }

    // ─── question display ───────────────────────────────────────────────────────

    /**
     * Loads the current question into the left panel and tells the arena to spawn flags.
     * Works for all three game modes — TriviaGame polymorphism handles the differences.
     */
    public void displayQuestion() {
        stopQuestionTimer();

        if (currentGame.isGameOver()) {
            if (arenaPanel != null) arenaPanel.stop();
            showLeaderboardScreen();
            return;
        }

        Question q = currentGame.getCurrentQuestion();
        if (q == null) {
            if (arenaPanel != null) arenaPanel.stop();
            showLeaderboardScreen();
            return;
        }

        // Update the top bar — two-player has a different label format
        if (currentGame instanceof TwoPlayerGame) {
            TwoPlayerGame tpg = (TwoPlayerGame) currentGame;
            playerLabel.setText("⚔ " + tpg.getAttackerName());
            defenderLabel.setText("🛡 " + tpg.getDefenderName());
            defenderLabel.setVisible(true);
            scoreLabel.setText(tpg.getScoreComparison());
        } else {
            playerLabel.setText("Player: " + currentGame.getCurrentPlayer().getName());
            defenderLabel.setVisible(false);
            scoreLabel.setText("Score: " + currentGame.getCurrentPlayer().getScore() + " pts");
        }
        progressLabel.setText(currentGame.getProgressString()
            + "  |  Mode: " + currentGame.getGameModeName()
            + "  |  Difficulty: " + currentGame.getDifficulty());

        // Update left panel question text
        questionLabel.setText("Topic: " + q.getTopic() + "\n\n" + q.getQuestionText());

        // Update option labels — True/False questions only have 2 options
        String[] options = q.getOptions();
        char[]   letters = {'A', 'B', 'C', 'D'};
        for (int i = 0; i < optionLabels.length; i++) {
            if (i < options.length) {
                optionLabels[i].setText(letters[i] + ".  " + options[i]);
                optionLabels[i].setVisible(true);
            } else {
                optionLabels[i].setVisible(false);  // hide C/D for True/False questions
            }
            // reset any highlighting from the previous question
            optionLabels[i].getStyleClass().removeAll(
                "option-display-correct", "option-display-incorrect");
            if (!optionLabels[i].getStyleClass().contains("option-display-label")) {
                optionLabels[i].getStyleClass().add("option-display-label");
            }
        }

        feedbackLabel.setText("Navigate your character to the correct flag!");
        feedbackLabel.getStyleClass().removeAll(
            "feedback-correct", "feedback-incorrect", "feedback-timeout");
        nextButton.setVisible(false);

        // In two-player mode, tell the arena which entity can capture this round
        int activeIdx = 0;
        if (currentGame instanceof TwoPlayerGame) {
            // read BEFORE submitAnswer() flips the index
            activeIdx = ((TwoPlayerGame) currentGame).getCurrentPlayerIndex();
        }

        arenaPanel.loadQuestion(q, activeIdx);
        if (currentGame instanceof TwoPlayerGame) {
            timerLabel.setText("⏱ --");
            timerLabel.getStyleClass().removeAll("timer-urgent");
            if (!timerLabel.getStyleClass().contains("timer-label")) {
                timerLabel.getStyleClass().add("timer-label");
            }
            // startQuestionTimer() is invoked by arenaPanel.onOverlayDismissed after 3s
        } else if (currentGame instanceof SinglePlayerGame) {
            // solo mode has no per-question timer
            timerLabel.setText("⏱ --");
            timerLabel.getStyleClass().removeAll("timer-urgent");
            if (!timerLabel.getStyleClass().contains("timer-label")) {
                timerLabel.getStyleClass().add("timer-label");
            }
        } else {
            startQuestionTimer();
        }
    }

    // ─── flag capture callback — fired by ArenaGamePanel ───────────────────────

    /**
     * Called by the arena when the active entity's center enters a flag's capture radius.
     * answerIndex matches the option index (0=A, 1=B, 2=C, 3=D).
     */
    private void onArenaFlagCaptured(int answerIndex) {
        stopQuestionTimer();
        arenaPanel.deactivate();  // stop accepting further captures for this question

        // record the answer — submitAnswer() updates scores and advances the question index
        currentGame.submitAnswer(answerIndex);
        Answer  answer  = currentGame.getLastAnswer();
        boolean correct = answer.isCorrect();

        // highlight the correct (and if wrong, also the chosen) option label
        int    correctIdx = answer.getQuestion().getCorrectIndex();
        for (int i = 0; i < optionLabels.length; i++) {
            optionLabels[i].getStyleClass().removeAll(
                "option-display-label", "option-display-correct", "option-display-incorrect");
            if (i == correctIdx) {
                optionLabels[i].getStyleClass().add("option-display-correct");
            } else if (i == answerIndex && !correct) {
                optionLabels[i].getStyleClass().add("option-display-incorrect");
            } else {
                optionLabels[i].getStyleClass().add("option-display-label");
            }
        }

        // trigger entity animation based on correctness
        if (correct) {
            arenaPanel.celebrateActiveEntity();
        } else {
            arenaPanel.zapActiveEntity();
        }

        // show feedback in the left panel
        feedbackLabel.setText(answer.getFeedback());
        feedbackLabel.getStyleClass().removeAll(
            "feedback-correct", "feedback-incorrect", "feedback-timeout");
        feedbackLabel.getStyleClass().add(correct ? "feedback-correct" : "feedback-incorrect");

        // refresh score display immediately
        if (currentGame instanceof TwoPlayerGame) {
            scoreLabel.setText(((TwoPlayerGame) currentGame).getScoreComparison());
        } else {
            scoreLabel.setText("Score: " + currentGame.getCurrentPlayer().getScore() + " pts");
        }

        nextButton.setVisible(true);
    }

    // ==========================================================================
    // TIMERS
    // ==========================================================================

    // per-question countdown for Two Player (30s) and Time Based (20s) modes
    // if it hits zero, we auto-submit with index -1 (always wrong) and zap the entity
    private void startQuestionTimer() {
        if (currentGame instanceof SinglePlayerGame) return;
        int duration = (currentGame instanceof TwoPlayerGame) ? 30 : 20;
        questionSecondsLeft = duration;
        timerLabel.setText("⏱ " + questionSecondsLeft + "s");
        timerLabel.getStyleClass().removeAll("timer-urgent");
        if (!timerLabel.getStyleClass().contains("timer-label")) {
            timerLabel.getStyleClass().add("timer-label");
        }

        questionTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            questionSecondsLeft--;
            timerLabel.setText("⏱ " + questionSecondsLeft + "s");

            // turn red at 5 seconds left
            if (questionSecondsLeft <= 5) {
                timerLabel.getStyleClass().removeAll("timer-label");
                timerLabel.getStyleClass().add("timer-urgent");
            }

            if (questionSecondsLeft <= 0) {
                stopQuestionTimer();
                handleTimeOut();
            }
        }));

        questionTimer.setCycleCount(duration);
        questionTimer.play();
    }

    // called when the 15-second question timer expires
    private void handleTimeOut() {
        arenaPanel.deactivate();
        arenaPanel.zapActiveEntity();  // entity gets zapped for not answering in time

        currentGame.submitAnswer(-1);  // -1 = timeout, always recorded as wrong
        Answer answer = currentGame.getLastAnswer();

        // highlight the correct option so the player learns even when they time out
        int correctIdx = answer.getQuestion().getCorrectIndex();
        for (int i = 0; i < optionLabels.length; i++) {
            optionLabels[i].getStyleClass().removeAll(
                "option-display-label", "option-display-correct", "option-display-incorrect");
            if (i == correctIdx) {
                optionLabels[i].getStyleClass().add("option-display-correct");
            } else {
                optionLabels[i].getStyleClass().add("option-display-label");
            }
        }

        feedbackLabel.setText("Time's up!  " + answer.getFeedback());
        feedbackLabel.getStyleClass().removeAll("feedback-correct", "feedback-incorrect");
        feedbackLabel.getStyleClass().add("feedback-timeout");

        if (currentGame instanceof TwoPlayerGame) {
            scoreLabel.setText(((TwoPlayerGame) currentGame).getScoreComparison());
        } else {
            scoreLabel.setText("Score: " + currentGame.getCurrentPlayer().getScore() + " pts");
        }

        nextButton.setVisible(true);
    }

    private void stopQuestionTimer() {
        if (questionTimer != null) questionTimer.stop();
    }

    // 60-second global timer — only used in Time Based mode
    // checks every second if the clock has run out and ends the game
    private void startGlobalTimer() {
        globalTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            TimeBasedGame tbg = (TimeBasedGame) currentGame;
            progressLabel.setText(tbg.getProgressString()
                + "  |  Mode: Time Based"
                + "  |  Difficulty: " + tbg.getDifficulty());

            if (tbg.isTimeUp()) {
                stopAllTimers();
                tbg.endGame();
                showLeaderboardScreen();
            }
        }));
        globalTimer.setCycleCount(Timeline.INDEFINITE);
        globalTimer.play();
    }

    private void stopAllTimers() {
        stopQuestionTimer();
        if (globalTimer != null) {
            globalTimer.stop();
            globalTimer = null;
        }
        // stop and release the arena panel so callbacks can't fire after screen change
        if (arenaPanel != null) {
            arenaPanel.stop();
            arenaPanel = null;
        }
    }

    // ==========================================================================
    // LEADERBOARD SCREEN
    // ==========================================================================

    public void showLeaderboardScreen() {
        stopAllTimers();
        currentGame.endGame();  // finalize scores and populate the leaderboard

        BorderPane root = new BorderPane();
        root.getStyleClass().add("leaderboard-root");

        // header
        VBox headerBox = new VBox(6);
        headerBox.getStyleClass().add("leaderboard-header");
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(24, 20, 18, 20));

        Label lbTitle = new Label("Final Leaderboard");
        lbTitle.getStyleClass().add("lb-title");

        Label modeLabel = new Label("Mode: " + currentGame.getGameModeName()
            + "  |  Difficulty: " + currentGame.getDifficulty().toUpperCase());
        modeLabel.getStyleClass().add("lb-mode-label");

        Label winnerLabel = new Label(buildWinnerText());
        winnerLabel.getStyleClass().add("winner-label");
        winnerLabel.setWrapText(true);
        winnerLabel.setMaxWidth(700);
        winnerLabel.setAlignment(Pos.CENTER);

        headerBox.getChildren().addAll(lbTitle, modeLabel, winnerLabel);

        // ranked player rows
        VBox centerBox = new VBox(12);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(18, 60, 18, 60));

        Label rankHeader = new Label("Player Rankings");
        rankHeader.getStyleClass().add("section-header");
        centerBox.getChildren().add(rankHeader);

        ArrayList<Player> ranked = currentGame.getLeaderboard().getRankedPlayers();
        String[] medals = {"🥇", "🥈", "🥉"};

        for (int i = 0; i < ranked.size(); i++) {
            Player p     = ranked.get(i);
            String medal = (i < medals.length) ? medals[i] : (i + 1) + ".";

            HBox row = new HBox(14);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 18, 10, 18));
            row.getStyleClass().add("rank-row");
            if (i == 0) row.getStyleClass().add("rank-first");

            Label medalLbl = new Label(medal);
            medalLbl.setFont(Font.font(20));
            medalLbl.setMinWidth(36);

            Label nameLbl = new Label(p.getName());
            nameLbl.getStyleClass().add("rank-name");
            nameLbl.setMinWidth(160);

            Label scoreLbl = new Label(p.getScore() + " pts");
            scoreLbl.getStyleClass().add("rank-score");
            scoreLbl.setMinWidth(80);

            Label accLbl = new Label(String.format("%.0f%% accuracy  (%d/%d correct)",
                p.getAccuracy(), p.getCorrectAnswers(), p.getQuestionsAnswered()));
            accLbl.getStyleClass().add("rank-accuracy");

            row.getChildren().addAll(medalLbl, nameLbl, scoreLbl, accLbl);
            centerBox.getChildren().add(row);
        }

        // sustainability fact — index varies by winner score so it changes each game
        VBox factBox = new VBox(6);
        factBox.getStyleClass().add("fact-box");
        factBox.setAlignment(Pos.CENTER);
        factBox.setPadding(new Insets(14));
        factBox.setMaxWidth(700);

        Label factHeader = new Label("Sustainability Fact of the Day");
        factHeader.getStyleClass().add("fact-header");

        String[] facts = {
            "Recycling one aluminum can saves enough energy to power a TV for 3 hours!",
            "Turning off lights when leaving a room can reduce your electricity bill by up to 15%.",
            "A plant-based diet can reduce your carbon footprint by up to 73%!",
            "Cycling instead of driving reduces CO2 emissions for that trip by nearly 100%.",
            "The average American generates about 4.4 lbs of trash per day.",
            "Only about 0.3% of Earth's water is accessible fresh water for human use.",
            "Storm drain water on the UST campus flows directly into the Mississippi River."
        };

        Player winner    = currentGame.getLeaderboard().getWinner();
        int    factIndex = (winner != null) ? winner.getScore() % facts.length : 0;

        Label factLabel = new Label(facts[factIndex]);
        factLabel.getStyleClass().add("fact-text");
        factLabel.setWrapText(true);
        factLabel.setMaxWidth(650);
        factLabel.setAlignment(Pos.CENTER);

        factBox.getChildren().addAll(factHeader, factLabel);
        centerBox.getChildren().add(factBox);

        // navigation buttons
        HBox btnBox = new HBox(20);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(16));

        Button playAgainBtn = new Button("▶ Play Again");
        playAgainBtn.getStyleClass().add("play-again-button");
        playAgainBtn.setOnAction(e -> {
            currentGame.resetGame();
            currentGame.startGame();
            showGameScreen();
        });

        Button homeBtn = new Button("🏠 Main Menu");
        homeBtn.getStyleClass().add("home-button");
        homeBtn.setOnAction(e -> showWelcomeScreen());

        btnBox.getChildren().addAll(playAgainBtn, homeBtn);

        root.setTop(headerBox);
        root.setCenter(centerBox);
        root.setBottom(btnBox);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        applyStylesheet(scene);
        primaryStage.setScene(scene);
    }

    // builds the winner announcement line for the leaderboard header
    private String buildWinnerText() {
        if (currentGame instanceof TwoPlayerGame) {
            TwoPlayerGame tpg    = (TwoPlayerGame) currentGame;
            Player        winner = tpg.getWinner();
            if (winner != null) {
                return "Winner: " + winner.getName() + " with " + winner.getScore() + " points!";
            } else {
                return "It's a TIE!  Both players scored " + tpg.getPlayer1().getScore() + " points.";
            }
        } else {
            Player winner = currentGame.getLeaderboard().getWinner();
            if (winner != null) {
                return "Great job, " + winner.getName() + "!  Final Score: " + winner.getScore() + " points.";
            }
        }
        return "Game Complete!";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
