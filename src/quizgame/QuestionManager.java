package quizgame;

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

    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Question> getQuestion(String categoryFilter) {
        // Skapa frågorna
        int index = 1;
        while (properties.containsKey("question" + index)) {
            // Kontrollera frågans kategori
            String category = properties.getProperty("category" + index);

            if (category != null && category.equalsIgnoreCase(categoryFilter)) {
                // Skapa frågan om kategorin matchar
                String questionText = properties.getProperty("question" + index);
                String[] options = properties.getProperty("options" + index).split(",");
                int correctAnswer = Integer.parseInt(properties.getProperty("answer" + index));

                questions.add(new Question(questionText, options, correctAnswer));
            }

            index++;
        }

        return questions;
    }
}
