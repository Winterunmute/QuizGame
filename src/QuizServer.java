import java.io.*;
import java.net.*;
import java.util.List;

public class QuizServer {
    private InetAddress ip = InetAddress.getByName("127.0.0.1");
    private int portNum = 45555;
    private List<Question> questionBank;
    QuestionManager questionManager = new QuestionManager("src/questions.properties");

    public QuizServer() throws IOException {
        // Ladda frågebanken
        QuestionManager questionManager = new QuestionManager("src/questions.properties");
        questionBank = questionManager.getQuestion("Historia");

        // Starta servern
        ServerSocket serverSocket = new ServerSocket(portNum);
        System.out.println("Servern är igång på port " + portNum);

        while (true) {
            System.out.println("Väntar på en klient...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Klient ansluten från: " + clientSocket.getInetAddress());

            // Hantera klienten
            handleClient(clientSocket);
        }
    }

    // Metod för att hantera en klient
    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                out.println("Välkommen till QuizServer!");
                int score = 0;

                // Skicka frågor och ta emot svar
                for (Question question : questionBank) {
                    out.println("QUESTION: " + question.getQuestion());
                    String[] options = question.getOptions();
                    for (int i = 0; i < options.length; i++) {
                        out.println((i + 1) + ". " + options[i]);
                    }

                    String clientResponse = in.readLine();
                    if (clientResponse != null) {
                        int answerIndex = Integer.parseInt(clientResponse);
                        if (answerIndex == question.getCorrectAnswer()) {
                            out.println("Rätt svar!");
                            score++;
                        } else {
                            out.println("Fel svar! Rätt svar är: " + options[question.getCorrectAnswer() - 1]);
                        }
                    }
                }

                // Skicka slutresultat
                out.println("Spelet är slut! Du fick " + score + " poäng.");
            } catch (IOException e) {
                System.out.println("Klientfel: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Kunde inte stänga klientanslutning: " + e.getMessage());
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        try {
            new QuizServer();
        } catch (IOException e) {
            System.err.println("Kunde inte starta servern: " + e.getMessage());
        }
    }
}
