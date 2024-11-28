import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class QuestionManager {

    // Lista med alla tillgängliga frågor
    private List<Question> allQuestions;

    // Konstruktor som läser in frågor från angiven fil
    public QuestionManager(String propertiesFileName) throws IOException {
        allQuestions = new ArrayList<>();
        Properties properties = new Properties();

        // Försök läsa in properties-filen
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("questions.properties")) {
            if (input == null) {
                throw new FileNotFoundException("Kunde inte hitta filen: " + propertiesFileName);
            }
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        }

        // Skapa frågor från properties och lägg till i listan
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

    // Returnerar en kopia av alla frågor
    public List<Question> getAllQuestions() {
        return new ArrayList<>(allQuestions);
    }

    // Filtrerar och returnerar frågor för en specifik kategori
    public List<Question> getQuestionsByCategory(String categoryFilter) {
        List<Question> filteredQuestions = new ArrayList<>();
        for (Question question : allQuestions) {
            if (question.getCategory().equalsIgnoreCase(categoryFilter)) {
                filteredQuestions.add(question);
            }
        }
        return filteredQuestions;
    }

    // Hämtar alla unika kategorier som finns bland frågorna
    public List<String> getAllCategories() {
        Set<String> categories = new HashSet<>();
        for (Question question : allQuestions) {
            categories.add(question.getCategory());
        }
        return new ArrayList<>(categories);
    }
}
