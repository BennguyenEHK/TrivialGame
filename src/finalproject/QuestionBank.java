/**
 * QuestionBank.java
 * Stores all the sustainability trivia questions for the game.
 * Questions come directly from the SSLP Sustainability Trivia Game 2026 document.
 * getGameQuestions() shuffles the pool so every game is different.
 *
 * Author: Sefan Adinew
 * Course: CISC230 - Object Oriented Design and Programming
 * Semester: Spring 2026
 */
package finalproject;
import java.util.ArrayList;
import java.util.Collections;

public class QuestionBank {

    private ArrayList<Question> allQuestions;  // master list of all questions

    // constructor loads everything immediately
    public QuestionBank() {
        allQuestions = new ArrayList<Question>();
        loadQuestions();
    }

    // all questions are hardcoded here from the SSLP document
    // topics: Energy, Pollinator Path, Biking, Food, Water,
    //         Academics/Research, Engagement, Reuse, Recycling, Organics, Specialized
    private void loadQuestions() {

        // ---- ENERGY ----
        allQuestions.add(new Question(
            "Energy",
            "Which of the following is NOT a way to conserve energy?",
            new String[]{
                "Unplugging any devices when not in use.",
                "Turning off lights when leaving the room.",
                "Keeping windows closed to retain heat indoors in the winter.",
                "Washing laundry in small loads instead of full loads."
            }, 3, "easy"));

        allQuestions.add(new Question(
            "Energy",
            "Keeping blinds closed during the hottest parts of the day to limit excess heat in the summer is a way to conserve energy.",
            true, "easy"));

        allQuestions.add(new Question(
            "Energy",
            "Washing full loads of laundry saves energy because each load takes energy to wash.",
            true, "hard"));

        // ---- POLLINATOR PATH ----
        allQuestions.add(new Question(
            "Pollinator Path",
            "What is the name of the series of gardens around campus that attract pollinators and supports the study of pollinator activity on campus?",
            new String[]{
                "Bee Garden",
                "Pollinator Path",
                "Butterfly Garden",
                "Bee Path"
            }, 1, "easy"));

        allQuestions.add(new Question(
            "Pollinator Path",
            "The Pollinator Path is a series of gardens around campus which attract pollinators and support the study of pollinator activity on campus.",
            true, "easy"));

        // ---- BIKING ----
        allQuestions.add(new Question(
            "Biking",
            "Where is there a bike repair station located on campus?",
            new String[]{
                "On the south side of O'Shaughnessy Stadium.",
                "Inside the secure bike storage in Frey Residence Hall.",
                "Inside the secure bike storage in Schoenecker Hall North.",
                "All of the above."
            }, 3, "easy"));

        allQuestions.add(new Question(
            "Biking",
            "A bike repair station is located between ASC and O'Shaughnessy Stadium.",
            true, "easy"));

        allQuestions.add(new Question(
            "Biking",
            "St. Thomas provides secure indoor bike storage for students in Frey Residence Hall.",
            true, "medium"));

        // ---- FOOD ----
        allQuestions.add(new Question(
            "Food",
            "Which of the following is NOT a helpful tip for reducing food waste?",
            new String[]{
                "Storing produce properly.",
                "Planning meals at the beginning of the week.",
                "Only purchasing what you need.",
                "Storing all items from the store in the refrigerator."
            }, 3, "easy"));

        allQuestions.add(new Question(
            "Food",
            "In The View and the Northsider, you can mix and match food from different stations to make the perfect meal for you.",
            true, "medium"));

        // ---- WATER ----
        allQuestions.add(new Question(
            "Water",
            "Which of the following is NOT a way to conserve water?",
            new String[]{
                "Eating more plant-based meals.",
                "Taking shorter showers.",
                "Leaving the sink on while brushing your teeth.",
                "Only washing full loads of laundry."
            }, 2, "easy"));

        allQuestions.add(new Question(
            "Water",
            "The water that collects in the storm drains located on campus and the surrounding streets ends up in the Mississippi River.",
            true, "medium"));

        // ---- ACADEMICS / RESEARCH ----
        allQuestions.add(new Question(
            "Academics/Research",
            "The Sustainability minor is available to students in which of the following fields?",
            new String[]{
                "Arts and Sciences",
                "Engineering",
                "Business",
                "All of the above and more"
            }, 3, "medium"));

        allQuestions.add(new Question(
            "Academics/Research",
            "The Sustainability Scholars research designation through the Undergraduate Research Opportunities Program is open to students in all fields and majors.",
            true, "medium"));

        // ---- ENGAGEMENT ----
        allQuestions.add(new Question(
            "Engagement",
            "Which of the following are clubs at St. Thomas?",
            new String[]{
                "Sustainability Club",
                "Earth, Environment, and Society Club",
                "Tommie Outdoors",
                "All of the above"
            }, 3, "medium"));

        allQuestions.add(new Question(
            "Engagement",
            "The Undergraduate Student Government has a Sustainability Committee Chair position that is elected each year.",
            true, "medium"));

        // ---- REUSE ----
        allQuestions.add(new Question(
            "Reuse",
            "Dining Services reduces waste by...",
            new String[]{
                "Safely recovering leftover food to be donated.",
                "Offering reusable to-go containers in T's.",
                "Offering a discount for bringing your own reusable cup to campus coffee shops.",
                "All of the above."
            }, 3, "medium"));

        // false — Tommies Closet is FREE, not discounted
        allQuestions.add(new Question(
            "Reuse",
            "Tommies Closet is a monthly pop-up that allows students to shop for secondhand clothes donated by fellow Tommies for discount prices.",
            false, "medium"));

        allQuestions.add(new Question(
            "Reuse",
            "Tommies Closet is free for students to shop.",
            true, "hard"));

        // ---- RECYCLING ----
        allQuestions.add(new Question(
            "Recycling",
            "At St. Thomas, which item is accepted for recycling in the blue recycling bins around campus?",
            new String[]{
                "Glass",
                "Lightbulbs",
                "Plastic bags",
                "Batteries"
            }, 0, "hard"));

        allQuestions.add(new Question(
            "Recycling",
            "Plastic bags can be recycled in the blue recycling bins around campus.",
            false, "hard"));

        // ---- ORGANICS RECYCLING ----
        allQuestions.add(new Question(
            "Organics Recycling",
            "All of the following items are accepted for organics recycling except...",
            new String[]{
                "All food scraps",
                "Napkins",
                "All paper cups",
                "Flower trimmings"
            }, 2, "hard"));

        allQuestions.add(new Question(
            "Organics Recycling",
            "At St. Thomas, items placed in the green organics recycling bins are turned into compost that can be used in gardens and lawns.",
            true, "hard"));

        // ---- SPECIALIZED RECYCLING ----
        allQuestions.add(new Question(
            "Specialized Recycling",
            "Where is there a specialized recycling bin located on campus?",
            new String[]{
                "Outside the Campus Store in Murray-Herrick",
                "In the entrance to the Facilities and Design Center",
                "In the create[space]",
                "All of the above"
            }, 3, "hard"));

        allQuestions.add(new Question(
            "Specialized Recycling",
            "Plastic bags are accepted for recycling at the specialized recycling stations on campus.",
            true, "hard"));
    }

