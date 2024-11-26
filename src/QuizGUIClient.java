import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class QuizGUIClient extends AppGUI {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;
    private int currentRound = 1;

    public QuizGUIClient() {
        super(); // Initierar GUI-komponenterna
        System.out.println("QuizGUIClient startar"); // Felsökningslogg

        // Anslut till servern
        connectToServer();

        // Starta en tråd för att lyssna på serverns meddelanden
        new Thread(new ServerListener()).start();
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 45555);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Ansluten till servern!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Kunde inte ansluta till servern: " + e.getMessage(),
                    "Anslutningsfel", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    @Override
    protected void handleAnswerSelected(int answerIndex) {
        out.println(answerIndex);
        System.out.println("Skickade svar: " + answerIndex);
        // Inaktivera knapparna tills servern svarar
        enableAnswerButtons(false);
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println("Mottog meddelande från servern: " + serverMessage);
                    handleServerMessage(serverMessage);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(QuizGUIClient.this,
                        "Anslutningen till servern förlorad: " + e.getMessage(),
                        "Kommunikationsfel", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }

    private void handleServerMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Hantera servermeddelande: " + message);

            if (message.equals("ENTER_NAME")) {
                // Hantera serverns begäran om spelarens namn
                playerName = JOptionPane.showInputDialog(null, "Ange ditt namn:");
                if (playerName == null || playerName.trim().isEmpty()) {
                    playerName = "Anonym";
                }
                out.println(playerName);
                System.out.println("Skickade namn: " + playerName);

                // Uppdatera spelaretiketten
                setPlayerName(playerName);
            } else if (message.equals("WAIT")) {
                // Det är inte spelarens tur
                updateQuestion("Väntar på din tur...");
                enableAnswerButtons(false);
                System.out.println("Knapparna inaktiverade. Väntar på din tur.");
            } else if (message.equals("YOUR_TURN")) {
                // Det är spelarens tur
                enableAnswerButtons(true);
                System.out.println("Svarsknapparna är aktiverade.");
            } else if (message.startsWith("Fråga:")) {
                String questionText = message.substring(6).trim();
                updateQuestion(questionText);
                System.out.println("Visar fråga: " + questionText);
            } else if (message.startsWith("Alternativ:")) {
                String optionsString = message.substring(10).trim();
                String[] options = optionsString.split("\\|");
                updateOptions(options);
                System.out.println("Visar alternativ: " + String.join(", ", options));
            } else if (message.startsWith("Rätt svar!") || message.startsWith("Fel svar!")) {
                // Visa feedback och hantera färgmarkering av knappar
                String[] parts = message.split("\\|");
                String feedback = parts[0];
                int selectedIndex = Integer.parseInt(parts[1].split(":")[1]) - 1; // 0-index
                int correctIndex = Integer.parseInt(parts[2].split(":")[1]) - 1; // 0-index

                showFeedback(feedback);
                System.out.println("Feedback: " + feedback);

                if (feedback.startsWith("Rätt svar!")) {
                    highlightCorrectAnswer(correctIndex);
                } else {
                    highlightWrongAnswer(selectedIndex, correctIndex);
                }

                // Uppdatera rundaantalet om nödvändigt
                setRoundNumber(currentRound);
                currentRound++;

                // Återställ knapparna efter en kort fördröjning
                Timer timer = new Timer(1000, e -> resetAnswerButtons());
                timer.setRepeats(false);
                timer.start();
            } else if (message.equals("GAME_OVER")) {
                // Spelet är över
                System.out.println("Spelet är över.");
            } else if (message.startsWith("RESULTS:")) {
                // Hantera slutresultatet
                String resultsString = message.substring(8).trim();
                String[] playerResults = resultsString.split("\\|");
                StringBuilder resultsMessage = new StringBuilder("<html><h1>Spelet är slut! Slutresultat:</h1><ul>");
                for (String playerResult : playerResults) {
                    if (!playerResult.isEmpty()) {
                        String[] partsResult = playerResult.split(":");
                        resultsMessage.append("<li>").append(partsResult[0])
                                .append(": ").append(partsResult[1]).append(" poäng</li>");
                    }
                }
                resultsMessage.append("</ul></html>");
                JOptionPane.showMessageDialog(this, resultsMessage.toString(), "Slutresultat", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            } else if (message.equals("GAME_START")) {
                // Spelet har startat
                showPanel("Question");
                System.out.println("Spelet har startat.");
            } else if (message.equals("CHOOSE_CATEGORY")) {
                // Om spelaren är värd, fråga efter kategori
                String[] categories = {"Historia", "Geografi", "Sport", "Kemi"};
                String category = (String) JOptionPane.showInputDialog(null, "Välj kategori:", "Kategori",
                        JOptionPane.PLAIN_MESSAGE, null, categories, categories[0]); // Ändrat till null
                if (category != null) {
                    out.println(category);
                    System.out.println("Skickade kategori: " + category);
                }
            } else if (message.equals("CHOOSE_ROUNDS")) {
                // Om spelaren är värd, fråga efter antal ronder
                String roundsStr = JOptionPane.showInputDialog(null, "Ange antal ronder (1-5):", "3"); // Ändrat till null
                if (roundsStr != null) {
                    try {
                        int rounds = Integer.parseInt(roundsStr);
                        if (rounds < 1 || rounds > 5) {
                            throw new NumberFormatException("Antal ronder måste vara mellan 1 och 5.");
                        }
                        out.println(roundsStr);
                        System.out.println("Skickade antal ronder: " + roundsStr);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Ogiltigt antal ronder. Vänligen ange ett tal mellan 1 och 5.",
                                "Ogiltig inmatning", JOptionPane.ERROR_MESSAGE);
                        // Skicka standardvärdet om inmatningen är ogiltig
                        out.println("3");
                        System.out.println("Skickade standard antal ronder: 3");
                    }
                }
            } else if (message.startsWith("CATEGORY:")) {
                // Mottagning av kategori från värden
                String category = message.substring(9).trim();
                // Uppdatera GUI:t om det behövs, t.ex., visa aktuell kategori
                System.out.println("Mottog kategori: " + category);
            } else if (message.startsWith("ROUNDS:")) {
                // Mottagning av antal ronder från värden
                String roundsStr = message.substring(7).trim();
                try {
                    int rounds = Integer.parseInt(roundsStr);
                    // Uppdatera GUI:t om det behövs, t.ex., visa aktuellt antal ronder
                    System.out.println("Mottog antal ronder: " + rounds);
                } catch (NumberFormatException e) {
                    System.err.println("Ogiltigt antal ronder mottaget: " + roundsStr + ". Använder standardvärdet 3.");
                }
            } else {
                // Hantera oväntade meddelanden
                System.out.println("Oväntat meddelande från servern: " + message);
                JOptionPane.showMessageDialog(this, "Oväntat meddelande från servern: " + message,
                        "Fel", JOptionPane.ERROR_MESSAGE);
            }
        }); // Korrekt avslutning av lambda-uttrycket
    } // Avslutning av handleServerMessage-metoden

    public static void main(String[] args) {
        new QuizGUIClient();
    }
}
