@echo off
set JDK="C:\Program Files\Java\latest\jdk-25\bin"
set FX_PATH="C:\Users\LENOVO\Downloads\javafx\lib"
set OUT=bin

if not exist %OUT% mkdir %OUT%

echo Compiling...
%JDK%\javac --module-path %FX_PATH% --add-modules javafx.controls,javafx.fxml,javafx.media -d %OUT% src\finalproject\Scorable.java src\finalproject\Displayable.java src\finalproject\Question.java src\finalproject\Answer.java src\finalproject\Player.java src\finalproject\Leaderboard.java src\finalproject\TriviaGame.java src\finalproject\SinglePlayerGame.java src\finalproject\TwoPlayerGame.java src\finalproject\TimeBasedGame.java src\finalproject\QuestionBank.java src\finalproject\ArenaEntity.java src\finalproject\ArenaFlag.java src\finalproject\ArenaGamePanel.java src\finalproject\SustainabilityTriviaApp.java

if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b 1
)

if not exist %OUT%\finalproject mkdir %OUT%\finalproject
copy src\finalproject\styles.css %OUT%\finalproject\ >nul 2>&1

echo Running...
%JDK%\java --module-path %FX_PATH% --add-modules javafx.controls,javafx.fxml,javafx.media --enable-native-access=javafx.graphics -cp %OUT% finalproject.SustainabilityTriviaApp

pause
