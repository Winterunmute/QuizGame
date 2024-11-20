import java.io.*;
import java.util.*;

public class QuestionManager {
    private List<Question> questions;
    private Properties properties;

    public QuestionManager(String propertiesFilePath) throws IOException {
        questions = new ArrayList<>();
        properties = new Properties();

        // Läs frågorna från properties-filen
        try (FileInputStream fis = new FileInputStream(propertiesFilePath)) {
            properties.load(fis);
        }

        parseQuestions();
    }

    // Returnera alla frågor för en viss kategori
    public List<Question> getQuestionsByCategory(String category) {
        List<Question> filteredQuestions = new ArrayList<>();
        for (Question question : questions) {
            if (question.getCategory().equals(category)) {
                filteredQuestions.add(question);
            }
        }
        return filteredQuestions;
    }

    private void parseQuestions() {
        int index = 1;
        while (properties.containsKey("question" + index)) {
            String questionText = properties.getProperty("question" + index);
            String[] options = properties.getProperty("options" + index).split(",");
            int correctAnswer = Integer.parseInt(properties.getProperty("answer" + index).trim());
            String category = properties.getProperty("category" + index).trim();

            questions.add(new Question(questionText, options, correctAnswer, category));
            index++;
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
