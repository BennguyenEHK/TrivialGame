Project Instructions
Introduction
Welcome to the CISC 230 Semester Project! You will apply and enhance your understanding of object-oriented programming concepts in this Project.

The Sustainability Trivia Game is an interactive application developed in collaboration with the Student Sustainability Leadership Program (SSLP) to raise awareness about sustainability topics such as environmental conservation, recycling, and energy efficiency. Designed to educate and engage players during campus outreach events and classroom activities, the game challenges users with trivia questions in a fun, competitive format. Players can select from single-player, two-player, or time-based gameplay modes, making it versatile for various audiences.

The project integrates vital Object-Oriented Programming (OOP) principles—abstraction, encapsulation, inheritance, and polymorphism—while featuring a visually appealing Graphical User Interface (GUI) to enhance user experience. By designing this game, students will apply programming concepts in a practical, real-world context and contribute to SSLP’s mission of fostering sustainability knowledge and action. Deliverables include a UML diagram, a brief slide presentation, and a video demo showcasing the game’s functionality and OOP-based design.

Objectives
By the end of this lab, you will be able to:

Design and Implement: Create a Sustainability Trivia Game in collaboration with the Student Sustainability Leadership Program (SSLP).
Purpose: Develop a game to be used in sustainability awareness programs to educate and engage participants on topics like energy conservation, recycling, and sustainable actions.
OOP Principles: Apply all four pillars of Object-Oriented Programming (OOP)—abstraction, encapsulation, inheritance, and polymorphism—while integrating a user-friendly graphical interface (GUI).
Gameplay Options: Design the game with multiple modes, including single-player, two-player, and time-based gameplay, ensuring versatility and replayability.
Documentation and Demonstration: Document the implementation process and present the game’s functionality and structure through a video demo.
Hand-In Instructions
Please submit the source code from this lab at the appropriate location on the Canvas system. You should submit a compressed/archived file named Lab_X_< your first name_your last name>.zip (without angle brackets and X replaced with the lab number e.g Lab1_PakeezaAkram.zip) that contains ONLY the following files:

Completed Java Source Files:
All Java source files representing the work accomplished for this lab should be included.
Each file should contain the author's name in the comments at the top.
A detailed UML diagram showing the relationships among all classes and interfaces.
A slide deck (5-10 slides) explaining the structure, design choices, and application of OOP principles.
A video demo (less than 5-10 minutes) showcasing:
The game's GUI.
Different gameplay modes.
A voiceover explaining how OOP principles are applied in the code and how the game contributes to sustainability awareness.
 2. Honor code and Reflection Statement File:

Submit the honorcode.txt file, which you must fill out. The file specimen is provided.
3. UML Diagrams

Submit a UML Diagram as an image wherever required.
4. To Receive Credit

Use Comments Heavily:
Use intelligent comments and maintain clean, readable formatting of your code. Comments and code formatting account for 30% of your course grade.
Review the Coding Assignment Rubrics:
Ensure you review and adhere to the coding assignment rubrics provided by your instructor.
Task:
Requirements:

OOP Principles Implementation
Use abstraction to define shared game behaviors in an abstract class (e.g., TriviaGame).
Apply encapsulation by securing data fields in classes and exposing behavior through public methods.
Demonstrate inheritance by creating specific game modes (e.g., SinglePlayerGame, TwoPlayerGame) derived from a common abstract class.
Showcase polymorphism by overriding methods in derived classes and using dynamic method dispatch.
Class Requirements
Question Class: Handles trivia question details, such as the question text, answer options, and the correct answer.
Answer Class: Manages and validates the player’s responses to questions.
Player Class: Tracks individual player details such as name and score.
Leaderboard Class: Maintains a list of players and displays the scores in ranked order.
Ensure at least one interface is used to enforce functionality such as score tracking or GUI rendering.
Gameplay Features
Choose TWO mode, but it should have three levels of difficulty.
Single-player Mode: A solo mode where the player answers questions and tries to achieve the highest score.
Two-player Mode: A competitive mode where two players take turns answering questions.
Time-based Mode: A mode where players must answer as many questions as possible within a time limit.
Graphical User Interface (GUI)
Use a GUI library (e.g., JavaFX) to design an interactive and visually appealing interface.
Provide:
A welcome screen with game mode options.
A screen for displaying questions, answer options, and player feedback.
A final leaderboard screen to display scores and rankings.
Content and Sustainability Focus
Use sustainability-related questions provided in the lab document.
Ensure the content aligns with SSLP's goals to educate players about campus sustainability initiatives, energy conservation, recycling, and sustainable actions.
 

