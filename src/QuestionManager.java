import java.io.*;
import java.util.*;
//2
public class QuestionManager {

    private List<Question> questions; // List to store questions for the current category
    private Properties properties;

    public QuestionManager(String propertiesFilePath) throws IOException {
        questions = new ArrayList<>();
        properties = new Properties();

        // Load questions from the specific properties file
        try (FileInputStream fis = new FileInputStream(propertiesFilePath)) {
            properties.load(fis);
        }

        loadQuestions(); // Populate the `questions` list
    }

    private void loadQuestions() {
        questions.clear(); // Clear any existing questions to avoid mixing categories

        int index = 1;
        while (properties.containsKey("question" + index)) {
            String questionText = properties.getProperty("question" + index);
            String[] options = properties.getProperty("options" + index).split(",");
            int correctAnswer = Integer.parseInt(properties.getProperty("answer" + index));

            questions.add(new Question(questionText, options, correctAnswer));
            index++;
        }

        System.out.println("Laddade " + questions.size() + " frågor.");
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
