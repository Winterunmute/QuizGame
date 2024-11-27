import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class QuestionManager {

    private List<Question> allQuestions;

    public QuestionManager(String propertiesFileName) throws IOException {
        allQuestions = new ArrayList<>();
        Properties properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("questions.properties")) {
            if (input == null) {
                throw new FileNotFoundException("Kunde inte hitta filen: " + propertiesFileName);
            }
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        }

        // Skapa alla frågor och lagra dem i allQuestions
        int index = 1;
        while (properties.containsKey("question" + index)) {
            String questionText = properties.getProperty("question" + index);
            String[] options = properties.getProperty("options" + index).split(",");
            int correctAnswer = Integer.parseInt(properties.getProperty("answer" + index));
            String category = properties.getProperty("category" + index);

            Question question = new Question(questionText, options, correctAnswer, category);
            allQuestions.add(question);

            index++;
        }

    }

    // Returnerar alla frågor
    public List<Question> getAllQuestions() {
        return new ArrayList<>(allQuestions); // Returnera en kopia för att undvika oavsiktliga ändringar
    }

    // Returnerar frågor för en specifik kategori
    public List<Question> getQuestionsByCategory(String categoryFilter) {
        List<Question> filteredQuestions = new ArrayList<>();
        for (Question question : allQuestions) {
            if (question.getCategory().equalsIgnoreCase(categoryFilter)) {
                filteredQuestions.add(question);
            }
        }
        return filteredQuestions;
    }

    public List<String> getAllCategories() {
        Set<String> categories = new HashSet<>();
        for (Question question : allQuestions) {
            categories.add(question.getCategory());
        }
        return new ArrayList<>(categories);
    }
}
