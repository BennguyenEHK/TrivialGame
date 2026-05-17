# Sustainability Trivia Game

A JavaFX trivia game with a 2D flag-capture arena. Three game modes: Single Player, Two Player (turn-based), and Time Based.

**Author:** Sefan Adinew — CISC 230, Spring 2026

---

## Setup in Eclipse

### 1. Import the Project

`File > Import > General > Existing Projects into Workspace` → select the cloned folder → Finish.

### 2. Add JavaFX to the Build Path

`Right-click project > Properties > Java Build Path > Libraries tab > Add JARs...`

Navigate into the `lib/` folder inside the project and select **all `.jar` files**, then click OK → Apply and Close.

### 3. Add VM Arguments to the Run Configuration

`Run > Run Configurations > Java Application > (your config) > Arguments tab > VM arguments:`

```
--module-path lib --add-modules javafx.controls,javafx.fxml,javafx.media --enable-native-access=javafx.graphics
```

> If Eclipse does not resolve the `lib` relative path, use the absolute path to the `lib/` folder instead:
> `--module-path "C:/path/to/project/lib" --add-modules javafx.controls,javafx.fxml,javafx.media --enable-native-access=javafx.graphics`

### 4. Set the Main Class

In the same Run Configuration, `Main tab > Main class:`

```
lab9.SustainabilityTriviaApp
```

### 5. Run

Click **Run**. The welcome screen will appear.

---

## Project Structure

```
src/lab9/          Java source files
lib/               JavaFX 25 JARs (included — no download needed)
Project-instrucitonns.md   Assignment requirements
```

## Controls (Arena)

| Player | Move |
|--------|------|
| Player 1 | W A S D |
| Player 2 | Arrow Keys |

Navigate your character to the flag labelled with your answer choice.