    // returns all questions unfiltered
    public ArrayList<Question> getAllQuestions() {
        return new ArrayList<Question>(allQuestions);
    }

    // filters by difficulty using a regular for loop
    public ArrayList<Question> getQuestionsByDifficulty(String difficulty) {
        ArrayList<Question> filtered = new ArrayList<Question>();
        for (int i = 0; i < allQuestions.size(); i++) {
            if (allQuestions.get(i).getDifficulty().equalsIgnoreCase(difficulty)) {
                filtered.add(allQuestions.get(i));
            }
        }
        return filtered;
    }

    // the main method used by each game mode to get questions
    // shuffles so the order is random every game
    // pass count = -1 to get the full pool (used by Time Based mode)
    public ArrayList<Question> getGameQuestions(String difficulty, int count) {
        ArrayList<Question> pool = getQuestionsByDifficulty(difficulty);

        // fallback: if nothing matches, use everything
        if (pool.isEmpty()) {
            pool = new ArrayList<Question>(allQuestions);
        }

        Collections.shuffle(pool);

        if (count == -1) return pool;  // Time Based mode gets the whole pool

        int limit = Math.min(count, pool.size());
        return new ArrayList<Question>(pool.subList(0, limit));
    }

    public int getTotalCount() {
        return allQuestions.size();
    }
}
