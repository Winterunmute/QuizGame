// Klass som representerar en fråga i spelet med tillhörande svarsalternativ
public class Question {
    // Frågans text
    private String question;

    // Array med svarsalternativ
    private String[] options;

    // Index för det korrekta svaret (1-baserat)
    private int correctAnswer;

    // Frågans kategori
    private String category;

    // Konstruktor som skapar en ny fråga med alla nödvändiga delar
    public Question(String question, String[] options, int correctAnswer, String category) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
    }

    // Hämtar frågans text
    public String getQuestion() {
        return question;
    }

    // Hämtar array med svarsalternativ
    public String[] getOptions() {
        return options;
    }

    // Hämtar index för det korrekta svaret
    public int getCorrectAnswer() {
        return correctAnswer;
    }

    // Hämtar frågans kategori
    public String getCategory() {
        return category;
    }
}
