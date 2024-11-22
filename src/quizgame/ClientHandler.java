package quizgame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final List<Question> questionBank;


    public ClientHandler(Socket clientSocket, List<Question> questionBank) {
        this.clientSocket = clientSocket;
        this.questionBank = questionBank;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            // Välkomna spelaren
            out.println("Välkommen till QuizServer!");

            // Spela spelet
            playGame(out, in);

        } catch (IOException e) {
            System.err.println("Fel vid hantering av klient: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Kunde inte stänga klientanslutning: " + e.getMessage());
            }
        }
    }

    private void playGame(PrintWriter out, BufferedReader in) throws IOException {
        int score = 0;

        // Skicka frågor och ta emot svar
        for (Question question : questionBank) {
            out.println("QUESTION: " + question.getQuestion());
            String[] options = question.getOptions();

            // Skicka alternativen till klienten
            for (int i = 0; i < options.length; i++) {
                out.println((i + 1) + ". " + options[i]);
            }

            // Ta emot spelarens svar
            String clientResponse = in.readLine();
            if (clientResponse != null) {
                try {
                    int answerIndex = Integer.parseInt(clientResponse);
                    if (answerIndex == question.getCorrectAnswer()) {
                        out.println("Rätt svar!");
                        score++;
                    } else {
                        out.println("Fel svar! Rätt svar är: " + options[question.getCorrectAnswer() - 1]);
                    }
                } catch (NumberFormatException e) {
                    out.println("Ogiltigt svar. Du får ingen poäng.");
                }
            }
        }

        // Skicka slutresultat
        out.println("Spelet är slut! Du fick " + score + " poäng.");
    }


}

