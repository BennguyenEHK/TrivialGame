/**
 * Question.java
 * Holds everything about one trivia question:
 * the text, the answer choices, which one is correct, the topic, and difficulty.
 * I made two constructors — one for multiple choice and one for true/false.
 *
 * Author: Sefan Adinew
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package lab9;

public class Question {

    // all fields are private so nothing outside this class can change them directly
    private String topic;         // e.g. "Energy", "Biking", "Recycling"
    private String questionText;  // the actual question shown to the player
    private String[] options;     // the answer choices (A, B, C, D or True/False)
    private int correctIndex;     // which index in options[] is the right answer
    private String difficulty;    // "easy", "medium", or "hard"
    private String questionType;  // "multiple_choice" or "true_false"

    // constructor for multiple choice questions
    public Question(String topic, String questionText, String[] options,
                    int correctIndex, String difficulty) {
        this.topic        = topic;
        this.questionText = questionText;
        this.options      = options;
        this.correctIndex = correctIndex;
        this.difficulty   = difficulty;
        this.questionType = "multiple_choice";
    }

    // constructor for true/false questions
    // automatically sets up the options array and picks the correct index
    public Question(String topic, String questionText, boolean isTrue, String difficulty) {
        this.topic        = topic;
        this.questionText = questionText;
        this.options      = new String[]{"True", "False"};
        this.correctIndex = isTrue ? 0 : 1;
        this.difficulty   = difficulty;
        this.questionType = "true_false";
    }

    // getters — only way to read the private fields
    public String getTopic()        { return topic; }
    public String getQuestionText() { return questionText; }
    public String[] getOptions()    { return options; }
    public int getCorrectIndex()    { return correctIndex; }
    public String getDifficulty()   { return difficulty; }
    public String getQuestionType() { return questionType; }

    // returns true if the player picked the right answer
    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctIndex;
    }

    // returns the actual text of the correct answer
    public String getCorrectAnswerText() {
        return options[correctIndex];
    }

    // setter with validation — only accepts known difficulty values
    public void setDifficulty(String difficulty) {
        if (difficulty.equals("easy") || difficulty.equals("medium") || difficulty.equals("hard")) {
            this.difficulty = difficulty;
        }
    }

    // useful for debugging — shows the topic and question text
    @Override
    public String toString() {
        return "[" + difficulty.toUpperCase() + " - " + topic + "] " + questionText;
    }
}