Project Steps

Step 1: Understand the Sustainability Content

Familiarize yourself with the provided trivia questions and answers.
Focus on energy conservation, recycling, and SSLP initiatives to ensure the game content aligns with the sustainability theme.
Step 2: Plan the Game Structure

Use the software (e.g., Lucidchart, draw.io) to draft the UML diagram.
Identify:
The abstract class to define common behaviors (e.g., TriviaGame).
Concrete subclasses for specific game modes (e.g., SinglePlayerGame, TwoPlayerGame, TimeBasedGame).
Relationships between classes like Question, Answer, Player, and Leaderboard.
Interfaces for enforcing specific functionalities (e.g., Scorable, Displayable).
Step 3: Implement the Game

Define the core game logic using the identified classes and OOP principles.
Use encapsulation to secure fields in classes such as Question and Player.
Implement inheritance by creating a hierarchy of classes for different game modes.
Apply polymorphism by overriding methods in derived classes (e.g., scoring mechanisms or game logic).
Step 4: Design the GUI

Choose a Java GUI library (JavaFX is recommended).
Create a welcome screen where players can select their desired game mode.
Design the question screen to display the current question, answer options, and feedback.
Include a final leaderboard screen to display player rankings after the game ends.
Use colors, fonts, and layouts to make the interface visually appealing and user-friendly.
Step 5: Test the Game

Test all three gameplay modes to ensure functionality and user experience.
Debug any errors and ensure smooth transitions between screens in the GUI.
Step 6: Bring Uniqueness:

Highlight at least one thing you are doing uniquely and are proud of. This can be related to design, GUI, or new features you learned on your own and implemented in the lab.
Step 7: Prepare Deliverables

Create a UML diagram that clearly illustrates the game structure and relationships.
Develop a short presentation (2-5 slides) explaining the structure, gameplay, and OOP principles used.
Record a video demo (less than 5 minutes):
Show the game in action.
Explain how OOP principles are applied in the design.
Highlight how the game aligns with SSLP’s goals.
Presentation by both members of not more than 10-12 minutes to explain the project. Highlight the unique feature.
Slide 1: Title slide (30 seconds)
Slide 2: UML Diagram (120 seconds)
Slide 3: GUI features Used (60 seconds)
Slide 4: Unique Feature implemented (60 seconds)
Slide 5: Project Demo. (Most of the time)
Deliverables Checklist

Java files
Well commented and written
Must follow best practices
UML Diagram:
Clearly labeled classes, attributes, and methods.
Relationships between classes (e.g., inheritance, aggregation, composition).
Interfaces and their implementations.
Slide Deck:
Title slide with your name and project title.
A slide describing the game’s structure and class hierarchy.
A slide explaining how OOP principles are implemented.
A final slide detailing how the game supports sustainability awareness.
Video Demo:
Show gameplay in all three modes (single-player, two-player, and time-based).
Explain the GUI design and functionality.
Discuss the implementation of OOP principles and sustainability content.
You can test AI usage to enhance GUI ONLY but be ready to answer any questions needed to answer. Meaning KNOW YOUR STUFF.
Evaluation Criteria

Code Quality (30%):
Adherence to OOP principles.
Proper encapsulation, method usage, and comments.
Functionality (30%):
Smooth gameplay in all three modes.
Correct scoring and leaderboard logic.
GUI Design (20%):
Aesthetic and user-friendly interface.
Appropriate feedback for user actions.
Deliverables (20%):
Clear and accurate UML diagram.
Well-prepared slides.
Informative and engaging video demo.
Well prepared presentation by both members.