public class Question {
    private String question;
    private String[] options;
    private int correctAnswer; // Index 1-baserat
    private String category;

    public Question(String question, String[] options, int correctAnswer, String category) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public String getCategory() {
        return category;
    }
}
