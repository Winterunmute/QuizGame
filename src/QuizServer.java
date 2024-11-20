import java.io.*;
import java.net.*;
import java.util.*;
//2

public class QuizServer {
    private static final String CATEGORY_DIRECTORY = "src/"; // Directory where properties files are stored
    private static final String DEFAULT_CATEGORY = "Historia"; // Default category
    private List<Question> questions; // Questions for the current round

    public QuizServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(45555);
        System.out.println("Servern är igång på port 45555");

        while (true) {
            System.out.println("Väntar på en klient...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Klient ansluten från: " + clientSocket.getInetAddress());
            handleClient(clientSocket); // Handle client connection
        }
    }

    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                out.println("Välkommen till QuizServer!");
                out.println("Välj kategori (Historia/Geografi/Math):");


                boolean continueGame = true;
                while (continueGame) {
                    // Prompt client for category
                    String category = in.readLine().trim();

                    // Validate category and load questions
                    if (!isValidCategory(category)) {
                        out.println("INVALID_CATEGORY: Ogiltig kategori. Servern använder standardkategorin.");
                        category = DEFAULT_CATEGORY; // Fallback to default category
                    }

                    loadQuestionsForCategory(category);

                    if (questions.isEmpty()) {
                        out.println("INTE_TILLRÄCKLIGA_FRÅGOR: Inga frågor hittades i denna kategori.");
                        return; // Exit if no questions are loaded
                    }

                    // Run the quiz for the selected category
                    int score = 0;
                    for (Question question : questions) {
                        out.println("QUESTION: " + question.getQuestion());

                        // Send options to the client
                        String[] options = question.getOptions();
                        for (int i = 0; i < options.length; i++) {
                            out.println((i + 1) + ". " + options[i]);
                        }

                        // Get client's answer and evaluate
                        String clientResponse = in.readLine();
                        if (clientResponse != null) {
                            int answerIndex = Integer.parseInt(clientResponse);
                            if (answerIndex == question.getCorrectAnswer()) {
                                out.println("Rätt svar!");
                                score++;
                            } else {
                                out.println("Fel svar! Rätt svar var: " + options[question.getCorrectAnswer() - 1]);
                            }
                        }
                    }

                    // Send score and ask if the client wants another round
                    out.println("Rundan är slut! Du fick " + score + " poäng.");
                    out.println("Vill du spela igen? (ja/nej): ");
                    String playAgain = in.readLine();
                    if (playAgain == null || !playAgain.equalsIgnoreCase("ja")) {
                        continueGame = false;
                    }
                }

            } catch (IOException e) {
                System.out.println("Fel vid hantering av klient: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close(); // Ensure the socket is closed
                } catch (IOException e) {
                    System.out.println("Kunde inte stänga klientanslutningen: " + e.getMessage());
                }
            }
        }).start();
    }

    private boolean isValidCategory(String category) {
        // Check if a properties file exists for the selected category
        File categoryFile = new File(CATEGORY_DIRECTORY + category + ".properties");
        return categoryFile.exists();
    }

    private void loadQuestionsForCategory(String category) throws IOException {
        questions = new ArrayList<>(); // Clear questions from the previous round
        String categoryFilePath = CATEGORY_DIRECTORY + category + ".properties";
        QuestionManager questionManager = new QuestionManager(categoryFilePath);
        questions = questionManager.getQuestions();
        System.out.println("Laddade " + questions.size() + " frågor för kategorin: " + category);
    }

    public static void main(String[] args) {
        try {
            new QuizServer();
        } catch (IOException e) {
            System.err.println("Kunde inte starta servern: " + e.getMessage());
        }
    }
}
