import java.io.*;
import java.util.*;

public class QuestionManager {
    private List<Question> questions;

    public QuestionManager(String propertiesFilePath) throws IOException {
        questions = new ArrayList<>();
        Properties properties = new Properties();

        // Läs frågorna från properties-filen
        try (FileInputStream fis = new FileInputStream(propertiesFilePath)) {
            properties.load(fis);
        }

        // Skapa frågorna
        int index = 1;
        while (properties.containsKey("question" + index)) {
            String questionText = properties.getProperty("question" + index);
            String[] options = properties.getProperty("options" + index).split(",");
            int correctAnswer = Integer.parseInt(properties.getProperty("answer" + index));

            questions.add(new Question(questionText, options, correctAnswer));
            index++;
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void getQuestion(String category) {

    }
}
